package com.shimmerresearch.MultiShimmerRecordReview.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by joe on 25/06/15.
 */
public class HorizontalListViewAdapter extends ArrayAdapter<HorizontalListItemForReview> {

    private final ArrayList<HorizontalListItemForReview> things;
    private Context context;


    private final static int SAMPLE_SIZE = 30;
    private final static int DRAW_SIZE = SAMPLE_SIZE + 6;
    private int indexOfFirstVisiblePoint;
    private XYPlot plot;
    private SimpleXYSeries series;
    boolean surfaceReady;

    private MediaPlayer mp;


    public HorizontalListViewAdapter(Context context, ArrayList<HorizontalListItemForReview> things) {
        super(context, -1, things);
        this.context = context;
        this.things = things;




    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myInflatedView = inflater.inflate(R.layout.list_horiz_item, parent, false);

        // Get the data
        String name = things.get(position).getName();
        ArrayList<Double> points = things.get(position).getPoints();
        String fileName = things.get(position).getFileName();
        String label = things.get(position).getLabel();
        String exercise = things.get(position).getExercise();
        int rowID = things.get(position).getRowID();


        // Set name and label
        // todo sort label proper
        TextView header = (TextView) myInflatedView.findViewById(R.id.text_header);
        header.setText("Row:" + rowID + " - " + name);
        TextView labelText = (TextView) myInflatedView.findViewById(R.id.text_label);
        labelText.setText(label);
        TextView exerciseText = (TextView) myInflatedView.findViewById(R.id.text_exercise);
        exerciseText.setText(exercise);


        //////////////////////
        // Plot
        //////////////////////


        indexOfFirstVisiblePoint = 0;
        plot = (XYPlot) myInflatedView.findViewById(R.id.reviewPlot);
        series = new SimpleXYSeries("Plot");
        series.useImplicitXVals();
        plot.addSeries(series, new LineAndPointFormatter(Color.BLACK, Color.GREEN, null, null));
        plot.setRangeBoundaries(-20, 30, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, 35, BoundaryMode.FIXED);

        plot.setDomainValueFormat(new DecimalFormat("#"));

        DashPathEffect dashFx = new DashPathEffect(new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);

        plot.setDomainStepValue(1);
        plot.setRangeStepValue(7);
        plot.setDomainLabel("");
        plot.setRangeLabel("m/s^2");

        for (Double d : points) {
            series.addLast(null, d);
        }
        plot.redraw();

        //////////////////////// plot


        ////////////////////////
        // Video
        ////////////////////////


        surfaceReady = false;
        SurfaceHolder vidHolder;

        VideoView originalVidView = (VideoView) myInflatedView.findViewById(R.id.reviewVideoView);
        vidHolder = originalVidView.getHolder();
        vidHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceReady = true;

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(null, "Zapped a view");


            }
        });


        originalVidView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("storage", "loading vid from: " +fileName);
                if (surfaceReady) {
                    Log.d(null, "surface is ready");

                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();

                    }

                    mp = new MediaPlayer();

                    try {
                        mp.setDataSource(fileName);

                        mp.setDisplay(vidHolder);
                        //    mPs.get(position).setLooping(true);
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });


        ////////////////////////


        return myInflatedView;
    }


    public void releaseMP() {
        if (mp != null) {
            mp.release();
        }
    }

}






