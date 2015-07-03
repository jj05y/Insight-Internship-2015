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

import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ConnectFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.RecordFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Fragments.ReviewFragment;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.Util.DataBaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.Util.DrawerListAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Util.NavItem;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends Activity implements Linker {


    private ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private ConnectFragment connectFragment;
    private RecordFragment recordFragment;
    private ReviewFragment reviewFragment;


    ArrayList<NavItem> navItems;
    private HashMap<String, String> addressesMap;
    private HashMap<String, Shimmer> shimmersMap;
    private HashMap<String, Boolean> isConnectedMap;

    private DataBaseHandler db;
    private boolean isPlotting;


    //private HashMap<String, MenuItem> menuIndicators;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getSavedSensorAddresses();
        createShimmerMap();
        createIsConnectedMap();

        db = new DataBaseHandler(this);
        isPlotting = Boolean.FALSE;

        navItems = new ArrayList<>();
        navItems.add(new NavItem("Set Up Sensors", R.drawable.ic_shimmer));
        navItems.add(new NavItem("Record a New Session", R.drawable.ic_record));
        navItems.add(new NavItem("Review an Old Session", R.drawable.ic_review));


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
        reviewFragment = new ReviewFragment();


    }


    private void createIsConnectedMap() {
        isConnectedMap = new HashMap<>();
        isConnectedMap.put(ConnectFragment.LEFT_THIGH, false);
        isConnectedMap.put(ConnectFragment.LEFT_CALF, false);
        isConnectedMap.put(ConnectFragment.RIGHT_THIGH, false);
        isConnectedMap.put(ConnectFragment.RIGHT_CALF, false);
        isConnectedMap.put(ConnectFragment.LOWER_BACK, false);

    }

    private void createShimmerMap() {
        shimmersMap = new HashMap<>();
        shimmersMap.put(ConnectFragment.LEFT_THIGH, new Shimmer(this, mHandler, ConnectFragment.LEFT_THIGH, 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true));
        shimmersMap.put(ConnectFragment.LEFT_CALF, new Shimmer(this, mHandler, ConnectFragment.LEFT_CALF, 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true));
        shimmersMap.put(ConnectFragment.RIGHT_THIGH, new Shimmer(this, mHandler, ConnectFragment.RIGHT_THIGH, 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true));
        shimmersMap.put(ConnectFragment.RIGHT_CALF, new Shimmer(this, mHandler, ConnectFragment.RIGHT_CALF, 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true));
        shimmersMap.put(ConnectFragment.LOWER_BACK, new Shimmer(this, mHandler, ConnectFragment.LOWER_BACK, 10, 0, 4, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_BATT, true));
    }


    private void selectItemFromDrawer(int i) {

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();

        switch (i) {
            case 0:
                fragment = connectFragment;
                break;
            case 1:
                fragment = recordFragment;
                break;
            case 2:
                fragment = reviewFragment;
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

/*    private void createMenuIndicators(Menu menu) {
        menuIndicators = new HashMap<>();
        menuIndicators.put(ConnectFragment.LEFT_THIGH, (MenuItem) menu.findItem(R.id.left_thigh_indicator));
        menuIndicators.put(ConnectFragment.LEFT_CALF, (MenuItem) menu.findItem(R.id.left_calf_indicator));
        menuIndicators.put(ConnectFragment.RIGHT_THIGH, menu.findItem(R.id.right_thigh_indicator));
        menuIndicators.put(ConnectFragment.RIGHT_CALF, menu.findItem(R.id.right_calf_indicator));
        menuIndicators.put(ConnectFragment.LOWER_BACK, menu.findItem(R.id.lower_back_indicator));
    }*/

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
    public DataBaseHandler getDb() {
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

    public void getSavedSensorAddresses() {

        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        addressesMap = new HashMap<>();
        addressesMap.put(ConnectFragment.LEFT_THIGH, savedAddresses.getString(ConnectFragment.LEFT_THIGH, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(ConnectFragment.RIGHT_THIGH, savedAddresses.getString(ConnectFragment.RIGHT_THIGH, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(ConnectFragment.LEFT_CALF, savedAddresses.getString(ConnectFragment.LEFT_CALF, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(ConnectFragment.RIGHT_CALF, savedAddresses.getString(ConnectFragment.RIGHT_CALF, ConnectFragment.DEFAULT_ADDRESS));
        addressesMap.put(ConnectFragment.LOWER_BACK, savedAddresses.getString(ConnectFragment.LOWER_BACK, ConnectFragment.DEFAULT_ADDRESS));


    }

    @Override
    public void onStop() {
        super.onStop();

        shimmersMap.get(ConnectFragment.LEFT_THIGH).stop();
        shimmersMap.get(ConnectFragment.LEFT_CALF).stop();
        shimmersMap.get(ConnectFragment.RIGHT_THIGH).stop();
        shimmersMap.get(ConnectFragment.RIGHT_CALF).stop();
        shimmersMap.get(ConnectFragment.LOWER_BACK).stop();


        SharedPreferences savedAddresses = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedAddresses.edit();
        editor.putString(ConnectFragment.LEFT_THIGH, addressesMap.get(ConnectFragment.LEFT_THIGH));
        editor.putString(ConnectFragment.LEFT_CALF, addressesMap.get(ConnectFragment.LEFT_CALF));
        editor.putString(ConnectFragment.RIGHT_THIGH, addressesMap.get(ConnectFragment.RIGHT_THIGH));
        editor.putString(ConnectFragment.RIGHT_CALF, addressesMap.get(ConnectFragment.RIGHT_CALF));
        editor.putString(ConnectFragment.LOWER_BACK, addressesMap.get(ConnectFragment.LOWER_BACK));
        editor.commit();

    }

    private final Handler mHandler = new Handler() {


        @SuppressWarnings("null")
        public void handleMessage(Message msg) {
            Shimmer shimmer;
            double[] rmsArr = new double[3];
            double rms;


            if ((msg.obj instanceof ObjectCluster)) {    // within each msg an object can be include, objectclusters are used to represent the data structure of the leftThighShimmer device
                ObjectCluster objectCluster = (ObjectCluster) msg.obj;


                Iterator<Map.Entry<String, String>> it = addressesMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> e = it.next();
                    String key = e.getKey();
                    String addr = e.getValue();
                    Log.d(null, "looking at sensor: " + key);
                    if (objectCluster.mBluetoothAddress.equals(addr)) {
                        //we're dealing with sensor "key" :)
                        Log.d(null, "found sensor!  " + key);
                        shimmer = shimmersMap.get(key);
                        switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
                            case Shimmer.MESSAGE_READ:
                                Log.d("objtest", "objtest winning in MESSAGE READ");

                                Collection<FormatCluster> batteryFormats = objectCluster.mPropertyCluster.get("VSenseBatt");  // first retrieve all the possible formats for the current sensor device
                                FormatCluster formatCluster = ObjectCluster.returnFormatCluster(batteryFormats, "CAL"); // retrieve the calibrated data
                                if (batteryFormats != null) {
                                    recordFragment.setBattText(" Battery(" + formatCluster.mUnits + "): " + String.format("%.2f", formatCluster.mData));
                                }

                                Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
                                formatCluster = ObjectCluster.returnFormatCluster(accelXFormats, "CAL"); // retrieve the calibrated data
                                if (formatCluster != null) {
                                    rmsArr[0] = formatCluster.mData * formatCluster.mData;
                                    Log.d(null, "rmsArr first " + rmsArr[0]);

                                }

                                Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
                                formatCluster = ObjectCluster.returnFormatCluster(accelYFormats, "CAL"); // retrieve the calibrated data
                                if (formatCluster != null) {
                                    rmsArr[1] = formatCluster.mData * formatCluster.mData;
                                    Log.d(null, "rmsArr second " + rmsArr[1]);


                                }

                                Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
                                formatCluster = ObjectCluster.returnFormatCluster(accelZFormats, "CAL"); // retrieve the calibrated data
                                if (formatCluster != null) {
                                    rmsArr[2] = formatCluster.mData * formatCluster.mData;
                                    Log.d(null, "rmsArr third " + rmsArr[2]);


                                }

                                Log.d(null, "rms Sum  = " + (rmsArr[0] + rmsArr[1] + rmsArr[2]));
                                rms = Math.sqrt(rmsArr[0] + rmsArr[1] + rmsArr[2]);
                                Log.d("rmsArr", rms + "");

                                if (isPlotting) {
                                    Log.d(null, "PLOTTINGGGGGGG");
                                    recordFragment.addToPoints(rms);
                                }


                                break;
                            case Shimmer.MESSAGE_TOAST:
                                Toast.makeText(getBaseContext(), msg.getData().getString(Shimmer.TOAST),
                                        Toast.LENGTH_SHORT).show();
                                Log.d("objtest", "objtest winning in MESSAGE TOAST");

                                break;
                            case Shimmer.MESSAGE_STATE_CHANGE:
                                Log.d("objtest", "obtest winning in MESSAGE CHANGE");

                                switch (msg.arg1) {
                                    case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                                        Log.d("objtest", "objtest winning in STATE FULLY_INITIALIZED");
                                        if (shimmer.getShimmerState() == Shimmer.STATE_CONNECTED) {
                                            Log.d("objtest", "objtest winning in STATE CONNECTED");

                                            Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();

                                            isConnectedMap.put(key, true);
                                            Log.d(null, "putting into isConnected Map true: " + key);
                                            connectFragment.redrawLocalIndicators();
                                            connectFragment.setButtonTexts();
                                            //menuIndicators.get(ConnectFragment.LEFT_THIGH).setIcon(R.drawable.ic_greencircle);

                                        }
                                        break;
                                    case Shimmer.STATE_CONNECTING:
                                        Log.d("objtest", "objtest winning in STATE CONNECTING");

                                        Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();
                                        break;
                                    case Shimmer.STATE_NONE:
                                        Log.d("objtest", "objtest winning in STATE NONE");

                                        Toast.makeText(getBaseContext(), "No State", Toast.LENGTH_SHORT).show();
                                        //menuIndicators.get(ConnectFragment.LEFT_THIGH).setIcon(R.drawable.ic_redcircle);

                                        break;
                                }
                                break;
                        }
                    }
                }
            }
        }
    };





}




/*
if (isConnectedMap.get(ConnectFragment.LEFT_THIGH)) {
        Log.d(null, "redrawing " + menuIndicators.get(ConnectFragment.LEFT_THIGH));
        menuIndicators.get(ConnectFragment.LEFT_THIGH).setIcon(R.drawable.ic_greencircle);
        } else {
        menuIndicators.get(ConnectFragment.LEFT_THIGH).setIcon(R.drawable.ic_redcircle);
        }
        if (isConnectedMap.get(ConnectFragment.LEFT_CALF)) {
        menuIndicators.get(ConnectFragment.LEFT_CALF).setIcon(R.drawable.ic_greencircle);
        } else {
        menuIndicators.get(ConnectFragment.LEFT_CALF).setIcon(R.drawable.ic_redcircle);
        }
        if (isConnectedMap.get(ConnectFragment.RIGHT_THIGH)) {
        menuIndicators.get(ConnectFragment.RIGHT_THIGH).setIcon(R.drawable.ic_greencircle);
        } else {
        menuIndicators.get(ConnectFragment.RIGHT_THIGH).setIcon(R.drawable.ic_redcircle);
        }
        if (isConnectedMap.get(ConnectFragment.LEFT_CALF)) {
        menuIndicators.get(ConnectFragment.LEFT_CALF).setIcon(R.drawable.ic_greencircle);
        } else {
        menuIndicators.get(ConnectFragment.LEFT_CALF).setIcon(R.drawable.ic_redcircle);
        }
        if (isConnectedMap.get(ConnectFragment.LOWER_BACK)) {
        menuIndicators.get(ConnectFragment.LOWER_BACK).setIcon(R.drawable.ic_greencircle);
        } else {
        menuIndicators.get(ConnectFragment.LOWER_BACK).setIcon(R.drawable.ic_redcircle);
        }



    
*/
