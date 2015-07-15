package com.shimmerresearch.MultiShimmerRecordReview.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shimmerresearch.MultiShimmerRecordReview.Adapters.DrawerListAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ChooseSignalsFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ConnectFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ManageDBFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.RecordFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ReviewByExLabelFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ReviewByNameFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.NavItem;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;

public class MainActivity extends Activity implements Linker {


    private ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private ConnectFragment connectFragment;
    private RecordFragment recordFragment;
    private ReviewByNameFragment reviewByNameFragment;
    private ReviewByExLabelFragment reviewByExLabelFragment;
    private ManageDBFragment manageDBFragment;
    private Fragment settingsFragment;

    ArrayList<NavItem> navItems;
    private HashMap<String, String> addressesMap;
    private HashMap<String, Shimmer> shimmersMap;
    private HashMap<String, Boolean> isConnectedMap;
    private HashMap<String, Boolean> plotSensorsMap;
    private HashMap<String, Boolean> plotSignalsMap;

    private DatabaseHandler db;
    private boolean isPlotting;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Configuration.setTooLegacyObjectClusterSensorNames();


        getSavedSensorAddresses();
        createShimmerMap();
        createIsConnectedMap();
        setUpSignalMaps();


        db = new DatabaseHandler(this);
        isPlotting = Boolean.FALSE;

        navItems = new ArrayList<>();
        navItems.add(new NavItem("Set Up Sensors", R.drawable.ic_shimmer));
        navItems.add(new NavItem("Choose Signals", R.drawable.ic_signal));
        navItems.add(new NavItem("Record a New Session", R.drawable.ic_record));
        navItems.add(new NavItem("Review by Name", R.drawable.ic_review));
        navItems.add(new NavItem("Review by Exercise/Label", R.drawable.ic_tag));
        navItems.add(new NavItem("Manage Database", R.drawable.ic_manage_db));


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        drawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter drawerListAdapter = new DrawerListAdapter(this, navItems);
        drawerList.setAdapter(drawerListAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectItemFromDrawer(i);
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_hamburger, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();

            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.openDrawer(drawerPane);

        connectFragment = new ConnectFragment();
        recordFragment = new RecordFragment();
        reviewByNameFragment = new ReviewByNameFragment();
        reviewByExLabelFragment = new ReviewByExLabelFragment();
        settingsFragment = new ChooseSignalsFragment();
        manageDBFragment = new ManageDBFragment();


    }

    private void setUpSignalMaps() {
        plotSensorsMap = new HashMap<>();
        plotSignalsMap = new HashMap<>();

        plotSensorsMap.put(C.LOWER_BACK, true);
        plotSensorsMap.put(C.LEFT_THIGH, false);
        plotSensorsMap.put(C.LEFT_CALF, false);
        plotSensorsMap.put(C.RIGHT_THIGH, false);
        plotSensorsMap.put(C.RIGHT_CALF, false);

        plotSignalsMap.put(C.ACCEL_MAG, true);
        plotSignalsMap.put(C.ACCEL_X, false);
        plotSignalsMap.put(C.ACCEL_Y, false);
        plotSignalsMap.put(C.ACCEL_Z, false);
        plotSignalsMap.put(C.PITCH, false);
        plotSignalsMap.put(C.ROLL, false);
        plotSignalsMap.put(C.YAW, false);


    }


    private void createIsConnectedMap() {
        isConnectedMap = new HashMap<>();
        isConnectedMap.put(C.LEFT_THIGH, false);
        isConnectedMap.put(C.LEFT_CALF, false);
        isConnectedMap.put(C.RIGHT_THIGH, false);
        isConnectedMap.put(C.RIGHT_CALF, false);
        isConnectedMap.put(C.LOWER_BACK, false);

    }
// C.SAMPLE_RATE, 0, 4, ,

    private void createShimmerMap() {
        shimmersMap = new HashMap<>();
        for (String sensor : C.SENSORS) {
            shimmersMap.put(sensor, new Shimmer(this, handler, sensor, C.SAMPLE_RATE, C.ACCEL_RANGE, C.GSR_RANGE, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_GYRO | Shimmer.SENSOR_MAG, true, false, false, false, C.GYRO_RANGE, C.MAG_RANGE));
            shimmersMap.get(sensor).enableOnTheFlyGyroCal(true, 102, 1.2);
            shimmersMap.get(sensor).enable3DOrientation(true);
        }
    }


    private void selectItemFromDrawer(int i) {

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();

        switch (i) {
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
            default:
                fragment = null;
        }


        fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();

        drawerList.setItemChecked(i, true);
        setTitle(navItems.get(i).getText());
        drawerLayout.closeDrawer(drawerPane);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                if (drawerLayout.isDrawerOpen(drawerPane)) {
                    drawerLayout.closeDrawer(drawerPane);
                } else {
                    drawerLayout.openDrawer(drawerPane);
                }
                break;
            default:
                //nope
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
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
    public HashMap<String, Boolean> getIsConnectedMap() {
        return isConnectedMap;
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


    public void getSavedSensorAddresses() {

        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        addressesMap = new HashMap<>();
        addressesMap.put(C.LEFT_THIGH, savedAddresses.getString(C.LEFT_THIGH, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(C.RIGHT_THIGH, savedAddresses.getString(C.RIGHT_THIGH, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(C.LEFT_CALF, savedAddresses.getString(C.LEFT_CALF, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(C.RIGHT_CALF, savedAddresses.getString(C.RIGHT_CALF, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(C.LOWER_BACK, savedAddresses.getString(C.LOWER_BACK, ConnectFragment.DEFAULT_ADDRESS));


    }

    @Override
    public void onStop() {
        super.onStop();

        shimmersMap.get(C.LEFT_THIGH).stop();
        shimmersMap.get(C.LEFT_CALF).stop();
        shimmersMap.get(C.RIGHT_THIGH).stop();
        shimmersMap.get(C.RIGHT_CALF).stop();
        shimmersMap.get(C.LOWER_BACK).stop();


        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedAddresses.edit();
        editor.putString(C.LEFT_THIGH, addressesMap.get(C.LEFT_THIGH));
        editor.putString(C.LEFT_CALF, addressesMap.get(C.LEFT_CALF));
        editor.putString(C.RIGHT_THIGH, addressesMap.get(C.RIGHT_THIGH));
        editor.putString(C.RIGHT_CALF, addressesMap.get(C.RIGHT_CALF));
        editor.putString(C.LOWER_BACK, addressesMap.get(C.LOWER_BACK));
        editor.commit();

    }

    @Override
    protected void onResume() {
        //todo,,, verify sensors
        Log.d("resume", "resuming");


        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerPane)) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            drawerLayout.openDrawer(drawerPane);
        }
    }

    //handler re-write without loop :) phwaar if this works! #ballsy -
    private final Handler handler = new Handler() {


        @SuppressWarnings("null")
        public void handleMessage(Message msg) {
            Shimmer shimmer;
            double[] magArr = new double[3];
            double mag;


            if ((msg.obj instanceof ObjectCluster)) {    // within each msg an object can be include, objectclusters are used to represent the data structure of the leftThighShimmer device
                ObjectCluster objectCluster = (ObjectCluster) msg.obj;
                String sensor = objectCluster.mMyName;
                //we're dealing with sensor "sensor" :)
                shimmer = shimmersMap.get(sensor);
                switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
                    case Shimmer.MESSAGE_READ:

                        Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
                        FormatCluster formatCluster = ObjectCluster.returnFormatCluster(accelXFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isPlotting)
                                recordFragment.addToPoints(formatCluster.mData, sensor, C.ACCEL_X);
                            magArr[0] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelYFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isPlotting)
                                recordFragment.addToPoints(formatCluster.mData, sensor, C.ACCEL_Y);
                            magArr[1] = formatCluster.mData * formatCluster.mData;
                        }

                        Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
                        formatCluster = ObjectCluster.returnFormatCluster(accelZFormats, "CAL"); // retrieve the calibrated data
                        if (formatCluster != null) {
                            if (isPlotting)
                                recordFragment.addToPoints(formatCluster.mData, sensor, C.ACCEL_Z);
                            magArr[2] = formatCluster.mData * formatCluster.mData;
                        }

                        mag = Math.sqrt(magArr[0] + magArr[1] + magArr[2]);
                        if (isPlotting) recordFragment.addToPoints(mag, sensor, C.ACCEL_MAG);


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
                        if (isPlotting) recordFragment.addToPoints(pitch, sensor, C.PITCH);


                        roll = (180 * roll) / Math.PI;
                        if (isPlotting) recordFragment.addToPoints(roll, sensor, C.ROLL);

                        yaw = (180 * yaw) / Math.PI;
                        if (isPlotting) recordFragment.addToPoints(yaw, sensor, C.YAW);

                        // Log.d("PRY", "pitch: " + pitch + "\troll: " + roll + "\tyaw: " + yaw);


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

                                    isConnectedMap.put(sensor, true);
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


