package com.shimmerresearch.DataBaseVidView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import cmw.DataBaseVidView.R;

/**
 * Created by joe on 15/06/15.
 */
public class AnalyzeActivity extends Activity implements View.OnTouchListener {

    private final static int SAMPLE_SIZE = 30;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;

    private int indexOfFirstVisiblePoint;
    private XYPlot plot;
    private ArrayList<Double> xVals;
    private ArrayList<Double> xValsAfterHarshPass;
    private ArrayList<Double> xValsAfterSoftPass;
    private SimpleXYSeries prePlot;
    private SimpleXYSeries afterPlot;
    private SimpleXYSeries softPlot;

    private SurfaceHolder vidHolder;
    private MediaPlayer mp;
    private String pathToVid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze);

        Intent intent = getIntent();
        ArrayList<Point> points = (ArrayList<Point>) intent.getSerializableExtra("data");
        xVals = new ArrayList<Double>();
        for (Point p : points) {
            xVals.add(p.getxVal());
        }

        pathToVid = (String) intent.getStringExtra("path");

        indexOfFirstVisiblePoint = 0;

        TextView prePass = (TextView) findViewById(R.id.preLowPassText);
        TextView afterPass = (TextView) findViewById(R.id.afterLowPassText);
        TextView softPass= (TextView) findViewById(R.id.soft_low_pass_text);

        prePass.setText("XAccel Peaks before Low Pass Filter: " + peakCounter(xVals));
        xValsAfterHarshPass = harshLowPass(xVals);
        afterPass.setText("XAccel Peaks after Harsh Low Pass Filter: " + peakCounter(xValsAfterHarshPass));
        xValsAfterSoftPass = softLowPass(xVals);
        softPass.setText("XAccel Peaks after Soft Low Pass Filter: " + peakCounter(xValsAfterSoftPass));

        CheckBox preCheck = (CheckBox) findViewById(R.id.pre_checkBox);
        CheckBox harshCheck = (CheckBox) findViewById(R.id.harsh_checkBox);
        CheckBox softCheck = (CheckBox) findViewById(R.id.soft_checkBox);



        preCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    plot.addSeries(prePlot,new LineAndPointFormatter(Color.BLACK, Color.MAGENTA, null, null));
                } else {
                    plot.removeSeries(prePlot);
                }
                plot.redraw();
            }
        });
        harshCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    plot.addSeries(afterPlot, new LineAndPointFormatter(Color.BLACK, Color.CYAN, null, null));
                } else {
                    plot.removeSeries(afterPlot);
                }
                plot.redraw();

            }
        });
        softCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    plot.addSeries(softPlot, new LineAndPointFormatter(Color.BLACK, Color.YELLOW, null, null));
                } else {
                    plot.removeSeries(softPlot);
                }
                plot.redraw();

            }
        });

        plot = (XYPlot) findViewById(R.id.passPlot);


        prePlot = new SimpleXYSeries("Pre");
        afterPlot = new SimpleXYSeries("Harsh");
        softPlot = new SimpleXYSeries("Soft");

        prePlot.useImplicitXVals();
        afterPlot.useImplicitXVals();
        softPlot.useImplicitXVals();

        plot.addSeries(prePlot, new LineAndPointFormatter(Color.BLACK, Color.MAGENTA, null, null));
        plot.addSeries(afterPlot, new LineAndPointFormatter(Color.BLACK, Color.CYAN, null, null));
        plot.addSeries(softPlot, new LineAndPointFormatter(Color.BLACK, Color.YELLOW, null, null));


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

        for (int i = 0; i < DRAW_SIZE; i++) {
            prePlot.addLast(null, xVals.get(i));
            afterPlot.addLast(null, xValsAfterHarshPass.get(i));
            softPlot.addLast(null, xValsAfterSoftPass.get(i));
        }

        plot.redraw();




        ////////////////////////
        //  set up vid
        ////////////////////////

        VideoView originalVidView = (VideoView) findViewById(R.id.videoView);
        vidHolder = originalVidView.getHolder();
        vidHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mp = new MediaPlayer();
                try {
                    Log.d("storage", "from analyze: " +pathToVid);
                    mp.setDataSource(pathToVid);

                    mp.setDisplay(vidHolder);
                    mp.setLooping(true);
                    mp.prepare();
                    mp.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });




    }

    private int peakCounter(ArrayList<Double> vals) {
        Iterator<Double> it = vals.iterator();
        double prev, curr, next;
        int numPeaks = 0;

        prev = 0;
        curr = it.next();
        while (it.hasNext()) {
            next = it.next();
            if (curr > prev && curr > next) {
                numPeaks++;
            }
            prev = curr;
            curr = next;
        }
        return numPeaks;
    }

    private ArrayList<Double> harshLowPass(ArrayList<Double> vals) {
        ArrayList<Double> result = new ArrayList<Double>();
        double x;
        x = vals.get(0) + vals.get(1) + vals.get(2);
        result.add(x/3);
        x = vals.get(0) + vals.get(1) + vals.get(2) + vals.get(3);
        result.add(x/4);
        for (int i = 2; i < vals.size()-2; i++) {
                x = vals.get(i-2) + vals.get(i-1) + vals.get(i) + vals.get(i+1) + vals.get(i+2);
                result.add(x/5);

        }
        x = vals.get(vals.size()-4) + vals.get(vals.size()-3) + vals.get(vals.size()-2) + vals.get(vals.size()-1);
        result.add(x/4);
        x = vals.get(vals.size()-3) + vals.get(vals.size()-2) + vals.get(vals.size()-1);
        result.add(x/3);
        return result;
    }

    private ArrayList<Double> softLowPass(ArrayList<Double> vals) {
        ArrayList<Double> result = new ArrayList<Double>();
        double x;
        x = vals.get(0) + vals.get(1);
        result.add(x/2);
        for (int i = 1; i < vals.size()-1; i++) {
            x = vals.get(i-1) + vals.get(i) + vals.get(i+1);
            result.add(x/3);
        }
        x = vals.get(vals.size()-2) + vals.get(vals.size()-1);
        result.add(x/2);
        return result;
    }


    private PointF firstFinger;
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        plot.calculateMinMaxVals();
        float minX = plot.getCalculatedMinX().floatValue();
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
                        if (xVals == null || indexOfFirstVisiblePoint <= 0) break;

                        prePlot.removeLast();
                        prePlot.addFirst(null, xVals.get(indexOfFirstVisiblePoint - 1));

                        afterPlot.removeLast();
                        afterPlot.addFirst(null, xValsAfterHarshPass.get(indexOfFirstVisiblePoint - 1));

                        softPlot.removeLast();
                        softPlot.addFirst(null, xValsAfterSoftPass.get(indexOfFirstVisiblePoint -1));

                        indexOfFirstVisiblePoint--;
                        bar--;
                    }
                    bar = foo - (int) minX;
                    while (bar <= -1) {
                        //right to left
                        if (xVals == null || indexOfFirstVisiblePoint + DRAW_SIZE + 1 >= xVals.size())
                            break;

                        prePlot.removeFirst();
                        prePlot.addLast(null, xVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1));

                        afterPlot.removeFirst();
                        afterPlot.addLast(null, xValsAfterHarshPass.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1));

                        softPlot.removeFirst();
                        softPlot.addLast(null, xValsAfterSoftPass.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1));

                        indexOfFirstVisiblePoint++;
                        bar++;
                    }
                    plot.redraw();

                    break;
            }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.release();


    }
}