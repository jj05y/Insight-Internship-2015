package classify.WorkerClasses;


import android.os.AsyncTask;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import classify.Interfaces.VideoSplitCallBackListener;

public class SplitVideo extends AsyncTask<String, Integer, String> {
    private String mediaPath;
    private String newFileName;
    private double startTime;
    private double endTime;
    private double length;
    private String newfileDir;
    private VideoSplitCallBackListener videoSplitCallBackListener;

    public SplitVideo(double length, double startTime, String mediaPath, String newFileName, String newFileDir, VideoSplitCallBackListener videoSplitCallBackListener) {
        this.length = length;
        this.startTime = startTime;
        this.mediaPath = mediaPath;
        this.newFileName = newFileName;
        this.endTime = this.startTime + this.length;
        this.newfileDir = newFileDir;
        this.videoSplitCallBackListener = videoSplitCallBackListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        trimVideo();
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        videoSplitCallBackListener.onVideoSplitComplete();
        super.onPostExecute(result);
    }

    private void trimVideo() {
        Log.d(null, "going for " + startTime + " to " + endTime);

        Movie movie = null;
        File file = new File(mediaPath);
        if (file.exists()) {
            try {
                movie = MovieCreator.build(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());
        // remove all tracks we will create new tracks from the old


        boolean timeCorrected = false;

        // Here we try to find a track that has sync samples. Since we can only start decoding
        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
        // such a frame
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)

                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTime = correctTimeToSyncSample(track, startTime, false);
                endTime = correctTimeToSyncSample(track, endTime, true);

                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = -1;
            long startSample1 = -1;
            long endSample1 = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= startTime) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample;
                }

                lastTime = currentTime;
                currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            try {
                movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Container out = new DefaultMp4Builder().build(movie);
            File outFile = new File(newfileDir, newFileName + ".mp4");
            Log.d("storage", "saving too" + outFile.toString());
            FileChannel fc = new RandomAccessFile(outFile, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(null, "Chopped");

    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }
}