package com.shimmerresearch.MultiShimmerRecordReview.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.ItemForReview;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class ViewPagerAdapter extends PagerAdapter {


    private ArrayList<ItemForReview> things;
    private Context context;
    private final static int SAMPLE_SIZE = 30;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;
    private XYPlot plot;
    private HashMap<String, SimpleXYSeries> accelMagSeriesMap;
    private HashMap<String, SimpleXYSeries> accelXSeriesMap;
    private HashMap<String, SimpleXYSeries> accelYSeriesMap;
    private HashMap<String, SimpleXYSeries> accelZSeriesMap;
    private HashMap<String, SimpleXYSeries> pitchSeriesMap;
    private HashMap<String, SimpleXYSeries> rollSeriesMap;
    private HashMap<String, SimpleXYSeries> yawSeriesMap;
    //private HashMap<String, LineAndPointFormatter> formatterMap;
    private DatabaseHandler db;
    private boolean readyToPlayVid;
    private MediaPlayer mp;
    private String reviewType;

    HashMap<String, Boolean> signalsMap;
    HashMap<String, Boolean> sensorsMap;

    private Linker linker;
    Random rand;



    public ViewPagerAdapter(ArrayList<ItemForReview> itemsForReview, Context context, DatabaseHandler db, String reviewType) {
        this.things = itemsForReview;
        this.context = context;
        this.db = db;
        Log.d("page", "brandnew");
        mp = new MediaPlayer();
        this.reviewType = reviewType;
        linker = (Linker) context;
        rand = new Random();
    }



    @Override
    public int getCount() {
        return things.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myInflatedView = inflater.inflate(R.layout.view_pager_item, null);
        myInflatedView.setTag("page" + position);
        //View myInflatedView = inflater.inflate(R.layout.view_pager_item, container, false); // - other option

        //get things from things
        String name = things.get(position).getName();
        HashMap<String, ArrayList<Double>> accelMagPoints = things.get(position).getAccelMagPoints();
        HashMap<String, ArrayList<Double>> accelXPoints = things.get(position).getAccelXPoints();
        HashMap<String, ArrayList<Double>> accelYPoints = things.get(position).getAccelYPoints();
        HashMap<String, ArrayList<Double>> accelZPoints = things.get(position).getAccelZPoints();
        HashMap<String, ArrayList<Double>> pitchPoints = things.get(position).getPitchPoints();
        HashMap<String, ArrayList<Double>> rollPoints = things.get(position).getRollPoints();
        HashMap<String, ArrayList<Double>> yawPoints = things.get(position).getYawPoints();
        String fileName = things.get(position).getFileName();
        int label = things.get(position).getLabel();
        Log.d("spinner", "label comin outa db as " + C.LABELS[label]);
        int exercise = things.get(position).getExercise();
        Log.d("spinner", "exercise comin outa db as " + C.EXERCISES[exercise]);
        int rowID = things.get(position).getRowID();
        int rep = things.get(position).getRep();

        signalsMap = linker.getPlotSignalsMap();
        sensorsMap = linker.getPlotSensorsMap();

        //name text
        TextView nameText = (TextView) myInflatedView.findViewById(R.id.text_new_review_name);
        nameText.setText(name + " - Rep: " + rep);

        // exercise spinner
        Spinner exerciseSpinner = (Spinner) myInflatedView.findViewById(R.id.exercise_spinner);
        ArrayAdapter<String> exerciseSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, C.EXERCISES);
        exerciseSpinner.setAdapter(exerciseSpinnerAdapter);
        exerciseSpinner.setSelection(exercise);

        // label spinner
        Spinner labelSpinner = (Spinner) myInflatedView.findViewById(R.id.label_spinner);
        ArrayAdapter<String> labelSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, C.LABELS);
        labelSpinner.setAdapter(labelSpinnerAdapter);
        labelSpinner.setSelection(label);

        labelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.updateLabel(rowID, i);
                switch (reviewType) {
                    case C.REVIEW_BY_NAME:
                        things = db.getAllWithName(name);
                        break;
                    case C.REVIEW_BY_LABEL:
                        things = db.getAllWithExerciseAndLabel(exercise, label);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.updateExercise(rowID, i);
                switch (reviewType) {
                    case C.REVIEW_BY_NAME:
                        things = db.getAllWithName(name);
                        break;
                    case C.REVIEW_BY_LABEL:
                        things = db.getAllWithExerciseAndLabel(exercise, label);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //video view :/ eugh mayb just start looping? not on creation, wouldnt work,,, hmm,

        readyToPlayVid = false;

        VideoView videoView = (VideoView) myInflatedView.findViewById(R.id.new_review_video_view);
        SurfaceHolder vidHolder = videoView.getHolder();
        vidHolder.addCallback(new Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d("surfaceTesting", "created position " + position);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fileName, MediaStore.Images.Thumbnails.MINI_KIND);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), thumb);
                videoView.setBackground(bitmapDrawable);
                readyToPlayVid = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d("surfaceTesting", "changed position " + position);
                readyToPlayVid = true;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d("surfaceTesting", "destroyed position " + position);


            }
        });

        videoView.setOnTouchListener(new VideoView.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (readyToPlayVid) {
                            videoView.setBackground(null);
                            Log.d("surfaceTesting", "Boom");
                            mp.reset();
                            try {
                                Log.d("storage", "from viewpageradapter: " + fileName);
                                mp.setDataSource(fileName);

                                mp.setDisplay(vidHolder);
                                mp.prepare();
                                mp.start();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                }
                return false;
            }
        });


        //todo ----------- :|


        //plot,,, also need to make the plot scroll with vid,,, hmm,,


        plot = (XYPlot) myInflatedView.findViewById(R.id.new_review_plot);
        accelMagSeriesMap = new HashMap<>();
        accelXSeriesMap = new HashMap<>();
        accelYSeriesMap = new HashMap<>();
        accelZSeriesMap = new HashMap<>();
        pitchSeriesMap = new HashMap<>();
        rollSeriesMap = new HashMap<>();
        yawSeriesMap = new HashMap<>();
        //plot.setRangeBoundaries(0, 20, BoundaryMode.FIXED);
        //plot.setDomainBoundaries(0, 35, BoundaryMode.FIXED);

        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(5);
        plot.setRangeStepValue(7);
        plot.setDomainLabel("");
        plot.setRangeLabel("");

        plot.getLegendWidget().setSize(new SizeMetrics(70, SizeLayoutType.ABSOLUTE, 650, SizeLayoutType.ABSOLUTE));

/*
        formatterMap = new HashMap<>();
        formatterMap.put(C.LEFT_THIGH, new LineAndPointFormatter(Color.GREEN, null, null, null));
        formatterMap.put(C.LEFT_CALF, new LineAndPointFormatter(Color.RED, null, null, null));
        formatterMap.put(C.RIGHT_THIGH, new LineAndPointFormatter(Color.BLUE, null, null, null));
        formatterMap.put(C.RIGHT_CALF, new LineAndPointFormatter(Color.YELLOW, null, null, null));
        formatterMap.put(C.LOWER_BACK, new LineAndPointFormatter(Color.CYAN, null, null, null));
*/



        for (String key: C.KEYS) {
            if (signalsMap.get(C.ACCEL_MAG) && sensorsMap.get(key)) {
                accelMagSeriesMap.put(key, new SimpleXYSeries(C.ACCEL_MAG));
                accelMagSeriesMap.get(key).useImplicitXVals();
                for (double d: accelMagPoints.get(key)){
                    accelMagSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(accelMagSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.ACCEL_X) && sensorsMap.get(key)) {
                accelXSeriesMap.put(key, new SimpleXYSeries(C.ACCEL_X));
                accelXSeriesMap.get(key).useImplicitXVals();
                for (double d: accelXPoints.get(key)){
                    accelXSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(accelXSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.ACCEL_Y) && sensorsMap.get(key)) {
                accelYSeriesMap.put(key, new SimpleXYSeries(C.ACCEL_Y));
                accelYSeriesMap.get(key).useImplicitXVals();
                for (double d: accelYPoints.get(key)){
                    accelYSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(accelYSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.ACCEL_Z) && sensorsMap.get(key)) {
                accelZSeriesMap.put(key, new SimpleXYSeries(C.ACCEL_Z));
                accelZSeriesMap.get(key).useImplicitXVals();
                for (double d: accelZPoints.get(key)){
                    accelZSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(accelZSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.PITCH) && sensorsMap.get(key)) {
                pitchSeriesMap.put(key, new SimpleXYSeries(C.PITCH));
                pitchSeriesMap.get(key).useImplicitXVals();
                for (double d: pitchPoints.get(key)){
                    pitchSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(pitchSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.ROLL) && sensorsMap.get(key)) {
                rollSeriesMap.put(key, new SimpleXYSeries(C.ROLL));
                rollSeriesMap.get(key).useImplicitXVals();
                for (double d: rollPoints.get(key)){
                    rollSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(rollSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }
            if (signalsMap.get(C.YAW) && sensorsMap.get(key)) {
                yawSeriesMap.put(key, new SimpleXYSeries(C.YAW));
                yawSeriesMap.get(key).useImplicitXVals();
                for (double d: yawPoints.get(key)){
                    yawSeriesMap.get(key).addLast(null, d);
                }
                plot.addSeries(yawSeriesMap.get(key), new LineAndPointFormatter(randColor(), null, null, null));
            }

        }
        plot.redraw();



        container.addView(myInflatedView);
        return myInflatedView;

    }

    private int randColor() {
        int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return color;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    public void stopMediaPlayer() {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }


}
