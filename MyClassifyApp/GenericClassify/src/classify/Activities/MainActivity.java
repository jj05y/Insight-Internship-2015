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


package classify.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cmw.R;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;

import classify.Adapters.DrawerListAdapter;
import classify.Constants.C;
import classify.DatabaseClasses.DatabaseHandler;
import classify.Fragments.ChooseSignalsFragment;
import classify.Fragments.ClassifyFragment;
import classify.Fragments.ConnectFragment;
import classify.Fragments.ManageDBFragment;
import classify.Fragments.RecordFragment;
import classify.Fragments.ReviewByExLabelFragment;
import classify.Fragments.ReviewByNameFragment;
import classify.Interfaces.Linker;
import classify.ListItems.NavItem;
import classify.ObjectClasses.DataPacket;

public class MainActivity extends Activity implements Linker {

    private ArrayList<NavItem> navItems;
    private DrawerLayout drawerLayout;
    private ListView navList;
    private DrawerListAdapter drawerListAdapter;
    private ActionBarDrawerToggle drawerToggle;

    private HashMap<String, String> addressesMap;
    private HashMap<String, Shimmer> shimmersMap;
    private HashMap<String, Boolean> plotSignalsMap;
    private HashMap<String, Boolean> plotSensorsMap;

    private ConnectFragment connectFragment;
    private RecordFragment recordFragment;
    private ManageDBFragment manageDBFragment;
    private ChooseSignalsFragment settingsFragment;
    private ReviewByExLabelFragment reviewByExLabelFragment;
    private ReviewByNameFragment reviewByNameFragment;
    private ClassifyFragment classifyFragment;

    private DatabaseHandler db;

    private boolean isPlotting;
    private boolean isTestStreaming;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.setTooLegacyObjectClusterSensorNames();
        setContentView(R.layout.activity_main);

        navItems = new ArrayList<>();
        navItems.add(new NavItem("Set Up Sensors", R.drawable.ic_shimmer));
        navItems.add(new NavItem("Choose Signals", R.drawable.ic_signal));
        navItems.add(new NavItem("Record a New Session", R.drawable.ic_record));
        navItems.add(new NavItem("Review by Name", R.drawable.ic_review));
        navItems.add(new NavItem("Review by Exercise/Label", R.drawable.ic_tag));
        navItems.add(new NavItem("Manage Database", R.drawable.ic_manage_db));
        navItems.add(new NavItem("Classify", R.drawable.ic_greencircle));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_hamburger, 0, 0); //option to override on open and closed here,
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        navList = (ListView) findViewById(R.id.nav_drawer);
        drawerListAdapter = new DrawerListAdapter(this, navItems);
        navList.setAdapter(drawerListAdapter);
        navList.setOnItemClickListener(new DrawItemClickListener());
        navList.setBackgroundResource(R.color.White);

        drawerLayout.openDrawer(navList);


        getSavedSensorAddresses();
        createShimmerMap();
        setUpSignalMaps();
        isPlotting = false;
        isTestStreaming = false;

        connectFragment = new ConnectFragment();
        recordFragment = new RecordFragment();
        settingsFragment = new ChooseSignalsFragment();
        manageDBFragment = new ManageDBFragment();
        reviewByExLabelFragment = new ReviewByExLabelFragment();
        reviewByNameFragment = new ReviewByNameFragment();
        classifyFragment = new ClassifyFragment();

        db = new DatabaseHandler(this);

    }
    private void setUpSignalMaps() {
        //these values need to correspond to those set as intially checked in fragment_connect.xml
        plotSignalsMap = new HashMap<>();
        plotSignalsMap.put(C.ACCEL_MAG, true);
        plotSignalsMap.put(C.ACCEL_X, false);
        plotSignalsMap.put(C.ACCEL_Y, false);
        plotSignalsMap.put(C.ACCEL_Z, false);
        plotSignalsMap.put(C.PITCH, false);
        plotSignalsMap.put(C.ROLL, false);
        plotSignalsMap.put(C.YAW, false);
        plotSignalsMap.put(C.GYRO_Z, false);
        plotSignalsMap.put(C.GYRO_Y, false);
        plotSignalsMap.put(C.GYRO_X, false);
        plotSignalsMap.put(C.GYRO_MAG, false);
        plotSignalsMap.put(C.QUAT_W, false);
        plotSignalsMap.put(C.QUAT_Z, false);
        plotSignalsMap.put(C.QUAT_Y, false);
        plotSignalsMap.put(C.QUAT_X, false);


        plotSensorsMap = new HashMap<>();
        plotSensorsMap.put(C.MAIN_SENSOR, true);


    }


    private void createShimmerMap() {
        shimmersMap = new HashMap<>();
        for (String sensor : C.SENSORS) {
            shimmersMap.put(sensor, new Shimmer(this, handler, sensor, C.SAMPLE_RATE, C.ACCEL_RANGE, C.GSR_RANGE, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_GYRO | Shimmer.SENSOR_MAG | Shimmer.SENSOR_BATT, true, false, false, false, C.GYRO_RANGE, C.MAG_RANGE));
            shimmersMap.get(sensor).enableOnTheFlyGyroCal(true, 102, 1.2);
            shimmersMap.get(sensor).enable3DOrientation(true);
        }
    }


    private void selectFragment(int pos) {

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();

        switch (pos) {
            case 0:
                fragment = connectFragment;
                break;
            case 1:
                fragment = settingsFragment;
                break;
            case 2:
                fragment = recordFragment;
                break;
            case 3:
                fragment = reviewByNameFragment;
                break;
            case 4:
                fragment = reviewByExLabelFragment;
                break;
            case 5:
                fragment = manageDBFragment;
                break;
            case 6:
                fragment = classifyFragment;
                break;
            default:
                fragment = connectFragment;
        }


        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        navList.setItemChecked(pos, true);
        setTitle(navItems.get(pos).getText());
        drawerLayout.closeDrawer(navList);

    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    private class DrawItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            selectFragment(pos);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedAddresses.edit();

        for (String sensor : C.SENSORS) {
            shimmersMap.get(sensor).stop();
            editor.putString(sensor, addressesMap.get(sensor));
        }
        editor.commit();

    }

    public void getSavedSensorAddresses() {

        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        addressesMap = new HashMap<>();
        for (String sensor : C.SENSORS) {
            addressesMap.put(sensor, savedAddresses.getString(sensor, C.DEFAULT_ADDRESS));
        }


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navList)) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            drawerLayout.openDrawer(navList);
        }
    }


    @Override
    public HashMap<String, String> getAddressesMap() {
        return addressesMap;
    }

    @Override
    public HashMap<String, Shimmer> getShimmersMap() {
        return shimmersMap;
    }



    @Override
    public DatabaseHandler getDb() {
        return db;
    }

    @Override
    public void toggleIsPlotting() {
        if (isPlotting) {
            isPlotting = false;
        } else {
            isPlotting = true;
        }
    }

    @Override
    public boolean getIsPlotting() {
        return isPlotting;
    }

    @Override
    public HashMap<String, Boolean> getPlotSensorsMap() {
        return plotSensorsMap;
    }

    @Override
    public HashMap<String, Boolean> getPlotSignalsMap() {
        return plotSignalsMap;
    }

    @Override
    public boolean getisTestStreaming() {
        return isTestStreaming;
    }

    @Override
    public void toggleTestStreaming() {
        isTestStreaming = !isTestStreaming;
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(navList);
    }

    @Override
    public String[] findSpecificLabels(int exercise) {
        String[] specificLabels;
        switch (exercise) {
            case 1: // shape exercise
                specificLabels = new String[C.SHAPE_LABEL_INDEXES.length];
                for (int i = 0; i < specificLabels.length; i++) {
                    specificLabels[i] = C.LABELS[C.SHAPE_LABEL_INDEXES[i]];
                }
                break;
            case 2:
                specificLabels = new String[C.WIGGLE_LABEL_INDEXES.length];
                for (int i = 0; i < specificLabels.length; i++) {
                    specificLabels[i] = C.LABELS[C.WIGGLE_LABEL_INDEXES[i]];
                }
                break;
            default:
                specificLabels = new String[1];
                specificLabels[0] = "NOPE";
                break;

        }
        return specificLabels;
    }

    @Override
    public int findIndex(Object label) {

        return Arrays.asList(C.LABELS).indexOf(label);
    }


    private final Handler handler = new Handler() {


        @SuppressWarnings("null")
        public void handleMessage(Message msg) {
            Shimmer shimmer;
            double[] accelMagArr = new double[3];
            double accelMag;

            double[] gyroMagArr = new double[3];
            double gyroMag;


            if ((msg.obj instanceof ObjectCluster)) {    // within each msg an object can be include, objectclusters are used to represent the data structure of the leftThighShimmer device
                ObjectCluster objectCluster = (ObjectCluster) msg.obj;
                String sensor = objectCluster.mMyName;
                //we're dealing with sensor "sensor" :)
                shimmer = shimmersMap.get(sensor);
                switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
                    case Shimmer.MESSAGE_READ:
                        DataPacket data = new DataPacket();
                        Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
                        FormatCluster formatCluster = ObjectCluster.returnFormatCluster(accelXFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isTestStreaming || isPlotting) data.setAccelX(formatCluster.mData);
                            accelMagArr[0] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelYFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {

                            if (isTestStreaming || isPlotting) data.setAccelY(formatCluster.mData);
                            accelMagArr[1] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelZFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isTestStreaming || isPlotting) data.setAccelZ(formatCluster.mData);
                            accelMagArr[2] = formatCluster.mData * formatCluster.mData;
                        }

                        accelMag = Math.sqrt(accelMagArr[0] + accelMagArr[1] + accelMagArr[2]);
                        if (isTestStreaming || isPlotting) data.setAccelMag(accelMag);



                        //PITCH ROLL AND YAW
                        float angle = 0, x = 0, y = 0, z = 0;
                        Collection<FormatCluster> angleAFormats = objectCluster.mPropertyCluster.get("Axis Angle A");
                        formatCluster = ObjectCluster.returnFormatCluster(angleAFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            angle = (float) formatCluster.mData;
                        }
                        Collection<FormatCluster> angleXFormats = objectCluster.mPropertyCluster.get("Axis Angle X");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(angleXFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            x = (float) formatCluster.mData;
                        }
                        Collection<FormatCluster> angleYFormats = objectCluster.mPropertyCluster.get("Axis Angle Y");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(angleYFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            y = (float) formatCluster.mData;
                        }
                        Collection<FormatCluster> angleZFormats = objectCluster.mPropertyCluster.get("Axis Angle Z");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(angleZFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            z = (float) formatCluster.mData;
                        }
                        AxisAngle4d aa = new AxisAngle4d(x, y, z, angle);
                        Quat4d qt = new Quat4d();
                        qt.set(aa);
                        //Formula to find PitchRollYaw from quats applied
                        double pitch = Math.asin(-2 * (qt.x * qt.z - qt.w * qt.y));
                        double roll = Math.atan2(2 * (qt.x * qt.y + qt.w * qt.z), (qt.w * qt.w + qt.x * qt.x - qt.y * qt.y - qt.z * qt.z));
                        double yaw = Math.atan2(2 * (qt.y * qt.z + qt.w * qt.x), qt.w * qt.w - qt.x * qt.x - qt.y * qt.y + qt.z * qt.z);

                        pitch = (180 * pitch) / Math.PI;
                        if (isTestStreaming || isPlotting) data.setPitch(pitch);
                        roll = (180 * roll) / Math.PI;
                        if (isTestStreaming || isPlotting) data.setRoll(roll);
                        yaw = (180 * yaw) / Math.PI;
                        if (isTestStreaming || isPlotting) data.setYaw(yaw);

                        if (isTestStreaming || isPlotting) data.setQuatW(qt.w);
                        if (isTestStreaming || isPlotting) data.setQuatX(qt.x);
                        if (isTestStreaming || isPlotting) data.setQuatY(qt.y);
                        if (isTestStreaming || isPlotting) data.setQuatZ(qt.z);


                        //GYRO
                        Collection<FormatCluster> gyroXFormats = objectCluster.mPropertyCluster.get("Gyroscope X");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(gyroXFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isTestStreaming || isPlotting) data.setGyroX(formatCluster.mData);
                            gyroMagArr[0] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> gyroYFormats = objectCluster.mPropertyCluster.get("Gyroscope Y");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(gyroYFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {

                            if (isTestStreaming || isPlotting) data.setGyroY(formatCluster.mData);
                            gyroMagArr[1] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> gyroZFormats = objectCluster.mPropertyCluster.get("Gyroscope Z");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(gyroZFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isTestStreaming || isPlotting) data.setGyroZ(formatCluster.mData);
                            gyroMagArr[2] = formatCluster.mData * formatCluster.mData;
                        }

                        gyroMag = Math.sqrt(gyroMagArr[0] + gyroMagArr[1] + gyroMagArr[2]);
                        if (isTestStreaming || isPlotting) data.setGyroMag(gyroMag);

                        //BATT
                        Collection<FormatCluster> battFormats = objectCluster.mPropertyCluster.get("VSenseBatt");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(battFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isTestStreaming || isPlotting) data.setBatt(formatCluster.mData);
                        }

                        // Log.d("PRY", "pitch: " + pitch + "\troll: " + roll + "\tyaw: " + yaw);

                        if (isTestStreaming) connectFragment.sortTestStream(data);
                        if (isPlotting) recordFragment.addToPoints(sensor, data);


                        break;
                    case Shimmer.MESSAGE_TOAST:
                        Toast.makeText(getBaseContext(), msg.getData().getString(Shimmer.TOAST),
                                Toast.LENGTH_SHORT).show();
                        //Log.d("objtest", "objtest winning in MESSAGE TOAST");

                        break;
                    case Shimmer.MESSAGE_STATE_CHANGE:
                        //Log.d("objtest", "obtest winning in MESSAGE CHANGE");

                        switch (msg.arg1) {
                            case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                                //Log.d("objtest", "objtest winning in STATE FULLY_INITIALIZED");
                                if (shimmer.getShimmerState() == Shimmer.STATE_CONNECTED) {
                                    //Log.d("objtest", "objtest winning in STATE CONNECTED");

                                    Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();

                                    //Log.d(null, "putting into isConnected Map true: " + key);
                                    connectFragment.redrawLocalIndicators();
                                    connectFragment.setButtonTexts();
                                }
                                break;
                            case Shimmer.STATE_CONNECTING:
                                //Log.d("objtest", "objtest winning in STATE CONNECTING");

                                Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
                                break;
                            case Shimmer.STATE_NONE:
                                //Log.d("objtest", "objtest winning in STATE NONE");

                                Toast.makeText(getBaseContext(), "No State", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                }
            }

        }

    };
}
    



    
    