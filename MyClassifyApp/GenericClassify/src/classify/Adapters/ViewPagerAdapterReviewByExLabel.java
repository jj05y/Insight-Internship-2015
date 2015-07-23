package classify.Adapters;

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
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.cmw.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import classify.Constants.C;
import classify.DatabaseClasses.DatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForReview;


public class ViewPagerAdapterReviewByExLabel extends PagerAdapter {

    private static final int INITIAL_DELAY = 1570;
    private static final int DELAY_MILLI = 18;
    private static final int DELAY_NANO = 531250;


    private ArrayList<ItemForReview> things;
    private Context context;
    //private HashMap<String, LineAndPointFormatter> formatterMap;

    private DatabaseHandler db;
    private boolean readyToPlayVid;
    private MediaPlayer mp;
    private String reviewType;

    HashMap<String, Boolean> signalsMap;
    HashMap<String, Boolean> sensorsMap;

    private Linker linker;
    Random rand;

    private boolean paused;
    private boolean notStarted;
    private boolean finished;


    public ViewPagerAdapterReviewByExLabel(ArrayList<ItemForReview> itemsForReview, Context context, DatabaseHandler db) {
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
        View myInflatedView = inflater.inflate(R.layout.view_pager_item_small_video, null);
        myInflatedView.setTag("page" + position);
        //View myInflatedView = inflater.inflate(R.layout.view_pager_item_small_video, container, false); // - other option

        //get things from things
        String name = things.get(position).getName();

        HashMap<String, HashMap<String, ArrayList<Double>>> allPointsMap = new HashMap<>();
        allPointsMap.put(C.ACCEL_MAG, things.get(position).getAccelMagPoints());
        allPointsMap.put(C.ACCEL_X, things.get(position).getAccelXPoints());
        allPointsMap.put(C.ACCEL_Y, things.get(position).getAccelYPoints());
        allPointsMap.put(C.ACCEL_Z, things.get(position).getAccelZPoints());
        allPointsMap.put(C.PITCH, things.get(position).getPitchPoints());
        allPointsMap.put(C.ROLL, things.get(position).getRollPoints());
        allPointsMap.put(C.YAW, things.get(position).getYawPoints());

        String fileName = things.get(position).getFileName();
        int label = things.get(position).getActualLabel();
        Log.d("spinner", "label comin outa db as " + C.LABELS[label]);
        int exercise = things.get(position).getExercise();
        Log.d("spinner", "exercise comin outa db as " + C.EXERCISES[exercise]);
        int rowID = things.get(position).getRowID();
        int rep = things.get(position).getRep();

        signalsMap = linker.getPlotSignalsMap();
        sensorsMap = linker.getPlotSensorsMap();

        //name text
        TextView nameText = (TextView) myInflatedView.findViewById(R.id.text_new_review_name);
        nameText.setText(name);

        /*// exercise spinner - replaced by textView
        Spinner exerciseSpinner = (Spinner) myInflatedView.findViewById(R.id.exercise_spinner);
        ArrayAdapter<String> exerciseSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, C.EXERCISES);
        exerciseSpinner.setAdapter(exerciseSpinnerAdapter);
        exerciseSpinner.setSelection(exercise);*/

        //exerciseText
        TextView exerciseText = (TextView) myInflatedView.findViewById(R.id.text_exercise_text);
        exerciseText.setText(C.EXERCISES[exercise]);

        // label spinner
        Spinner labelSpinner = (Spinner) myInflatedView.findViewById(R.id.label_spinner);
        //mini list of labels relavant to that exercise;
        String[] specificLabels = linker.findSpecificLabels(exercise);

        ArrayAdapter<String> labelSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, specificLabels);
        labelSpinner.setAdapter(labelSpinnerAdapter);
        labelSpinner.setSelection(label);

        labelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String labelStringFromSpinner = labelSpinner.getSelectedItem().toString();
                Log.d("label", "think label is " + labelStringFromSpinner);
                db.updateActualLabel(rowID, linker.findIndex(labelStringFromSpinner));
                things = db.getAllWithExerciseAndLabel(exercise, label);
                notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*
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
*/

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


        HashMap<String, HashMap<String, SimpleXYSeries>> allSeriesMap = new HashMap<>();
        allSeriesMap.put(C.ACCEL_MAG, new HashMap<>());
        allSeriesMap.put(C.ACCEL_X, new HashMap<>());
        allSeriesMap.put(C.ACCEL_Y, new HashMap<>());
        allSeriesMap.put(C.ACCEL_Z, new HashMap<>());
        allSeriesMap.put(C.PITCH, new HashMap<>());
        allSeriesMap.put(C.ROLL, new HashMap<>());
        allSeriesMap.put(C.YAW, new HashMap<>());

        XYPlot plot = (XYPlot) myInflatedView.findViewById(R.id.new_review_plot);

        paused = false;
        notStarted = true;
        finished = false;

        videoView.setOnTouchListener(new VideoView.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                finished = true;
                            }
                        });
                        if (notStarted) {
                            if (readyToPlayVid) {
                                notStarted = false;
                                //animateGraph(allPointsMap, allSeriesMap, plot);
                                //animateCursor(plot);
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
                        } else if (finished) {
                            //animateGraph(allPointsMap, allSeriesMap, plot);
                            //animateCursor(plot);
                            mp.start();
                            finished = false;
                        } else if (!paused) {
                            mp.pause();
                            paused = true;
                        } else if (paused) {
                            mp.start();
                            paused = false;
                        }
                }
                return false;
            }
        });

        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(5);
        plot.setRangeStepValue(7);
        plot.setDomainLabel("");
        plot.setRangeLabel("");
        plot.setTitle("Rep: " + rep + "  (total reps: " + things.size() + ")");

        plot.getLegendWidget().setSize(new SizeMetrics(70, SizeLayoutType.ABSOLUTE, 650, SizeLayoutType.ABSOLUTE));

/*
        HashMap<String, LineAndPointFormatter> formatterMap = new HashMap<>();
        formatterMap.put(C.LEFT_THIGH, new LineAndPointFormatter(R.color.Black, null, null, null));
        formatterMap.put(C.LEFT_CALF, new LineAndPointFormatter(Color.RED, null, null, null));
        formatterMap.put(C.RIGHT_THIGH, new LineAndPointFormatter(Color.BLUE, null, null, null));
        formatterMap.put(C.RIGHT_CALF, new LineAndPointFormatter(Color.YELLOW, null, null, null));
        formatterMap.put(C.LOWER_BACK, new LineAndPointFormatter(Color.CYAN, null, null, null));
*/


        for (String sensor : C.SENSORS) {
            for (String signal : C.SIGNALS) {
                if (signalsMap.get(signal) && sensorsMap.get(sensor)) {
                    allSeriesMap.get(signal).put(sensor, new SimpleXYSeries(sensor + " - " + signal));
                    allSeriesMap.get(signal).get(sensor).useImplicitXVals();
                    if (allPointsMap.get(signal).get(sensor) != null) {
                        for (double d : allPointsMap.get(signal).get(sensor)) {
                            allSeriesMap.get(signal).get(sensor).addLast(null, d);
                        }
                        plot.addSeries(allSeriesMap.get(signal).get(sensor), new LineAndPointFormatter(randColor(), null, null, null));
                    }
                }
            }
        }
        plot.redraw();


        container.addView(myInflatedView);
        return myInflatedView;

    }



    private void slowMoTime() {

        new Thread() {
            public void run() {
                while (mp.isPlaying()) {
                    try {
                        mp.pause();
                        sleep(150);
                        mp.start();
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void animateGraph(HashMap<String, HashMap<String, ArrayList<Double>>> allPointsMap, HashMap<String, HashMap<String, SimpleXYSeries>> allSeriesMap, XYPlot plot) {

        //set domain and range boundries
        int plotSize = allPointsMap.get(C.ACCEL_MAG).get(C.MAIN_SENSOR).size();
        plot.setDomainBoundaries(0, plotSize, BoundaryMode.FIXED);

        plot.calculateMinMaxVals();
        plot.setRangeBoundaries(plot.getCalculatedMinY(), plot.getCalculatedMaxY(), BoundaryMode.FIXED);

        //clear graph
        for (String sensor : C.SENSORS) {
            for (String signal : C.SIGNALS) {
                if (signalsMap.get(signal) && sensorsMap.get(sensor)) {
                    while (allSeriesMap.get(signal).get(sensor).size() > 0) {
                        allSeriesMap.get(signal).get(sensor).removeFirst();
                    }
                }
            }
        }
        plot.redraw();


        //put points in one by one,
        new Thread() {

            private int size = plotSize;

            public void run() {
                try {
                    sleep(INITIAL_DELAY);
                    for (int i = 0; i < size; i++) {
                        while (paused) {
                            sleep(DELAY_MILLI, DELAY_NANO);
                        }
                        for (String sensor : C.SENSORS) {
                            for (String signal : C.SIGNALS) {
                                if (allPointsMap.get(signal).get(sensor) != null) {
                                    if (signalsMap.get(signal) && sensorsMap.get(sensor)) {
                                        allSeriesMap.get(signal).get(sensor).addLast(null, allPointsMap.get(signal).get(sensor).get(i));
                                    }
                                }
                            }
                        }
                        plot.redraw();
                        sleep(DELAY_MILLI, DELAY_NANO);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();


    }

    private void animateCursor(XYPlot plot) {
        plot.calculateMinMaxVals();
        double barHeight = (double) plot.getCalculatedMaxY();
        int plotSize = (int) plot.getCalculatedMaxX();
        SimpleXYSeries bar = new SimpleXYSeries("");
        plot.addSeries(bar, new BarFormatter(randColor(), randColor()));

        new Thread() {

            private int size = plotSize;
            double bh = barHeight;


            public void run() {
                try {
                    sleep(INITIAL_DELAY);
                    for (int i = 0; i < size; i++) {
                        bar.addFirst(i, bh);
                        plot.redraw();

                        while (paused) {
                            sleep(DELAY_MILLI, DELAY_NANO);
                        }

                        sleep(DELAY_MILLI, DELAY_NANO);

                        bar.removeFirst();
                        plot.redraw();
                    }
                } catch (InterruptedException e) {
                    Log.d("thread", "thread crapped itself");
                }
                plot.removeSeries(bar);

            }
        }.start();


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
        paused = false;
        notStarted = true;
        finished = false;
    }


}
