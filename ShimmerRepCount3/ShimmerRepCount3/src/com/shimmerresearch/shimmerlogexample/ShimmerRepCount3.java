//v0.2 -  8 January 2013

/*
 * Copyright (c) 2010, Shimmer Research, Ltd.
 * All rights reserved
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:

 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of Shimmer Research, Ltd. nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Jong Chern Lim
 * @date   October, 2013
 */

//Future updates needed
//- the handler should be converted to static 

/************************************/
/* NEW IN THIS SHIMMERREPCOUNT VERSION (VERSION 3):

 - removed Aditya's repcount function as it was not being used, if you need it, go to version 2
 - added in state to detect failure
 - added in timer to the reps, counts how long you have been in the current state
 */
/***********************************/

package com.shimmerresearch.shimmerlogexample;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.shimmerresearch.shimmerlogexample.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Vibrator;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import android.os.Bundle;
import android.app.Activity;
import com.shimmerresearch.driver.*;
import com.shimmerresearch.android.*;
import com.shimmerresearch.tools.Logging;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.google.common.collect.BiMap;

import org.w3c.dom.Text;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class ShimmerRepCount3 extends Activity {
    /** Called when the activity is first created. */

	private final static int SAMPLE_SIZE = 30;
	private final static int DRAW_SIZE = SAMPLE_SIZE + 6; //change this 0 to 6 later maybe??

	private final static int MINI_ARRAY_SIZE = 3;

	private final static double TOO_BIG = 1.0;
	private final static double TOO_SMALL = -1.0;

	private boolean reachedHigh;
	private int numReps;

	//enum type stuff
	private enum State {start, down, goingUp, up, goingDown, fail}
	private State state;

	private TextView x_txt;
	private TextView y_txt;
	private TextView z_txt;
	private TextView check_txt;
	private TextView displayCount;
	private TextView displayState;
	private TextView displayTime;

	private int count = 1;
	private int timeInState;

	private ArrayList<Double> x_vals;
	private ArrayList<Double> y_vals;
	private ArrayList<Double> z_vals;

	private ArrayList<Double> xButterVals;
	private ArrayList<Double> yButterVals;
	private ArrayList<Double> zButterVals;

	private XYPlot plot;

	private SimpleXYSeries x_series;
	private SimpleXYSeries y_series;
	private SimpleXYSeries z_series;

	private SimpleXYSeries xButterSeries;
	private SimpleXYSeries yButterSeries;
	private SimpleXYSeries zButterSeries;

	private LineAndPointFormatter x_seriesFormat;
	private LineAndPointFormatter y_seriesFormat;
	private LineAndPointFormatter z_seriesFormat;

	private LineAndPointFormatter xButterSeriesFormat;
	private LineAndPointFormatter yButterSeriesFormat;
	private LineAndPointFormatter zButterSeriesFormat;

	PointF firstFinger;

	//extra stuff to make the plot scrollable
	private boolean plotting;
	//private ArrayList<Point> points;
	private int indexOfFirstVisiblePoint;

	View.OnTouchListener myOnTouchListener;

	//Vibration and Sound Stuff
	ToneGenerator toneG;
	Vibrator v;

	// Shimmer Variables etc.
    private Shimmer mShimmerDevice1 = null;
	static final int REQUEST_CONNECT_SHIMMER = 2;
	static final int REQUEST_CONFIGURE_SHIMMER = 3;
	private BluetoothAdapter mBluetoothAdapter = null;
	boolean mFirstWrite=true;
    private static String mFileName = "shimmerbasicloggingexample";
	static Logging log = new Logging(mFileName,"\t"); //insert file name
    Timer mTimer;	
    boolean stop=false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.setTooLegacyObjectClusterSensorNames();
        setContentView(R.layout.main);

		//points = new ArrayList<Point>();
		indexOfFirstVisiblePoint = 0;
		plotting = false;

		reachedHigh = false;
		numReps = 0;
		state = State.start;
		timeInState = 0;

		x_txt = (TextView)findViewById(R.id.x_txt);
		y_txt = (TextView)findViewById(R.id.y_txt);
		z_txt = (TextView)findViewById(R.id.z_txt);

		check_txt = (TextView)findViewById(R.id.check_txt);
		displayCount = (TextView)findViewById(R.id.display_count);
		displayState = (TextView)findViewById(R.id.display_state);
		displayTime = (TextView)findViewById(R.id.display_time);

		toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		x_vals = new ArrayList<>();
		y_vals = new ArrayList<>();
		z_vals = new ArrayList<>();

		xButterVals = new ArrayList<>();
		yButterVals = new ArrayList<>();
		zButterVals = new ArrayList<>();

		plot = (XYPlot) findViewById(R.id.myResultsPlot);

		myOnTouchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				plot.calculateMinMaxVals();
				float minX = plot.getCalculatedMinX().floatValue();
				//Log.d("TEST", "OnTouchListener has started, plotting = " + plotting);
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
								if (x_vals == null || indexOfFirstVisiblePoint <= 0) break;

								double d;
								//d = x_vals.get(indexOfFirstVisiblePoint - 1);
								//x_series.removeLast();
								//x_series.addFirst(null, d);

								//d = y_vals.get(indexOfFirstVisiblePoint - 1);
								//y_series.removeLast();
								//y_series.addFirst(null, d);

								//d = z_vals.get(indexOfFirstVisiblePoint - 1);
								//z_series.removeLast();
								//z_series.addFirst(null, d);

								d = xButterVals.get(indexOfFirstVisiblePoint - 1);
								xButterSeries.removeLast();
								xButterSeries.addFirst(null, d);

								d = yButterVals.get(indexOfFirstVisiblePoint - 1);
								yButterSeries.removeLast();
								yButterSeries.addFirst(null, d);

								d = zButterVals.get(indexOfFirstVisiblePoint - 1);
								zButterSeries.removeLast();
								zButterSeries.addFirst(null, d);

								indexOfFirstVisiblePoint--;
								bar--;
							}
							bar = foo - (int) minX;
							while (bar <= -1) {
								//right to left
								if (x_vals == null || indexOfFirstVisiblePoint + DRAW_SIZE + 1 >= x_vals.size())
									break;

								double d;
								//d = x_vals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								//x_series.removeFirst();
								//x_series.addLast(null, d);

								//d = y_vals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								//y_series.removeFirst();
								//y_series.addLast(null, d);

								//d = z_vals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								//z_series.removeFirst();
								//z_series.addLast(null, d);

								d = xButterVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								xButterSeries.removeFirst();
								xButterSeries.addLast(null, d);

								d = yButterVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								yButterSeries.removeFirst();
								yButterSeries.addLast(null, d);

								d = zButterVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								zButterSeries.removeFirst();
								zButterSeries.addLast(null, d);

								indexOfFirstVisiblePoint++;
								bar++;
							}
							plot.redraw();

							break;
					}
				}
				return true;
			}
		};


		plot.setOnTouchListener(myOnTouchListener);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mShimmerDevice1 = new Shimmer(this, mHandler,"uppertorso",10,0,4,Shimmer.SENSOR_ACCEL,true); //Right Arm is a unique identifier for the shimmer unit
	//TRY CHANGE 10 TO SOMETHING ELSE (eg 51.2) - NOTE: already tried this, doesn't seem to work
		startGraph();

    }



	protected void onSurfaceChanged()
	{
		plot.redraw();
	}


 // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @SuppressWarnings("null")
		public void handleMessage(Message msg) {
            switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
            case Shimmer.MESSAGE_READ:
        		String[] signalNameArray=new String[4]; 
        		String myAddress="";
        		double[] dataValues=new double[4]; 
            	if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
            	    log.logData(objectCluster);
            	    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Accelerometer X");  // first retrieve all the possible formats for the current sensor device
					FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster!=null){
						Log.d("CalibratedData",objectCluster.mMyName + " AccelX: " + formatCluster.mData + " "+ formatCluster.mUnits);
					}
					Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster!=null){
						Log.d("CalibratedData",objectCluster.mMyName + " AccelY: " + formatCluster.mData + " "+formatCluster.mUnits);
					}
					Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster!=null){
						Log.d("CalibratedData",objectCluster.mMyName + " AccelZ: " + formatCluster.mData + " "+formatCluster.mUnits);
					}


					accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster != null){
						Log.d("CalibratedData", objectCluster.mMyName + " AccelLNX: " + formatCluster.mData + " " + formatCluster.mUnits);
						x_vals.add(formatCluster.mData);

						/* FILTERING STUFF */
						double filtered = filterButter(formatCluster.mData); //TODO change this back to normal
						xButterVals.add(filtered);
						x_txt.setText(filtered + "");
						//reaction(xButterVals);

						/* MY ARRAY IDEA */
						double sum = 0;
						int els = 0; //the number of elements
						int firstIndex = xButterVals.size() - 1;
						if (firstIndex < 0)
						{
							firstIndex = 0; //to ensure no negative indexing
						}
						for (int i = firstIndex; (els < MINI_ARRAY_SIZE) && (i >=0); i--)
						{
							sum+=xButterVals.get(i);
							els++;
						}
						double average = sum/((double) els);
						repCount(filtered, average); //use average of previous few data to get trend



					}
					accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster != null) {
						Log.d("CalibratedData", objectCluster.mMyName + " AccelLNY: " + formatCluster.mData + " " + formatCluster.mUnits);
						y_vals.add(formatCluster.mData);

						/* FILTERING STUFF */
						double filtered = filterButter(formatCluster.mData);
						yButterVals.add(filtered);
						y_txt.setText(filtered + "");

					}
					accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
					formatCluster = ((FormatCluster) ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
					if (formatCluster != null) {
						Log.d("CalibratedData", objectCluster.mMyName + " AccelLNZ: " + formatCluster.mData + " "+formatCluster.mUnits);
						z_vals.add(formatCluster.mData);

						/* FILTERING STUFF */
						double filtered = filterButter(formatCluster.mData);
						zButterVals.add(filtered);
						z_txt.setText(filtered + "");

						continueGraph(); //redraw the graph once per cycle (when the z data is collected)

					}

            	}
            	
                break;
                 case Shimmer.MESSAGE_TOAST:
                	Log.d("toast",msg.getData().getString(Shimmer.TOAST));
                	Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),
                            Toast.LENGTH_SHORT).show();
                break;
                 case Shimmer.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
             	case Shimmer.MSG_STATE_FULLY_INITIALIZED:
            	    if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED){
            	        Log.d("ConnectionStatus","Successful");
            	        mShimmerDevice1.startStreaming();
            	        shimmerTimer(SAMPLE_SIZE); //Disconnect in 30 seconds
            	     }
            	    break;
                case Shimmer.STATE_CONNECTING:
                	Log.d("ConnectionStatus","Connecting");
        	        break;
                case Shimmer.STATE_NONE:
                	Log.d("ConnectionStatus","No State");
                	break;
             }
        break;
            }
        }
    };

	  public synchronized void shimmerTimer(int seconds) {
	        mTimer = new Timer();
	        mTimer.schedule(new responseTask(), seconds * 1000);
	        
		}
	    
	    class responseTask extends TimerTask {
	        public void run() {
	        	stop=true;
	        	log.closeFile();
				mShimmerDevice1.stop();
				plotting = false;



	        }
	    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent;
		switch (item.getItemId()) {

			case R.id.itemConnect:
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_SHIMMER);

				return true;

			case R.id.itemDisconnect:
				mShimmerDevice1.stop();
				plotting = false;
				return true;
			case R.id.itemConfigure:
				serverIntent = new Intent(this, ConfigureActivity.class);
				serverIntent.putExtra("LowPowerMag",mShimmerDevice1.isLowPowerMagEnabled());
				serverIntent.putExtra("GyroOnTheFlyCal",mShimmerDevice1.isGyroOnTheFlyCalEnabled());
				startActivityForResult(serverIntent, REQUEST_CONFIGURE_SHIMMER);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

			case REQUEST_CONNECT_SHIMMER:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					if (mShimmerDevice1.getStreamingStatus()==true){
						mShimmerDevice1.stop();
						plotting = false;
					} else {
						String bluetoothAddress= data.getExtras()
								.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						mShimmerDevice1.connect(bluetoothAddress,"default");
					}
				}
				break;

			case REQUEST_CONFIGURE_SHIMMER:
				if (resultCode == Activity.RESULT_OK) {
					if (data.getExtras().getString("Command").equals("Mag")){
						if (mShimmerDevice1.getStreamingStatus()){
							mShimmerDevice1.stopStreaming();
							mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
							mShimmerDevice1.startStreaming();
						} else {
							mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
						}
					} else if (data.getExtras().getString("Command").equals("Gyro")) {
						mShimmerDevice1.enableOnTheFlyGyroCal(data.getExtras().getBoolean("Enable"), 100, 1.2);
					}
				}
				break;
		}
	}

	public void startGraph()
	{

		//format stuff
		x_seriesFormat = new LineAndPointFormatter(Color.rgb(255, 255, 255), null, null, null);
		y_seriesFormat = new LineAndPointFormatter(Color.rgb(0, 0, 255), null, null, null);
		z_seriesFormat = new LineAndPointFormatter(Color.rgb(255, 0, 0), null, null, null);

		xButterSeriesFormat = new LineAndPointFormatter(Color.rgb(0,255,0), null, null, null);
		yButterSeriesFormat = new LineAndPointFormatter(Color.rgb(0,0,255), null, null, null);
		zButterSeriesFormat = new LineAndPointFormatter(Color.rgb(255,0,0), null, null, null);

		x_series = new SimpleXYSeries(x_vals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Accel X");
		x_series.useImplicitXVals();
		y_series = new SimpleXYSeries(y_vals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Accel Y");
		y_series.useImplicitXVals();
		z_series = new SimpleXYSeries(z_vals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Accel Z");
		z_series.useImplicitXVals();

		xButterSeries = new SimpleXYSeries(xButterVals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Butter X");
		xButterSeries.useImplicitXVals();
		yButterSeries = new SimpleXYSeries(yButterVals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Butter Y");
		yButterSeries.useImplicitXVals();
		zButterSeries = new SimpleXYSeries(zButterVals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Butter Z");
		zButterSeries.useImplicitXVals();

		// reduce the number of range labels
		plot.setTicksPerRangeLabel(3);
		plot.getGraphWidget().setDomainLabelOrientation(-45);

		plot.setDomainBoundaries(0, SAMPLE_SIZE, BoundaryMode.FIXED);
		plot.setRangeBoundaries(-3, 3, BoundaryMode.FIXED);
		plotting = true;

	}

	public void continueGraph()
	{
		//do the plot here (after stopping collecting data)
		count++;

		plot.clear();

		int minimum_x = plot.getCalculatedMinX().intValue();

		Log.d("MIN X", "minimum_x = " + minimum_x + ", count = " + count);
		if (count >= SAMPLE_SIZE) {
			plot.setDomainBoundaries(minimum_x + 1, minimum_x + SAMPLE_SIZE + 1, BoundaryMode.FIXED);
			indexOfFirstVisiblePoint++;
		}

		//x_series.addLast(null, x_vals.get(x_vals.size() - 1));
		//y_series.addLast(null, y_vals.get(y_vals.size()-1));
		//z_series.addLast(null, z_vals.get(z_vals.size() - 1));

		xButterSeries.addLast(null, xButterVals.get(xButterVals.size()-1));
		yButterSeries.addLast(null, yButterVals.get(yButterVals.size()-1));
		zButterSeries.addLast(null, zButterVals.get(zButterVals.size()-1));


		//add a new series' to the xyplot:
		//plot.addSeries(x_series, x_seriesFormat);
		//plot.addSeries(y_series, y_seriesFormat);
		//plot.addSeries(z_series, z_seriesFormat);

		plot.addSeries(xButterSeries, xButterSeriesFormat);
		plot.addSeries(yButterSeries, yButterSeriesFormat);
		plot.addSeries(zButterSeries, zButterSeriesFormat);

		// reduce the number of range labels
		plot.setTicksPerRangeLabel(3);
		plot.getGraphWidget().setDomainLabelOrientation(-45);
		plot.redraw();
	}


	/*
	Recurrence relation:
y[n] = (  1 * x[n- 8])
     + (  8 * x[n- 7])
     + ( 28 * x[n- 6])
     + ( 56 * x[n- 5])
     + ( 70 * x[n- 4])
     + ( 56 * x[n- 3])
     + ( 28 * x[n- 2])
     + (  8 * x[n- 1])
     + (  1 * x[n- 0])

     + ( -0.0008613684 * y[n- 8])
     + (  0.0122466702 * y[n- 7])
     + ( -0.0820901316 * y[n- 6])
     + (  0.3191759433 * y[n- 5])
     + ( -0.8694409155 * y[n- 4])
     + (  1.5326255633 * y[n- 3])
     + ( -2.0838133003 * y[n- 2])
     + (  1.5905664958 * y[n- 1])
	 */

	private double filterButter(double xData) //function to do butterworth filtering (n=8, fc=20Hz) (http://www-users.cs.york.ac.uk/~fisher/cgi-bin/mkfscript)
	{

		final int NUMBER_POLES = 8;
		final int NUMBER_ZEROS = 8;
		final double GAIN =  6.097498035e+00;


		double [] xv;
		double [] yv;
		xv = new double[NUMBER_POLES+1];
		yv = new double[NUMBER_ZEROS+1];

		xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; xv[4] = xv[5]; xv[5] = xv[6]; xv[6] = xv[7]; xv[7] = xv[8];
		xv[8] = xData / GAIN; //MIGHT BE WRONG
		yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4]; yv[4] = yv[5]; yv[5] = yv[6]; yv[6] = yv[7]; yv[7] = yv[8];
		yv[8] =   (xv[0] + xv[8]) + 8 * (xv[1] + xv[7]) + 28 * (xv[2] + xv[6])
				+ 56 * (xv[3] + xv[5]) + 70 * xv[4]
				+ ( -0.0268965572 * yv[0]) + ( -0.3097392114 * yv[1])
				+ ( -1.5888819744 * yv[2]) + ( -4.7528602453 * yv[3])
				+ ( -9.0940029435 * yv[4]) + (-11.4399227870 * yv[5])
				+ ( -9.2880746605 * yv[6]) + ( -4.4840549977 * yv[7]);

		return yv[8];
	}

	private void reaction(ArrayList<Double> data)//the phone reacts to the data
	{
		if (data.size()<10)
		{
			//do nothing at first
		}
		else if (data.get(data.size()-1) > TOO_BIG || data.get(data.size()-1) < TOO_SMALL)
		{
			check_txt.setText("BAD");
			check_txt.setBackgroundColor(Color.parseColor("#ffff3329"));
			toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);//comment out these two lines if you want it to be quiet
			v.vibrate(500);
		}
		else {
			check_txt.setText("GOOD");
			check_txt.setBackgroundColor(Color.parseColor("#ff4189ff"));
		}
	}

	private void repCount(double data, double lastData)
	{
		final double UPPER = 1.4;
		final double LOWER = 0.1;
		final int MAX_TIME_IN_STATE = 25;

		switch (state)
		{
			case start:
				displayState.setText("STATE = START");
				displayTime.setText(Integer.toString(timeInState));
				if (data > lastData)
				{
					//we are going up
					state = State.goingUp;
					timeInState = 0;
				}
				else
				{
					//we are staying down
					state = State.down;
					timeInState = 0;
				}
				timeInState++;
				break;
			case goingUp:
				displayState.setText("STATE = GOING UP");
				displayTime.setText(Integer.toString(timeInState));
				if (data > UPPER)
				{
					//we're already all the way up
					state = State.up;
					timeInState = 0;
				}
				else if ((data < lastData * 0.90) || (timeInState > MAX_TIME_IN_STATE)) //0.90 to account for possible error
				{
					//going back down again, you have failed OR spent too much time in this state
					state = State.fail;
					timeInState = 0;
				}
				timeInState++;
				break;
			case up:
				displayState.setText("STATE = UP");
				displayTime.setText(Integer.toString(timeInState));
				if (timeInState > MAX_TIME_IN_STATE)
				{
					state = State.fail;
					timeInState = 0;
				}
				else if (data > UPPER)
				{
					//still in same state, do nothing
				}
				else if (data < lastData)
				{
					//we're going back down
					state = State.goingDown;
					timeInState = 0;
				}
				timeInState++;
				break;
			case goingDown:
				displayState.setText("STATE = GOING DOWN");
				displayTime.setText(Integer.toString(timeInState));
				if (data < LOWER)
				{
					//we're all the way down, completed a full rep
					state = State.down;
					timeInState = 0;
					numReps++;
					displayCount.setText(Integer.toString(numReps));
				}
				else if ((data > lastData * 1.10) || (timeInState > MAX_TIME_IN_STATE)) //1.10 to account for possible error
				{
					//going back up again, you have failed
					state = State.fail;
					timeInState = 0;
				}
				timeInState++;
				break;
			case down:
				displayState.setText("STATE = DOWN");
				displayTime.setText(Integer.toString(timeInState));
				if (timeInState > MAX_TIME_IN_STATE)
				{
					state = State.fail;
					timeInState = 0;
				}
				else if (data < LOWER)
				{
					//still in DOWN state, do nothing
				}
				else if (data > lastData)
				{
					//we are going up
					state = State.goingUp;
					timeInState = 0;
				}
				timeInState++;
				break;
			case fail: //maybe when you fail, numReps should go to zero...
				displayState.setText("STATE = FAIL");
				displayTime.setText(Integer.toString(timeInState));
				if (data < LOWER)
				{
					//you have gotten back to the beginning stage, ie DOWN, so start again
					state = State.down;
					timeInState = 0;
				}
				timeInState++;
				break;

		}

	}

	}