package com.shimmerresearch.DataBaseVidView;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cmw.DataBaseVidView.R;

public class MainActivity extends Activity implements View.OnTouchListener {

    private static final int REQUEST_CONNECT_SHIMMER = 2;
    private final static int SAMPLE_SIZE = 30;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;

    private Shimmer mShimmerDevice1 = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private TextView battText;
    private EditText nameEdit;

    private DataBaseHandler db;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private XYPlot plot;
    private SimpleXYSeries xAccellSeries;
    private SimpleXYSeries yAccellSeries;
    private SimpleXYSeries zAccellSeries;
    private boolean plotting;
    private int indexOfFirstVisibilePoint;

    private PointF firstFinger;

    private ArrayList<Point> points;

    private Camera c;
    private CameraPreview preview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording;
    private FrameLayout surface;

    private String storedFileName;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.setTooLegacyObjectClusterSensorNames();
        setContentView(R.layout.main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mShimmerDevice1 = new Shimmer(this, mHandler, "sensor", 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true); //sensor is a unique identifier for the shimmer unit
        battText = (TextView) findViewById(R.id.battText);
        db = new DataBaseHandler(this);
        points = new ArrayList<Point>();

        surface = (FrameLayout) findViewById(R.id.camera_preview);


        nameEdit = (EditText) findViewById(R.id.editText);
        nameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                redrawShortly();
                return false;
            }
        });

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_SHIMMER);

            }
        });
        isRecording = false;


        Button disConnectButton = (Button) findViewById(R.id.disconnect_button);
        disConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShimmerDevice1.stop();
                //startStopRecording();

            }
        });


        Button plotButton = (Button) findViewById(R.id.plot_button);
        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShimmerDevice1.getStreamingStatus()) {

                    startStopRecording();

                    if (plotButton.getText().equals("Plot")) {
                        reset();
                        nameEdit.setText("");
                        plotting = true;
                        plotButton.setText("Stop");

                    } else {
                        indexOfFirstVisibilePoint = points.size() - SAMPLE_SIZE;
                        plotting = false;
                        plotButton.setText("Plot");
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Connect Shimmer First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (!plotting && points.size() != 0) {
                    String name = nameEdit.getText().toString();

                    if (!name.equals("")) {
                        Toast.makeText(getBaseContext(), "Adding " + points.size() + " point graph to DB", Toast.LENGTH_SHORT).show();
                        db.addDetail(new Detail(name, points, storedFileName));

                        reset();
                    }

                    prepareListData();
                    listAdapter.updateListDataChild(listDataChild);
                    nameEdit.setText("");
                } else {
                    Toast.makeText(getBaseContext(), "Plot something first", Toast.LENGTH_SHORT).show();

                }
                redrawShortly();
            }
        });

        Button analyze = (Button) findViewById(R.id.analyze_button);
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points == null || points.size() == 0) {
                    Toast.makeText(getBaseContext(), "Select a graph first", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AnalyzeActivity.class);
                intent.putExtra("data", points);
                intent.putExtra("path", storedFileName);
                startActivity(intent);
            }
        });


        /////////////////////////
        /// Expandable List View
        /////////////////////////
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                expListView.collapseGroup(0);
                reset();
                Toast.makeText(getBaseContext(), "Drawing graph id " + (id + 1), Toast.LENGTH_SHORT).show();
                Detail d = db.getDetail((int) id + 1);
                points = d.getPoints();
                for (int i = 0; i < DRAW_SIZE; i++) {
                    xAccellSeries.addLast(null, points.get(i).getxVal());
                    yAccellSeries.addLast(null, points.get(i).getyVal());
                    zAccellSeries.addLast(null, points.get(i).getzVal());
                }
                redrawShortly();
                storedFileName = d.getStoredFileName();

                return false;
            }

        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int i) {
                redrawShortly();
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int i) {
                redrawShortly();
            }
        });

        ////////////////////////// ExpLisView

        //////////////////////////
        // Plot
        //////////////////////////

        plot = (XYPlot) findViewById(R.id.xyPlot);


        xAccellSeries = new SimpleXYSeries("Accel X");
        yAccellSeries = new SimpleXYSeries("Accel Y");
        zAccellSeries = new SimpleXYSeries("Accel Z");

        xAccellSeries.useImplicitXVals();
        yAccellSeries.useImplicitXVals();
        zAccellSeries.useImplicitXVals();

        LineAndPointFormatter formatX = new LineAndPointFormatter(Color.BLACK, Color.GREEN, null, null);
        formatX.getLinePaint().setStrokeJoin(Paint.Join.ROUND); //this dont seem to make a difference,,, hmm,,,

        plot.addSeries(xAccellSeries, formatX);
        plot.addSeries(yAccellSeries, new LineAndPointFormatter(Color.BLACK, Color.BLUE, null, null));
        plot.addSeries(zAccellSeries, new LineAndPointFormatter(Color.BLACK, Color.YELLOW, null, null));


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


        reset();


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

    private void reset() {
        //clear graph
        while (xAccellSeries.size() != 0) {
            xAccellSeries.removeFirst();
            yAccellSeries.removeFirst();
            zAccellSeries.removeFirst();
        }
        //clear points
        points.clear();

        //reset index's of visible points
        indexOfFirstVisibilePoint = 0;

        plot.redraw();


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Graphs");

        // Adding child data
        List<String> entries = new ArrayList<String>();
        List<String> graphNames = db.getAllNames();
        for (String s : graphNames) {
            entries.add(s);
        }

        listDataChild.put(listDataHeader.get(0), entries); // Header, Child data
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (mShimmerDevice1.getStreamingStatus()) {
                mShimmerDevice1.stop();
            } else {
                String bluetoothAddress = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                mShimmerDevice1.connect(bluetoothAddress, "default");
            }

        } else {
            Toast.makeText(MainActivity.this, "No Shimmer Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void plotTheGraphs(Point p) {


        if (xAccellSeries.size() > SAMPLE_SIZE) {
            xAccellSeries.removeFirst();
            yAccellSeries.removeFirst();
            zAccellSeries.removeFirst();

        }
        xAccellSeries.addLast(null, p.getxVal());
        yAccellSeries.addLast(null, p.getyVal());
        zAccellSeries.addLast(null, p.getzVal());

        plot.redraw();

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

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/vidsFromDualApp/");
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


    private final Handler mHandler = new Handler() {

        private boolean needsRedraw;

        @SuppressWarnings("null")
        public void handleMessage(Message msg) {
            needsRedraw = false;
            switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
                case Shimmer.MESSAGE_READ:

                    Point p = new Point();
                    if ((msg.obj instanceof ObjectCluster)) {    // within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
                        ObjectCluster objectCluster = (ObjectCluster) msg.obj;


                        Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
                        FormatCluster formatCluster = ObjectCluster.returnFormatCluster(accelXFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (plotting) {
                                p.setxVal(formatCluster.mData);
                            }
                        }

                        Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelYFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (plotting) {
                                p.setyVal(formatCluster.mData);
                            }
                        }

                        Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelZFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (plotting) {
                                p.setzVal(formatCluster.mData);
                            }

                        }

                        Collection<FormatCluster> batteryFormats = objectCluster.mPropertyCluster.get("VSenseBatt");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(batteryFormats, "CAL"); // retrieve the calibrated data
                        if (batteryFormats != null) {
                            battText.setText(" Battery(" + formatCluster.mUnits + "): " + String.format("%.2f", formatCluster.mData));
                        }
                        //if (plotting) upDatePeaks();

                        if (plotting) {
                            points.add(p);
                            plotTheGraphs(p);
                        }

                    }

                    break;
                case Shimmer.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Shimmer.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                            if (mShimmerDevice1.getShimmerState() == Shimmer.STATE_CONNECTED) {
                                Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
                                mShimmerDevice1.startStreaming();
                            }
                            break;
                        case Shimmer.STATE_CONNECTING:
                            Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case Shimmer.STATE_NONE:
                            Toast.makeText(getBaseContext(), "No State", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };


    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        plot.calculateMinMaxVals();
        float minX = plot.getCalculatedMinX().floatValue();
        if (!plotting) {
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

                        Point p = points.get(indexOfFirstVisibilePoint - 1);
                        xAccellSeries.removeLast();
                        xAccellSeries.addFirst(null, p.getxVal());

                        yAccellSeries.removeLast();
                        yAccellSeries.addFirst(null, p.getyVal());

                        zAccellSeries.removeLast();
                        zAccellSeries.addFirst(null, p.getzVal());

                        indexOfFirstVisibilePoint--;
                        bar--;
                    }
                    bar = foo - (int) minX;
                    while (bar <= -1) {
                        //right to left
                        if (points == null || indexOfFirstVisibilePoint + DRAW_SIZE + 1 >= points.size())
                            break;

                        Point p = points.get(indexOfFirstVisibilePoint + DRAW_SIZE + 1);
                        xAccellSeries.removeFirst();
                        xAccellSeries.addLast(null, p.getxVal());

                        yAccellSeries.removeFirst();
                        yAccellSeries.addLast(null, p.getyVal());

                        zAccellSeries.removeFirst();
                        zAccellSeries.addLast(null, p.getzVal());

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
    protected void onPause() {
        super.onPause();
        releaseCamera();
        releaseMediaRecorder();
        surface.removeAllViews();
    }


    @Override
    protected void onResume() {


        ////////////////////////
        /// Sort Camera
        ////////////////////////

        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            c = null;
            try {
                c = Camera.open();

                Camera.Parameters p = c.getParameters();
                p.setRotation(90);
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                p.setRecordingHint(true);
                c.setParameters(p);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        preview = new CameraPreview(this, c);
        surface.addView(preview);
        c.startPreview();
        super.onResume();


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


}



    



    
    