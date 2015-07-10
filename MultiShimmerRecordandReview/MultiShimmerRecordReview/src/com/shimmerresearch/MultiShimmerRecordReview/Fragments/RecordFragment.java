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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.shimmerresearch.MultiShimmerRecordReview.Activities.SaveDialog;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.MiscUtil.CameraPreview;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.Detail;
import com.shimmerresearch.MultiShimmerRecordReview.ObjectClasses.FilmSection;
import com.shimmerresearch.MultiShimmerRecordReview.ObjectClasses.FlatSpot;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.WorkerClasses.RepFinder;
import com.shimmerresearch.MultiShimmerRecordReview.WorkerClasses.SplitVideo;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class RecordFragment extends Fragment {

    private final static int SAMPLE_SIZE = 200;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;
    private static final String MEDIA_DIR = "/SessionRecordings/";
    private static final int SAVE_TO_DB = 7;
    public static final double MOVING_AVG_DEPTH = 20;
    public static final double GRAVITY = 9.8;

    private View myInflatedView;

    private Linker linker;
    HashMap<String, Shimmer> shimmers;
    HashMap<String, Boolean> isConnected;
    private TextView battText; //not used, but could be :)
    private Button streamButton;

    private XYPlot plot;
    private HashMap<String, SimpleXYSeries> accelMagSeriesMap;
    private HashMap<String, SimpleXYSeries> accelXSeriesMap;
    private HashMap<String, SimpleXYSeries> accelYSeriesMap;
    private HashMap<String, SimpleXYSeries> accelZSeriesMap;
    private HashMap<String, SimpleXYSeries> pitchSeriesMap;
    private HashMap<String, SimpleXYSeries> rollSeriesMap;
    private HashMap<String, SimpleXYSeries> yawSeriesMap;


    private HashMap<String, ArrayList<Double>> accelMagPointsMap;
    private HashMap<String, ArrayList<Double>> accelXPointsMap;
    private HashMap<String, ArrayList<Double>> accelYPointsMap;
    private HashMap<String, ArrayList<Double>> accelZPointsMap;
    private HashMap<String, ArrayList<Double>> pitchPointsMap;
    private HashMap<String, ArrayList<Double>> rollPointsMap;
    private HashMap<String, ArrayList<Double>> yawPointsMap;

    HashMap<String, Boolean> signalsMap;
    HashMap<String, Boolean> sensorsMap;

    private Camera c;
    private CameraPreview preview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording; //used only for starting/stopping media recorder
    private boolean isStreaming;

    private FrameLayout surface;

    private DatabaseHandler db;
    private String storedFileName;
    private int plotSize;

    private int indexOfFirstVisibilePoint;
    private PointF firstFinger;

    private Random rand;


    public RecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_record, container, false);

        linker = (Linker) getActivity();
        shimmers = linker.getShimmersMap();
        isConnected = linker.getIsConnectedMap();
        db = linker.getDb();
        rand = new Random();
        signalsMap = linker.getPlotSignalsMap();
        sensorsMap = linker.getPlotSensorsMap();

        indexOfFirstVisibilePoint = 0;

        buildPointsMaps();


        surface = (FrameLayout) myInflatedView.findViewById(R.id.camera_preview);

        isStreaming = false;

        plotSize = 0;

        //////////////////////////
        // Plot
        //////////////////////////

        plot = (XYPlot) myInflatedView.findViewById(R.id.xyPlot);
        buildSeriesMaps();

        plot.setRangeBoundaries(0, 20, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, DRAW_SIZE, BoundaryMode.FIXED);
        //plot.setOnTouchListener(new myTouchListener());
        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(1);
        plot.setRangeStepValue(11);
        plot.setDomainLabel("");
        plot.setRangeLabel("m/s^2");


        ////////////////////// Plot


        Button plotButton = (Button) myInflatedView.findViewById(R.id.button_start_record);
        streamButton = (Button) myInflatedView.findViewById(R.id.button_start_streaming);

        streamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linker.getIsPlotting()) {
                    plotButton.performClick();
                }
                if (atLeastOneConnected()) {
                    Iterator<Map.Entry<String, Shimmer>> it = shimmers.entrySet().iterator();
                    if (!isStreaming) {
                        while (it.hasNext()) {
                            Map.Entry<String, Shimmer> foo = it.next();
                            String key = foo.getKey();
                            Shimmer shimmer = foo.getValue();
                            if (isConnected.get(key)) {
                                shimmer.startStreaming();
                            }
                        }
                        isStreaming = true;
                        streamButton.setText("Stop Streaming");
                    } else {
                        while (it.hasNext()) {
                            Map.Entry<String, Shimmer> foo = it.next();
                            String key = foo.getKey();
                            Shimmer shimmer = foo.getValue();
                            if (isConnected.get(key)) {
                                shimmer.stopStreaming();
                            }
                        }
                        isStreaming = false;
                        streamButton.setText("Start Streaming");
                        Log.d(null, "Plot of size" + plotSize);
                    }
                } else {
                    Toast.makeText(getActivity(), "Connect All Shimmers before starting streaming", Toast.LENGTH_SHORT).show();
                }
                redrawShortly();
            }

        });

        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStreaming) {
                    startStopRecording();
                    if (!linker.getIsPlotting()) {
                        reset();
                        linker.toggleIsPlotting();
                        plotButton.setText("Stop Recording");
                    } else {
                        linker.toggleIsPlotting();
                        plotButton.setText("Start Recording");
                        plotButton.setText("Start Recording");
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
                if (!linker.getIsPlotting() && accelMagPointsMap.get(C.LOWER_BACK).size() != 0) {

                    Intent i = new Intent(getActivity(), SaveDialog.class);
                    startActivityForResult(i, SAVE_TO_DB);

                } else {
                    Toast.makeText(getActivity(), "Plot something first", Toast.LENGTH_SHORT).show();

                }
            }
        });

        Button resetButton = (Button) myInflatedView.findViewById(R.id.button_reset_record);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!linker.getIsPlotting()) {
                    reset();
                } else if (linker.getIsPlotting()) {
                    Toast.makeText(getActivity(), "Stop Recording First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset();


        return myInflatedView;
    }

    private void buildSeriesMaps() {
        accelMagSeriesMap = new HashMap<>();
        accelXSeriesMap = new HashMap<>();
        accelYSeriesMap = new HashMap<>();
        accelZSeriesMap = new HashMap<>();
        pitchSeriesMap = new HashMap<>();
        rollSeriesMap = new HashMap<>();
        yawSeriesMap = new HashMap<>();

        for (String s : C.KEYS) {
            accelMagSeriesMap.put(s, new SimpleXYSeries(s));
            accelMagSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(accelMagSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            accelXSeriesMap.put(s, new SimpleXYSeries(s));
            accelXSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(accelXSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            accelYSeriesMap.put(s, new SimpleXYSeries(s));
            accelYSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(accelYSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            accelZSeriesMap.put(s, new SimpleXYSeries(s));
            accelZSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(accelZSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            pitchSeriesMap.put(s, new SimpleXYSeries(s));
            pitchSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(pitchSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            rollSeriesMap.put(s, new SimpleXYSeries(s));
            rollSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(rollSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
            yawSeriesMap.put(s, new SimpleXYSeries(s));
            yawSeriesMap.get(s).useImplicitXVals();
            plot.addSeries(yawSeriesMap.get(s), new LineAndPointFormatter(randColor(), null, null, null));
        }
    }

    private int randColor() {
        int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return color;
    }

    private void buildPointsMaps() {
        accelMagPointsMap = new HashMap<>();
        accelXPointsMap = new HashMap<>();
        accelYPointsMap = new HashMap<>();
        accelZPointsMap = new HashMap<>();
        pitchPointsMap = new HashMap<>();
        rollPointsMap = new HashMap<>();
        yawPointsMap = new HashMap<>();
        for (String s : C.KEYS) {
            accelMagPointsMap.put(s, new ArrayList<>());
            accelXPointsMap.put(s, new ArrayList<>());
            accelYPointsMap.put(s, new ArrayList<>());
            accelZPointsMap.put(s, new ArrayList<>());
            pitchPointsMap.put(s, new ArrayList<>());
            rollPointsMap.put(s, new ArrayList<>());
            yawPointsMap.put(s, new ArrayList<>());
        }

    }


    private boolean atLeastOneConnected() {
        if (isConnected.get(C.LEFT_THIGH)) return true;
        if (isConnected.get(C.LEFT_CALF)) return true;
        if (isConnected.get(C.RIGHT_THIGH)) return true;
        if (isConnected.get(C.RIGHT_CALF)) return true;
        if (isConnected.get(C.LOWER_BACK)) return true;

        return false;
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

        for (String s : C.KEYS) {
            if (isConnected.get(s)) {
                //clear points
                accelMagPointsMap.get(s).clear();
                accelXPointsMap.get(s).clear();
                accelYPointsMap.get(s).clear();
                accelZPointsMap.get(s).clear();
                pitchPointsMap.get(s).clear();
                rollPointsMap.get(s).clear();
                yawPointsMap.get(s).clear();

                //clear graph
                while (accelMagSeriesMap.get(s).size() > 0) accelMagSeriesMap.get(s).removeFirst();
                while (accelXSeriesMap.get(s).size() > 0) accelXSeriesMap.get(s).removeFirst();
                while (accelYSeriesMap.get(s).size() > 0) accelYSeriesMap.get(s).removeFirst();
                while (accelZSeriesMap.get(s).size() > 0) accelZSeriesMap.get(s).removeFirst();
                while (pitchSeriesMap.get(s).size() > 0) pitchSeriesMap.get(s).removeFirst();
                while (rollSeriesMap.get(s).size() > 0) rollSeriesMap.get(s).removeFirst();
                while (yawSeriesMap.get(s).size() > 0) yawSeriesMap.get(s).removeFirst();


            }
        }


        //reset index's of visible points
        indexOfFirstVisibilePoint = 0;
        plot.redraw();


    }

    public void addToPoints(double value, String key, String signalType) {

        switch (signalType) {
            case C.ACCEL_MAG:
                accelMagPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.ACCEL_MAG)) {
                    if (accelMagSeriesMap.get(key).size() > SAMPLE_SIZE)
                        accelMagSeriesMap.get(key).removeFirst();
                    accelMagSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.ACCEL_X:
                accelXPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.ACCEL_X)) {
                    if (accelXSeriesMap.get(key).size() > SAMPLE_SIZE)
                        accelXSeriesMap.get(key).removeFirst();
                    accelXSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.ACCEL_Y:
                accelYPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.ACCEL_Y)) {
                    if (accelYSeriesMap.get(key).size() > SAMPLE_SIZE)
                        accelYSeriesMap.get(key).removeFirst();
                    accelYSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.ACCEL_Z:
                accelZPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.ACCEL_Z)) {
                    if (accelZSeriesMap.get(key).size() > SAMPLE_SIZE)
                        accelZSeriesMap.get(key).removeFirst();
                    accelZSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.PITCH:
                pitchPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.PITCH)) {
                    if (pitchSeriesMap.get(key).size() > SAMPLE_SIZE)
                        pitchSeriesMap.get(key).removeFirst();
                    pitchSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.ROLL:
                rollPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.ROLL)) {
                    if (rollSeriesMap.get(key).size() > SAMPLE_SIZE)
                        rollSeriesMap.get(key).removeFirst();
                    rollSeriesMap.get(key).addLast(null, value);
                }
                break;
            case C.YAW:
                yawPointsMap.get(key).add(value);
                if (sensorsMap.get(key) && signalsMap.get(C.YAW)) {
                    if (yawSeriesMap.get(key).size() > SAMPLE_SIZE)
                        yawSeriesMap.get(key).removeFirst();
                    yawSeriesMap.get(key).addLast(null, value);
                }
                break;
        }

        plot.redraw();



    }



/*    private class myTouchListener implements View.OnTouchListener {

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
                        minX += (oldFirstFinger.x - firstFinger.x) / 5;
                        int bar = foo - (int) minX;
                        while (bar >= 1) {
                            //left to right
                            if (plotSize == 0 || indexOfFirstVisibilePoint <= 0)
                                break;

                            if (isConnected.get(C.LEFT_THIGH)) {
                                Double d = pointsMap.get(C.LEFT_THIGH).get(indexOfFirstVisibilePoint - 1);
                                seriesMap.get(C.LEFT_THIGH).removeLast();
                                seriesMap.get(C.LEFT_THIGH).addFirst(null, d);
                            }
                            if (isConnected.get(C.LEFT_CALF)) {
                                Double d = pointsMap.get(C.LEFT_CALF).get(indexOfFirstVisibilePoint - 1);
                                seriesMap.get(C.LEFT_CALF).removeLast();
                                seriesMap.get(C.LEFT_CALF).addFirst(null, d);
                            }
                            if (isConnected.get(C.RIGHT_THIGH)) {
                                Double d = pointsMap.get(C.RIGHT_THIGH).get(indexOfFirstVisibilePoint - 1);
                                seriesMap.get(C.RIGHT_THIGH).removeLast();
                                seriesMap.get(C.RIGHT_THIGH).addFirst(null, d);
                            }
                            if (isConnected.get(C.RIGHT_CALF)) {
                                Double d = pointsMap.get(C.RIGHT_CALF).get(indexOfFirstVisibilePoint - 1);
                                seriesMap.get(C.RIGHT_CALF).removeLast();
                                seriesMap.get(C.RIGHT_CALF).addFirst(null, d);
                            }
                            if (isConnected.get(C.LOWER_BACK)) {
                                Double d = pointsMap.get(C.LOWER_BACK).get(indexOfFirstVisibilePoint - 1);
                                seriesMap.get(C.LOWER_BACK).removeLast();
                                seriesMap.get(C.LOWER_BACK).addFirst(null, d);
                            }


                            indexOfFirstVisibilePoint--;
                            bar--;
                        }
                        bar = foo - (int) minX;
                        while (bar <= -1) {
                            //right to left
                            if (pointsMap.get(C.LEFT_THIGH) == null || indexOfFirstVisibilePoint + DRAW_SIZE + 1 >= pointsMap.get(C.LEFT_THIGH).size())
                                break;

                            if (isConnected.get(C.LEFT_THIGH)) {
                                Double d = pointsMap.get(C.LEFT_THIGH).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                seriesMap.get(C.LEFT_THIGH).removeFirst();
                                seriesMap.get(C.LEFT_THIGH).addLast(null, d);
                            }
                            if (isConnected.get(C.LEFT_CALF)) {
                                Double d = pointsMap.get(C.LEFT_CALF).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                seriesMap.get(C.LEFT_CALF).removeFirst();
                                seriesMap.get(C.LEFT_CALF).addLast(null, d);
                            }
                            if (isConnected.get(C.RIGHT_THIGH)) {
                                Double d = pointsMap.get(C.RIGHT_THIGH).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                seriesMap.get(C.RIGHT_THIGH).removeFirst();
                                seriesMap.get(C.RIGHT_THIGH).addLast(null, d);
                            }
                            if (isConnected.get(C.RIGHT_CALF)) {
                                Double d = pointsMap.get(C.RIGHT_CALF).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                seriesMap.get(C.RIGHT_CALF).removeFirst();
                                seriesMap.get(C.RIGHT_CALF).addLast(null, d);
                            }
                            if (isConnected.get(C.LOWER_BACK)) {
                                Double d = pointsMap.get(C.LOWER_BACK).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                seriesMap.get(C.LOWER_BACK).removeFirst();
                                seriesMap.get(C.LOWER_BACK).addLast(null, d);
                            }


                            indexOfFirstVisibilePoint++;
                            bar++;
                        }
                        plot.redraw();

                        break;
                }
            }
            return true;
        }

    }*/

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
        };
        t.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            putDataInDataBase(data.getStringExtra("name"), data.getIntExtra("exercise", 0));
            Toast.makeText(getActivity(), "Added to Database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Nothing Saved", Toast.LENGTH_LONG).show();
        }
    }

    private void putDataInDataBase(String name, int exercise) {


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = dateFormat.format(new Date());

        ArrayList<FlatSpot> flatSpots = RepFinder.findFlatSpots(accelMagPointsMap.get(C.LOWER_BACK)); //todo need to get lumbar
        ArrayList<ArrayList<Double>> repsList = RepFinder.getReps(flatSpots, accelMagPointsMap.get(C.LOWER_BACK), exercise);
        ArrayList<FilmSection> filmSections = RepFinder.getTimes(C.SAMPLE_RATE, flatSpots, storedFileName);

        for (int i = 0; i < repsList.size(); i++) {

            // split vid
            String newFileDir = Environment.getExternalStorageDirectory() + MEDIA_DIR;
            String newFile = Environment.getExternalStorageDirectory() + MEDIA_DIR + date + "-rep" + i + ".mp4";

            //split
            double segLength = filmSections.get(i).getLength();
            new SplitVideo(segLength, filmSections.get(i).getStartTime(), storedFileName, date + "-rep" + i, newFileDir).execute();

            //split points


            HashMap<String, ArrayList<Double>> filteredAccelMag = filterAllValues(accelMagPointsMap);
            HashMap<String, ArrayList<Double>> choppedAccelMag = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredAccelMag.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedAccelMag.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredAccelX = filterAllValues(accelXPointsMap);
            HashMap<String, ArrayList<Double>> choppedAccelX = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredAccelX.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedAccelX.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredAccelY = filterAllValues(accelYPointsMap);
            HashMap<String, ArrayList<Double>> choppedAccelY = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredAccelY.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedAccelY.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredAccelZ = filterAllValues(accelZPointsMap);
            HashMap<String, ArrayList<Double>> choppedAccelZ = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredAccelZ.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedAccelZ.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredPitch = filterAllValues(pitchPointsMap);
            HashMap<String, ArrayList<Double>> choppedPitch = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredPitch.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedPitch.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredRoll = filterAllValues(rollPointsMap);
            HashMap<String, ArrayList<Double>> choppedRoll = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredRoll.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedRoll.put(key, tempReps.get(i));
                }
            }
            HashMap<String, ArrayList<Double>> filteredYaw = filterAllValues(yawPointsMap);
            HashMap<String, ArrayList<Double>> choppedYaw = new HashMap<>();
            for (String key: C.KEYS) {
                if (isConnected.get(key)) {
                    ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filteredYaw.get(key), exercise);
                    //all reps are calc'd on the lumbars flat spots
                    choppedYaw.put(key, tempReps.get(i));
                }
            }

            //pop in db
            Detail d = new Detail();
            d.setName(name);
            d.setDate(date);
            d.setVideoFile(newFile);
            d.setLabel(0);
            d.setExercise(exercise);
            d.setAccelMagPoints(choppedAccelMag);
            d.setAccelXPoints(choppedAccelX);
            d.setAccelYPoints(choppedAccelY);
            d.setAccelZPoints(choppedAccelZ);
            d.setPitchPoints(choppedPitch);
            d.setRollPoints(choppedRoll);
            d.setYawPoints(choppedYaw);
            d.setRep(i + 1);

            db.addDetail(d);
        }

        //at this point, all data is in db,,, can delete orginal file :/ eek
        File file = new File(storedFileName);
        file.delete();

    }

    private HashMap<String, ArrayList<Double>> filterAllValues(HashMap<String, ArrayList<Double>> pointsMap) {

        HashMap<String, ArrayList<Double>> filteredVals = new HashMap<>();

        Iterator<String> it = pointsMap.keySet().iterator();
        while (it.hasNext()) {

            //make new arraylist for the filtered points
            String key = it.next();
            ArrayList<Double> foo = new ArrayList<>();

            //filter points
            for (int i = 0; i < pointsMap.get(key).size(); i++) {
                if (i < MOVING_AVG_DEPTH) {
                    foo.add(GRAVITY);
                } else {
                    double sum = 0;
                    for (int k = 0; k < MOVING_AVG_DEPTH; k++) {
                        sum += pointsMap.get(key).get(i - k);
                    }
                    foo.add(sum / MOVING_AVG_DEPTH);
                }
            }

            //add to hash map
            filteredVals.put(key, foo);

        }


        return filteredVals;
    }


}
