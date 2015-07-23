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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.shimmerresearch.MultiShimmerRecordReview.Activities.SaveDialog;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.Detail;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.VideoSplitCallBackListener;
import com.shimmerresearch.MultiShimmerRecordReview.MiscUtil.CameraPreview;
import com.shimmerresearch.MultiShimmerRecordReview.ObjectClasses.FilmSection;
import com.shimmerresearch.MultiShimmerRecordReview.ObjectClasses.FlatSpot;
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


public class RecordFragment extends Fragment implements VideoSplitCallBackListener {

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

    private Button streamButton;
    private Button plotButton;
    private Button resetButton;
    private Button saveButton;

    private XYPlot plot;
    private HashMap<String, HashMap<String, SimpleXYSeries>> allSeriesMap;
    private HashMap<String, HashMap<String, ArrayList<Double>>> allPointsMap;

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

    private int indexOfFirstVisibilePoint;
    private PointF firstFinger;

    private Random rand;
    private int numberOfVidsDone;
    private int numberofVidsToDo;



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
        numberOfVidsDone = 0;

        buildPointsMaps();
        buildSeriesMaps();

        surface = (FrameLayout) myInflatedView.findViewById(R.id.camera_preview);

        isStreaming = false;


        //////////////////////////
        // Plot
        //////////////////////////

        plot = (XYPlot) myInflatedView.findViewById(R.id.xyPlot);


        plot.setRangeBoundaries(0, 20, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, DRAW_SIZE, BoundaryMode.FIXED);
        plot.setOnTouchListener(new myTouchListener());
        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(1);
        plot.setRangeStepValue(11);
        plot.setDomainLabel("");
        plot.setRangeLabel("m/s^2");


        ////////////////////// Plot


        plotButton = (Button) myInflatedView.findViewById(R.id.button_start_record);
        streamButton = (Button) myInflatedView.findViewById(R.id.button_start_streaming);
        saveButton = (Button) myInflatedView.findViewById(R.id.button_save);
        resetButton = (Button) myInflatedView.findViewById(R.id.button_reset_record);

        //decativate buttons
        plotButton.setEnabled(false);
        saveButton.setEnabled(false);
        resetButton.setEnabled(false);

        streamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linker.getIsPlotting()) {
                    plotButton.performClick();
                }
                if (isLowerBackConnected()) {
                    plotButton.setEnabled(true);
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
                            plotButton.setEnabled(false);
                        }
                        isStreaming = false;
                        streamButton.setText("Start Streaming");
                    }
                } else {
                    Toast.makeText(getActivity(), "Lower back sensor must be connected", Toast.LENGTH_SHORT).show();
                }
            }

        });

        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStreaming) {
                    startStopRecording();
                    if (!linker.getIsPlotting()) {
                        Iterator<XYSeries> it = plot.getSeriesSet().iterator();
                        while (it.hasNext()) {
                            SimpleXYSeries s = (SimpleXYSeries) it.next();
                            plot.removeSeries(s);
                        }
                        addSignalsToPlot();
                        reset();
                        linker.toggleIsPlotting();
                        plotButton.setText("Stop Recording");
                    } else {
                        linker.toggleIsPlotting();
                        plotButton.setText("Start Recording");
                        plotButton.setText("Start Recording");
                        saveButton.setEnabled(true);
                        resetButton.setEnabled(true);
                        plotButton.setEnabled(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "Start Streaming First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (!linker.getIsPlotting() && allPointsMap.get(C.ACCEL_MAG).get(C.LOWER_BACK).size() != 0) {

                    Intent i = new Intent(getActivity(), SaveDialog.class);
                    startActivityForResult(i, SAVE_TO_DB);
                    //save deactivated from on activity result
                } else {
                    Toast.makeText(getActivity(), "Plot something first", Toast.LENGTH_SHORT).show();

                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!linker.getIsPlotting()) {
                    plotButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    reset();
                } else if (linker.getIsPlotting()) {
                    Toast.makeText(getActivity(), "Stop Recording First", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return myInflatedView;
    }


    private void addSignalsToPlot() {
        //signals selected to be drawn on the signals fragment are added to the plot
        for (String sensor : C.SENSORS) {
            for (String signal : C.SIGNALS) {
                if (sensorsMap.get(sensor) && signalsMap.get(signal)) {
                    allSeriesMap.get(signal).put(sensor, new SimpleXYSeries(sensor + "-" + signal));
                    allSeriesMap.get(signal).get(sensor).useImplicitXVals();
                    plot.addSeries(allSeriesMap.get(signal).get(sensor), new LineAndPointFormatter(randColor(), null, null, null));
                }
            }
        }

    }


    private int randColor() {
        int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return color;
    }

    private void buildSeriesMaps() {
        allSeriesMap = new HashMap<>();
        allSeriesMap.put(C.ACCEL_MAG, new HashMap<>());
        allSeriesMap.put(C.ACCEL_X, new HashMap<>());
        allSeriesMap.put(C.ACCEL_Y, new HashMap<>());
        allSeriesMap.put(C.ACCEL_Z, new HashMap<>());
        allSeriesMap.put(C.PITCH, new HashMap<>());
        allSeriesMap.put(C.ROLL, new HashMap<>());
        allSeriesMap.put(C.YAW, new HashMap<>());
    }

    private void buildPointsMaps() {
        allPointsMap = new HashMap<>();
        allPointsMap.put(C.ACCEL_MAG, new HashMap<>());
        allPointsMap.put(C.ACCEL_X, new HashMap<>());
        allPointsMap.put(C.ACCEL_Y, new HashMap<>());
        allPointsMap.put(C.ACCEL_Z, new HashMap<>());
        allPointsMap.put(C.PITCH, new HashMap<>());
        allPointsMap.put(C.ROLL, new HashMap<>());
        allPointsMap.put(C.YAW, new HashMap<>());

        for (String sensor : C.SENSORS) {
            for (String signal : C.SIGNALS) {
                allPointsMap.get(signal).put(sensor, new ArrayList<>());
            }
        }

    }


    private boolean isLowerBackConnected() {
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

        for (String sensor : C.SENSORS) {
            for (String signal : C.SIGNALS) {

                //clear points
                allPointsMap.get(signal).get(sensor).clear();

                //clear graph

                if (allSeriesMap.get(signal).keySet().contains(sensor)) {
                    while (allSeriesMap.get(signal).get(sensor).size() > 0) {
                        allSeriesMap.get(signal).get(sensor).removeFirst();
                    }
                }
            }
        }

        //reset index's of visible points
        indexOfFirstVisibilePoint = 0;
        plot.redraw();

    }

    public void addToPoints(double value, String sensor, String signal) {

        allPointsMap.get(signal).get(sensor).add(value);
        if (sensorsMap.get(sensor) && signalsMap.get(signal)) {
            if (allSeriesMap.get(signal).get(sensor).size() > SAMPLE_SIZE) {
                allSeriesMap.get(signal).get(sensor).removeFirst();
            }
            allSeriesMap.get(signal).get(sensor).addLast(null, value);
        }
        plot.redraw();

        if (sensor.equals(C.LOWER_BACK) && signal == (C.ACCEL_MAG)) {
            indexOfFirstVisibilePoint++;
        }

    }


    private class myTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            plot.calculateMinMaxVals();
            float minX = plot.getCalculatedMinX().floatValue();
            if (!linker.getIsPlotting()) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: // Start gesture
                        firstFinger = new PointF(event.getX(), event.getY());
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
                            if (indexOfFirstVisibilePoint <= DRAW_SIZE) {
                                break;
                            }
                            for (String sensor : C.SENSORS) {
                                for (String signal : C.SIGNALS) {
                                    if (isConnected.get(sensor)) {
                                        if (signalsMap.get(signal) && sensorsMap.get(sensor)) {
                                            Double d = allPointsMap.get(signal).get(sensor).get(indexOfFirstVisibilePoint - 1);
                                            allSeriesMap.get(signal).get(sensor).removeLast();
                                            allSeriesMap.get(signal).get(sensor).addFirst(null, d);
                                        }
                                    }

                                }
                            }
                            indexOfFirstVisibilePoint--;
                            bar--;
                        }
                        bar = foo - (int) minX;
                        while (bar <= -1) {
                            //right to left
                            if (indexOfFirstVisibilePoint + DRAW_SIZE + 1 >= allPointsMap.get(C.ACCEL_MAG).get(C.LOWER_BACK).size()) {
                                break;
                            }

                            for (String sensor : C.SENSORS) {
                                for (String signal : C.SIGNALS) {
                                    if (isConnected.get(sensor)) {
                                        if (signalsMap.get(signal) && sensorsMap.get(sensor)) {
                                            Double d = allPointsMap.get(signal).get(sensor).get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                                            allSeriesMap.get(signal).get(sensor).removeFirst();
                                            allSeriesMap.get(signal).get(sensor).addLast(null, d);
                                        }
                                    }

                                }
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            putDataInDataBase(data.getStringExtra("name"), data.getIntExtra("exercise", 0));
            Toast.makeText(getActivity(), "Added to Database", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(false);
            plotButton.setEnabled(true);
        } else {
            Toast.makeText(getActivity(), "Nothing Saved", Toast.LENGTH_LONG).show();
        }
    }

    private void putDataInDataBase(String name, int exercise) {


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = dateFormat.format(new Date());

        ArrayList<FlatSpot> flatSpots = RepFinder.findFlatSpots(allPointsMap.get(C.ACCEL_MAG).get(C.LOWER_BACK)); //todo need to get lumbar
        ArrayList<FilmSection> filmSections = RepFinder.getTimes(C.SAMPLE_RATE, flatSpots, storedFileName);

        numberofVidsToDo = filmSections.size();

        for (int i = 0; i < numberofVidsToDo; i++) {

            // split vid
            String newFileDir = Environment.getExternalStorageDirectory() + MEDIA_DIR;
            String newFile = Environment.getExternalStorageDirectory() + MEDIA_DIR + date + "-rep" + i + ".mp4";

            //split
            double segLength = filmSections.get(i).getLength();

            new SplitVideo(segLength, filmSections.get(i).getStartTime(), storedFileName, date + "-rep" + i, newFileDir, this).execute();

            //split points

            HashMap<String, HashMap<String, ArrayList<Double>>> choppedPoints = new HashMap<>();

            for (String signal : C.SIGNALS) {
                HashMap<String, ArrayList<Double>> filtered = filterAllValues(allPointsMap.get(signal));
                choppedPoints.put(signal, new HashMap<>());
                for (String sensor : C.SENSORS) {
                    if (isConnected.get(sensor)) {
                        ArrayList<ArrayList<Double>> tempReps = RepFinder.getReps(flatSpots, filtered.get(sensor), exercise);
                        //all reps are calc'd on the lumbars/lowerbacks flat spots
                        choppedPoints.get(signal).put(sensor, tempReps.get(i));
                    }
                }
            }


            //pop in db
            Detail d = new Detail();
            d.setName(name);
            d.setDate(date);
            d.setVideoFile(newFile);
            d.setLabel(0);
            d.setExercise(exercise);
            d.setAccelMagPoints(choppedPoints.get(C.ACCEL_MAG));
            d.setAccelXPoints(choppedPoints.get(C.ACCEL_X));
            d.setAccelYPoints(choppedPoints.get(C.ACCEL_Y));
            d.setAccelZPoints(choppedPoints.get(C.ACCEL_Z));
            d.setPitchPoints(choppedPoints.get(C.PITCH));
            d.setRollPoints(choppedPoints.get(C.ROLL));
            d.setYawPoints(choppedPoints.get(C.YAW));
            d.setRep(i + 1);

            db.addDetail(d);
        }
    }

    @Override
    public void onVideoSplitComplete() {


        //this is called from the vidSplitter,,, and only deletes the orginal video file when finished with it,
        numberOfVidsDone++;
        Log.d("vidCallback", "#done: " + numberOfVidsDone + "\t #todo: " + numberofVidsToDo);
        if (numberOfVidsDone == numberofVidsToDo) {
            Log.d("delete", "deleting " + storedFileName);
            File file = new File(storedFileName);
            file.delete();
            numberOfVidsDone = 0;
        }

    }

    private HashMap<String, ArrayList<Double>> filterAllValues(HashMap<String, ArrayList<Double>> pointsMap) {

        HashMap<String, ArrayList<Double>> filteredVals = new HashMap<>();

        for (String key : C.SENSORS) {
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
