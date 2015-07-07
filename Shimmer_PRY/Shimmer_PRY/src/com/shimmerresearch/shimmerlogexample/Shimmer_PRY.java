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

package com.shimmerresearch.shimmerlogexample;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.androidplot.xy.SimpleXYSeries;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.app.Activity;
import com.shimmerresearch.driver.*;
import com.shimmerresearch.android.*;
import com.shimmerresearch.tools.Logging;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;

public class Shimmer_PRY extends Activity {
    /** Called when the activity is first created. */

	private final static int SAMPLE_SIZE = 154;
	private final static int DRAW_SIZE = SAMPLE_SIZE;

	private final static double TOO_BIG = 1.0;
	private final static double TOO_SMALL = -1.0;

	private final static int NUMBER_POLES = 8;
	private final static int NUMBER_ZEROS = 8;
	private final static double GAIN =  6.097498035;
	private final static int M = 20; //for the moving average filter

	private TextView mTVQ1;
	private TextView mTVQ2;
	private TextView mTVQ3;
	private TextView mTVQ4;

	private boolean plotDone =false;

	private int count = 1;
	private int timeInState;

	private ArrayList<Double> xVals;
	private ArrayList<Double> yVals;
	private ArrayList<Double> zVals;
    private ArrayList<Double> angleVals;

	private ArrayList<Double> AccelMag;

	private ArrayList<Double> PitchVals;
    private ArrayList<Double> RollVals;
    private ArrayList<Double> YawVals;

	private XYPlot plot;

	private SimpleXYSeries pitch_series;
	private SimpleXYSeries roll_series;
	private SimpleXYSeries yaw_series;
//	private SimpleXYSeries angle_series;

	private SimpleXYSeries pitchFilterSeries;
	private SimpleXYSeries rollFilterSeries;
	private SimpleXYSeries yawFilterSeries;
//	private SimpleXYSeries angleFilterSeries;

	private LineAndPointFormatter pitch_seriesFormat;
	private LineAndPointFormatter roll_seriesFormat;
	private LineAndPointFormatter yaw_seriesFormat;
//	private LineAndPointFormatter angle_seriesFormat;

//	private LineAndPointFormatter angleFilterSeriesFormat;

	PointF firstFinger;

	//extra stuff to make the plot scrollable
	private boolean plotting;

	private int indexOfFirstVisiblePoint;

	View.OnTouchListener myOnTouchListener;

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

		indexOfFirstVisiblePoint = 0;
		plotting = false;

		//To display values of quaternions on screen
		mTVQ1 = (TextView)findViewById(R.id.x_txt);
		mTVQ2 = (TextView)findViewById(R.id.y_txt);
		mTVQ3 = (TextView)findViewById(R.id.z_txt);
		mTVQ4 = (TextView)findViewById(R.id.textView);

		//To store X,Y and Z values and to be filtered
		xVals = new ArrayList<>();
		yVals = new ArrayList<>();
		zVals = new ArrayList<>();
		angleVals = new ArrayList<>();

        PitchVals = new ArrayList<>();
        RollVals = new ArrayList<>();
        YawVals = new ArrayList<>();

		plot = (XYPlot) findViewById(R.id.myResultsPlot);

		myOnTouchListener = new View.OnTouchListener() {		//To enable scrolling through the graphs
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
								if (PitchVals == null || indexOfFirstVisiblePoint <= 0) break;

								double d; //See what you did there ;)

								d = PitchVals.get(indexOfFirstVisiblePoint - 1);
								pitch_series.removeLast();
								pitch_series.addFirst(null, d);

								d = RollVals.get(indexOfFirstVisiblePoint - 1);
								roll_series.removeLast();
								roll_series.addFirst(null, d);

								d = YawVals.get(indexOfFirstVisiblePoint - 1);
								yaw_series.removeLast();
								yaw_series.addFirst(null, d);

								indexOfFirstVisiblePoint--;
								bar--;
							}
							bar = foo - (int) minX;
							while (bar <= -1) {
								//right to left
								if (PitchVals == null || indexOfFirstVisiblePoint + DRAW_SIZE + 1 >= xVals.size())
									break;

								double d;

								d = PitchVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								pitch_series.removeFirst();
								pitch_series.addLast(null, d);
								d = RollVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								roll_series.removeFirst();
								roll_series.addLast(null, d);
								d = YawVals.get(indexOfFirstVisiblePoint + DRAW_SIZE + 1);
								yaw_series.removeFirst();
								yaw_series.addLast(null, d);

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
		//mShimmerDevice1 = new Shimmer(this, mHandler, "ted", true);

		//From 3D Orientation Shimmer example
		mShimmerDevice1 = new Shimmer(this, mHandler,"RightArm",51.2, 0, 0, Shimmer.SENSOR_ACCEL|Shimmer.SENSOR_GYRO|Shimmer.SENSOR_MAG, false);
		mShimmerDevice1.enableOnTheFlyGyroCal(true, 102, 1.2); //TODO PROBLEM MIGHT BE HERE
		mShimmerDevice1.enable3DOrientation(true);

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

            	if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj;

					if (objectCluster.mMyName=="RightArm") {

						Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Axis Angle A");  // first retrieve all the possible formats for the current sensor device

                        float angle = 0, x = 0, y = 0, z = 0; //Store individual values of x,y,z and angle

                        if (accelXFormats != null) {
							FormatCluster formatCluster = ((FormatCluster) ObjectCluster.returnFormatCluster(accelXFormats, "CAL")); // retrieve the calibrated data
							angleVals.add(formatCluster.mData);
							double filteredangle = movingAverage(angleVals);
							angle = (float) filteredangle;
						}

						Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Axis Angle X");   // first retrieve all the possible formats for the current sensor device
						if (accelYFormats != null) {
							FormatCluster formatCluster = ((FormatCluster) ObjectCluster.returnFormatCluster(accelYFormats, "CAL")); // retrieve the calibrated data
							xVals.add(formatCluster.mData);
							double filteredx = movingAverage(xVals);
							x = (float) filteredx;
						}

                        Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Axis Angle Y");  // first retrieve all the possible formats for the current sensor device
                        if (accelZFormats != null){
                            FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
                            yVals.add(formatCluster.mData);
                            double filteredy = movingAverage(yVals);
                            y = (float) filteredy;
                        }
						Collection<FormatCluster> aaFormats = objectCluster.mPropertyCluster.get("Axis Angle Z");
						if (aaFormats != null) {
							FormatCluster formatCluster = ((FormatCluster) ObjectCluster.returnFormatCluster(aaFormats, "CAL")); // retrieve the calibrated data
							zVals.add(formatCluster.mData);
							double filteredz = movingAverage(zVals);
							z = (float) filteredz;

							//AccelMag.add( Math.sqrt(Math.pow(x,2)+ Math.pow(y,2) + Math.pow(z,2)));

							AxisAngle4d aa = new AxisAngle4d(x, y, z, angle); //Axis angle representation of x,y,z,and angle

							Quat4d qt = new Quat4d();

							qt.set(aa);  //Convert axis angle to quaternions

							//Formula to find PitchRollYaw from quats applied
                            double Pitch = Math.atan2(2 * (qt.y * qt.z + qt.w * qt.x), qt.w * qt.w - qt.x * qt.x - qt.y * qt.y + qt.z * qt.z);
							Pitch = (180*Pitch)/Math.PI;
                            PitchVals.add(Pitch);

                            double Yaw = Math.asin(-2 * (qt.x * qt.z - qt.w * qt.y));
							Yaw = (180*Yaw)/Math.PI;
                            YawVals.add(Yaw);

                            double Roll = Math.atan2(2 * (qt.x * qt.y + qt.w * qt.z), (qt.w * qt.w + qt.x * qt.x - qt.y * qt.y - qt.z * qt.z));
							Roll = (180*Roll)/Math.PI;
                            RollVals.add(Roll);

                            mTVQ1.setText(Double.toString(qt.w));
                            mTVQ2.setText(Double.toString(qt.x));
                            mTVQ3.setText(Double.toString(qt.y) );
                            mTVQ4.setText(Double.toString(qt.z));
						}
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
            	        shimmerTimer(30); //Disconnect in 30 seconds //
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
				if(!plotDone)
				{
					doGraph();
				}
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
				if(!plotDone)
				{
					doGraph();
				}
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
						if(!plotDone)
						{
							doGraph();
						}

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

	private void doGraph() //for plotting data at 51.2 Hz *Sean's plotting function*
	{
		indexOfFirstVisiblePoint = 0;
		//format stuff
		pitch_seriesFormat = new LineAndPointFormatter(Color.rgb(255, 0, 255), null, null, null);
		roll_seriesFormat = new LineAndPointFormatter(Color.rgb(255, 255, 0), null, null, null);
		yaw_seriesFormat = new LineAndPointFormatter(Color.rgb(0, 255, 255), null, null, null);

		pitch_series = new SimpleXYSeries(PitchVals.subList(indexOfFirstVisiblePoint, SAMPLE_SIZE), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pitch");
		pitch_series.useImplicitXVals();

		roll_series = new SimpleXYSeries(RollVals.subList(RollVals.size() - SAMPLE_SIZE, RollVals.size()), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Roll");
		roll_series.useImplicitXVals();

		yaw_series = new SimpleXYSeries(YawVals.subList(YawVals.size() - SAMPLE_SIZE, YawVals.size()), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Yaw");
		yaw_series.useImplicitXVals();

		plot.setTicksPerRangeLabel(3);
		plot.getGraphWidget().setDomainLabelOrientation(-45);

		plot.setDomainBoundaries(indexOfFirstVisiblePoint, indexOfFirstVisiblePoint + SAMPLE_SIZE, BoundaryMode.FIXED); //change back to SAMPLE_SIZE
		plot.setRangeBoundaries(-180, 180, BoundaryMode.FIXED); //this changes depending on type of data being plotted

		//add a new series' to the xyplot:
		plot.addSeries(pitch_series, pitch_seriesFormat);
		plot.addSeries(roll_series, roll_seriesFormat);
		plot.addSeries(yaw_series, yaw_seriesFormat);

		plot.redraw();
		plotDone = true;
	}

	private double movingAverage(ArrayList<Double> data) //function to do moving average filtering (M=20)
	{
		double[] xv;
		double result = 0;

		xv = new double[M];
		int start = data.size() - (M);
		if (start < 0)
			start = 0;
		List<Double> xvL = data.subList(start, data.size());
		int diff = xv.length - xvL.size();
		for (int i = 0; i < xvL.size(); i++)
		{
			xv[diff+i] = xvL.get(i);
		}
		for (int i = 0; i < xv.length; i++)
		{
			result += xv[i];
		}

		result /= M;

		return result;
	}

    }