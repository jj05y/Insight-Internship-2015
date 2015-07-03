package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.coremedia.iso.IsoFile;
import com.shimmerresearch.MultiShimmerRecordReview.Activities.SaveDialog;
import com.shimmerresearch.MultiShimmerRecordReview.Util.CameraPreview;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.Util.DataBaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.Util.Detail;
import com.shimmerresearch.MultiShimmerRecordReview.Util.SplitVideo;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class RecordFragment extends Fragment implements View.OnTouchListener {

    private final static int SAMPLE_SIZE = 30;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;
    private static final String MEDIA_DIR = "/SessionRecordings/";
    private static final int SAVE_TO_DB = 7;

    private View myInflatedView;

    private Linker linker;
    HashMap<String, Shimmer> shimmers;
    HashMap<String, Boolean> isConnected;
    private TextView battText;
    private Button startStreamingButton;

    private XYPlot plot;
    private SimpleXYSeries accellSeries;
    private int indexOfFirstVisibilePoint;

    private PointF firstFinger;

    private ArrayList<Double> points;

    private Camera c;
    private CameraPreview preview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording; //used only for starting/stopping media recorder
    private boolean isStreaming;

    private FrameLayout surface;

    private DataBaseHandler db;
    private String storedFileName;

    public RecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_record, container, false);

        linker = (Linker) getActivity();
        shimmers = linker.getShimmersMap();
        isConnected = linker.getIsConnectedMap();
        db = linker.getDb();

        battText = (TextView) myInflatedView.findViewById(R.id.text_record);

        points = new ArrayList<Double>();

        surface = (FrameLayout) myInflatedView.findViewById(R.id.camera_preview);

        //isPlotting = linker.toggleIsPlotting();
        isStreaming = false;


        //////////////////////////
        // Plot
        //////////////////////////

        plot = (XYPlot) myInflatedView.findViewById(R.id.xyPlot);

        accellSeries = new SimpleXYSeries("LT");
        accellSeries.useImplicitXVals();

        plot.addSeries(accellSeries, new LineAndPointFormatter(Color.BLACK, Color.BLUE, null, null));


        plot.setRangeBoundaries(-20, 30, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, 35, BoundaryMode.FIXED);
        plot.setOnTouchListener(this);
        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(1);
        plot.setRangeStepValue(11);
        plot.setDomainLabel("");
        plot.setRangeLabel("m/s^2");


        ////////////////////// Plot




        Button startPlottingAndSaving = (Button) myInflatedView.findViewById(R.id.button_start_record);
        startStreamingButton = (Button) myInflatedView.findViewById(R.id.button_start_streaming);

        startStreamingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linker.getIsPlotting()) {
                    startPlottingAndSaving.performClick();
                }
                if (allConnected()) {
                    if (!isStreaming) {
                        shimmers.get(ConnectFragment.LEFT_THIGH).startStreaming();
                        isStreaming = true;
                        startStreamingButton.setText("Stop Streaming");
                    } else {
                        shimmers.get(ConnectFragment.LEFT_THIGH).stopStreaming();
                        isStreaming = false;
                        startStreamingButton.setText("Start Streaming");
                        Log.d(null, "Plot of size" + points.size());
                    }
                } else {
                    Toast.makeText(getActivity(), "Connect All Shimmers before starting streaming", Toast.LENGTH_SHORT).show();
                }
                redrawShortly();
            }

        });

        startPlottingAndSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStreaming) {
                    startStopRecording();
                    if (!linker.getIsPlotting()) {
                        reset();
                        linker.toggleIsPlotting();
                        startPlottingAndSaving.setText("Stop Recording");
                    } else {
                        linker.toggleIsPlotting();
                        startPlottingAndSaving.setText("Start Recording");
                    }
                } else {
                    Toast.makeText(getActivity(), "Start Streaming First", Toast.LENGTH_SHORT).show();
                }
                redrawShortly();
            }
        });

        Button saveButton = (Button) myInflatedView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (!linker.getIsPlotting() && points.size() != 0) {

                    Intent i = new Intent(getActivity(), SaveDialog.class);
                    startActivityForResult(i, SAVE_TO_DB);

                } else {
                    Toast.makeText(getActivity(), "Plot something first", Toast.LENGTH_SHORT).show();

                }
                redrawShortly();
            }
        });

        reset();


        return myInflatedView;
    }

    private boolean allConnected() {
        if (!isConnected.get(ConnectFragment.LEFT_THIGH)) return false;
        //todo more sensors here!
        return true;
    }


    public void setBattText(String s) {
        if (battText != null) {
            battText.setText(s);
        }
    }

    private void startStopRecording() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            c.lock();         // take camera access back from MediaRecorder
            // inform the user that recording has stopped
            isRecording = false;
        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }

    }
    private boolean prepareVideoRecorder() {

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        c.unlock();
        mMediaRecorder.setCamera(c);


        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(preview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(null, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(null, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + MEDIA_DIR);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        storedFileName = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";

        Log.d("storage", "  " + storedFileName);
        mediaFile = new File(storedFileName);

        return mediaFile;
    }

    private void reset() {
        //clear graph
        while (accellSeries.size() != 0) {
            accellSeries.removeFirst();

        }
        //clear points
        points.clear();

        //reset index's of visible points
        indexOfFirstVisibilePoint = 0;
        plot.redraw();


    }

    private void plotTheGraphs(Double d) {

        if (accellSeries.size() > SAMPLE_SIZE) {
            accellSeries.removeFirst();

        }

        accellSeries.addLast(null, d);


        plot.redraw();


    }


    public void addToPoints(Double rms) {
        points.add(rms);
        plotTheGraphs(rms);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        plot.calculateMinMaxVals();
        float minX = plot.getCalculatedMinX().floatValue();
        if (!linker.getIsPlotting()) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // Start gesture
                    firstFinger = new PointF(event.getX(), event.getY());
                    plot.redraw();

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // second finger
                    break;
                case MotionEvent.ACTION_MOVE:
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    int foo = (int) minX;
                    minX += (oldFirstFinger.x - firstFinger.x) / 10;
                    int bar = foo - (int) minX;
                    while (bar >= 1) {
                        //left to right
                        if (points == null || indexOfFirstVisibilePoint <= 0) break;

                        Double d = points.get(indexOfFirstVisibilePoint - 1);
                        accellSeries.removeLast();
                        accellSeries.addFirst(null, d);

                        indexOfFirstVisibilePoint--;
                        bar--;
                    }
                    bar = foo - (int) minX;
                    while (bar <= -1) {
                        //right to left
                        if (points == null || indexOfFirstVisibilePoint + DRAW_SIZE + 1 >= points.size())
                            break;

                        Double d = points.get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                        accellSeries.removeFirst();
                        accellSeries.addLast(null, d);


                        indexOfFirstVisibilePoint++;
                        bar++;
                    }
                    plot.redraw();

                    break;
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        releaseMediaRecorder();
        surface.removeAllViews();
    }
    private void releaseCamera() {
        if (c != null) {
            c.release();        // release the camera for other applications
            c = null;
        }
    }
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            c.lock();           // lock camera for later use
        }
    }

    @Override
    public void onResume() {

        ////////////////////////
        /// Sort Camera
        ////////////////////////


        //todo WHY IS IT SIDE WAYS!?!??!!?!?

        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            c = null;
            try {
                c = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Camera.Parameters p = c.getParameters();
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            p.setRecordingHint(true);
            c.setParameters(p);
            //setCameraDisplayOrientation(getActivity(), c);
            preview = new CameraPreview(getActivity(), c);
            surface.addView(preview);
            c.startPreview();
        }

        /////////////////////////// Camera
        super.onResume();
    }

    public static void setCameraDisplayOrientation(Activity activity, android.hardware.Camera camera) {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(null, "Camera found");
                cameraId = i;
                break;
            }
        }
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void redrawShortly() {
        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                plot.redraw();

            }

            ;

        };
        t.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            putDataInDataBase(data.getStringExtra("name"), data.getStringExtra("exercise"));
            Toast.makeText(getActivity(), "Added to Database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Nothing Saved", Toast.LENGTH_LONG).show();
        }
    }

    private void putDataInDataBase(String name, String exercise) {

        // need to split data into 5

        //then make 5 details,

        //add em all,
        // maybe a list of filenames and points chunks,,
        //how the hell am i gonna get file name?

        double lengthInSeconds = 0;
        try {
            IsoFile isoFile = new IsoFile(storedFileName);
            lengthInSeconds = (double)
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                    isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        } catch (IOException e) {
            e.printStackTrace();
        }
        double segLength = lengthInSeconds / 5;
        Log.d("Times", "len" + lengthInSeconds + " seglen " + segLength);


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = dateFormat.format(new Date());


        for (int i = 0; i < 5; i++) {

            // split vid
            String newFileDir = Environment.getExternalStorageDirectory() + MEDIA_DIR;
            String newFile = Environment.getExternalStorageDirectory() + MEDIA_DIR + date + "-rep" + i + ".mp4";

            new SplitVideo(segLength, segLength * i, storedFileName, date + "-rep" + i, newFileDir).execute();
            Log.d("times", segLength + " " + (segLength*i) );

            //split points
            int len = points.size();
            int seglen = len/5;
            List<Double> foo = points.subList(i*seglen, (i*seglen) + seglen);
            ArrayList<Double> subList = new ArrayList<>();
            for (Double d : foo) {
                subList.add(d);
            }

            //pop in db
            Detail d = new Detail();
            d.setName(name);
            d.setDate(date);
            d.setVideoFile(newFile);
            d.setLabel("No Label");
            d.setExercise(exercise);
            d.setPoints(subList);

            db.addDetail(d);
        }



    }


}
