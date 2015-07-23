package classify.WorkerClasses;

import android.util.Log;

import com.coremedia.iso.IsoFile;

import java.io.IOException;
import java.util.ArrayList;

import classify.ObjectClasses.FilmSection;
import classify.ObjectClasses.FlatSpot;


public class RepFinder {

    private static int REACH = 20;
    private static double UPPER_BOUND = 10.2;
    private static double LOWER_BOUND = 9.6;
    private static int END_CHOP = REACH + 2;
    private static int REP_START_STRETCH = 10;
    private static int REP_END_STRETCH = 20;
    private static double FILM_STRETCH = 1;
    private static double FILM_DELAY = 2.5;

    static public ArrayList<ArrayList<Double>> getReps(ArrayList<FlatSpot> flatSpots, ArrayList<Double> points, int exercise) {

        ArrayList<ArrayList<Double>> repsList = new ArrayList<>();

/*        if (flatSpots.isEmpty()) { //if there's no flat spots,,, return whole graph
            repsList.add(points);
            return repsList;
        }*/

        // chop up graph
        for (int i = 0; i < flatSpots.size() - 1; i++) {

            ArrayList<Double> rep = new ArrayList<>();
            FlatSpot flat = flatSpots.get(i);
            FlatSpot nextFlat = flatSpots.get(i + 1);

            int startOfRep;
            //start of rep is the end of the first flat
            if (flat.getSize() > REP_START_STRETCH) {
                startOfRep = flat.getEnd() - REP_START_STRETCH;
            } else {
                startOfRep = flat.getStart();
            }

            int endOfRep;
            // end of rep is the start of the nextPage flat
            if (nextFlat.getSize() > REP_END_STRETCH) {
                endOfRep = nextFlat.getStart() + REP_END_STRETCH;
            } else {
                endOfRep = nextFlat.getEnd();
            }

            rep.addAll(points.subList(startOfRep, endOfRep + 1));


            repsList.add(rep);

        }

        //if the end point isnt contained in a flat,,,
        if (!flatSpots.get(flatSpots.size() - 1).contains(points.size() - END_CHOP)) {
            //,,,, we need to count the data from the last flat to end

            ArrayList<Double> rep = new ArrayList<>();
            FlatSpot flat = flatSpots.get(flatSpots.size() - 1);

            int startOfRep;
            if (flat.getSize() > REP_START_STRETCH)
                startOfRep = flat.getEnd() - REP_START_STRETCH;
            else
                startOfRep = flat.getStart();

            int endOfRep = points.size() - 1;

            rep.addAll(points.subList(startOfRep, endOfRep + 1));


            repsList.add(rep);


        }


        return repsList;
    }

    static public ArrayList<FlatSpot> findFlatSpots(ArrayList<Double> points) {


        //have all points, now to check,
        ArrayList<FlatSpot> flatSpots = new ArrayList<>();

        for (int i = 0; i < points.size() - REACH; i++) {
            double thisPoint = points.get(i);
            double thatPoint = points.get(i + REACH);

            if (withinBounds(thisPoint) && withinBounds(thatPoint)) {
                //then we have a flat spot.
                //loads of flat spots,,, time to join them,, how,,, need to keep track of them,
                flatSpots.add(new FlatSpot(i, i + REACH));

            }
        }

        //combine flatspots,
        ArrayList<FlatSpot> squishedFlatSpots = new ArrayList<>();

        for (int i = 0; i < flatSpots.size(); i++) {
            int start = flatSpots.get(i).getStart();
            int newEnd = flatSpots.get(i).getEnd();
            // if end is within another flatspot, new End = that flatspots end,

            while (i < flatSpots.size() && flatSpots.get(i).contains(newEnd)) {
                newEnd = flatSpots.get(i).getEnd();
                i++;
            }
            squishedFlatSpots.add(new FlatSpot(start, newEnd));
        }

        return squishedFlatSpots;
    }

    static public ArrayList<FilmSection> getTimes(double sampleRate, ArrayList<FlatSpot> flatSpots, String videoFile) {

        double filmLengthInSeconds = 0;
        try {
            IsoFile isoFile = new IsoFile(videoFile);
            filmLengthInSeconds = (double)
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                    isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("times", "Full Video Length: " + filmLengthInSeconds);
        //now length in seconds is set, its only going  to be used to stop bumping at the end,

        ArrayList<FilmSection> filmSections = new ArrayList<>();

        for (int i = 0; i < flatSpots.size() - 1; i++) {
            FlatSpot flat = flatSpots.get(i);
            FlatSpot nextFlat = flatSpots.get(i + 1);
            // repstart needs to be the start of the first rep
            // aka at the end of the first flat spot
            double repStart = flat.getEnd() / sampleRate;
            double repEnd = nextFlat.getStart() / sampleRate;
            double filmStart;
            double filmEnd;

            //need to incorperate the delay,,, vids are a playing a we bit early,, need to push later
            if (repStart - FILM_STRETCH - FILM_DELAY > 0) {
                filmStart = repStart - FILM_STRETCH - FILM_DELAY;
            } else if (repStart - FILM_DELAY > 0) {
                filmStart = repStart - FILM_DELAY;
            } else {
                filmStart = repStart;
            }

            if (repEnd + FILM_STRETCH - FILM_DELAY < filmLengthInSeconds) {
                filmEnd = repEnd + FILM_STRETCH - FILM_DELAY;
            } else if (repEnd - FILM_DELAY < filmLengthInSeconds) {
                filmEnd = repEnd - FILM_DELAY;
            } else if (repEnd > filmLengthInSeconds) { //this is pretty redundant
                Log.wtf("wtf", "repEnd > filmLengthInSeconds");
                filmEnd = repStart;
            } else {
                filmEnd = repEnd;
            }

            Log.d("times", "Adding film: " + new FilmSection(filmStart, filmEnd));
            filmSections.add(new FilmSection(filmStart, filmEnd));
        }

        return filmSections;
    }


    private static boolean withinBounds(double point) {
        if (point > UPPER_BOUND) return false;
        if (point < LOWER_BOUND) return false;

        return true;
    }
}
