package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shimmerresearch.MultiShimmerRecordReview.Activities.DeviceListActivity;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by joe on 22/06/15.
 */
public class ConnectFragment extends Fragment {

    public final static String DEFAULT_ADDRESS = "00:00:00:00:00:00";
    private static final int REQUEST_CONNECT_SHIMMER = 2;

    private String currentlyConnecting = "";
    private Linker linker;

    private HashMap<String, String> addresses;
    private HashMap<String, Shimmer> shimmers;
    HashMap<String, Boolean> isConnected;

    private HashMap<String, ImageView> indicators;
    private HashMap<String, TextView> addressTexts;

    private HashMap<String, Button> newButtons;
    private HashMap<String, Button> oldButtons;

    private View myInflatedView;

    public ConnectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_connect, container, false);
        linker = (Linker) getActivity();
        addresses = linker.getAddressesMap();
        shimmers = linker.getShimmersMap();
        isConnected = linker.getIsConnectedMap();

        initializeIndicators();
        initializeAddressTexts();
        initializeButtons();
        Log.d("redraw", "redrawing");
        redrawLocalIndicators();
        setButtonTexts();


        View.OnClickListener newListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clickedSensor = "";

                switch (view.getId()) {
                    case R.id.button_lt_n:
                        clickedSensor = C.LEFT_THIGH;
                        break;
                    case R.id.button_lc_n:
                        clickedSensor = C.LEFT_CALF;
                        break;
                    case R.id.button_lb_n:
                        clickedSensor = C.LOWER_BACK;
                        break;
                    case R.id.button_rt_n:
                        clickedSensor = C.RIGHT_THIGH;
                        break;
                    case R.id.button_rc_n:
                        clickedSensor = C.RIGHT_CALF;
                        break;
                }
                if (!isConnected.get(clickedSensor)) {
                    currentlyConnecting = clickedSensor; //this holds clicked sensor while device list activity is launched
                    Log.d(null, "clicked button is " + clickedSensor);
                    Intent i;
                    i = new Intent(getActivity(), DeviceListActivity.class);

                    startActivityForResult(i, REQUEST_CONNECT_SHIMMER);
                } else {
                    shimmers.get(clickedSensor).stop();
                    isConnected.put(clickedSensor, false);
                    setButtonTexts();
                    redrawLocalIndicators();
                }
            }
        };


        View.OnClickListener oldListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clickedSensor = "";

                switch (view.getId()) {
                    case R.id.button_lt_o:
                        clickedSensor = C.LEFT_THIGH;
                        break;
                    case R.id.button_lc_o:
                        clickedSensor = C.LEFT_CALF;
                        break;
                    case R.id.button_lb_o:
                        clickedSensor = C.LOWER_BACK;
                        break;
                    case R.id.button_rt_o:
                        clickedSensor = C.RIGHT_THIGH;
                        break;
                    case R.id.button_rc_o:
                        clickedSensor = C.RIGHT_CALF;
                        break;
                }
                if (!isConnected.get(clickedSensor)) {
                    Toast.makeText(getActivity(), "Connecting: " + addresses.get(clickedSensor), Toast.LENGTH_SHORT).show();
                    shimmers.get(clickedSensor).connect(addresses.get(clickedSensor), "default");
                    setButtonTexts();
                } else {
                    shimmers.get(clickedSensor).stop();
                    isConnected.put(clickedSensor, false);
                    setButtonTexts();
                    redrawLocalIndicators();
                }
            }
        };



        for (String sensor : C.SENSORS) {
            newButtons.get(sensor).setOnClickListener(newListener);
            oldButtons.get(sensor).setOnClickListener(oldListener);
        }


        Button connectAllButton = (Button) myInflatedView.findViewById(R.id.button_connect_all);
        Button disconnectAllButton = (Button) myInflatedView.findViewById(R.id.button_disconnect_all);

        connectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<Map.Entry<String, String>> it = addresses.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> e = (Map.Entry<String, String>) it.next();
                    if (!e.getValue().equals(DEFAULT_ADDRESS)) {
                        String key = e.getKey();
                        if (!isConnected.get(key)) {
                            Toast.makeText(getActivity(), "Connecting: " + addresses.get(key), Toast.LENGTH_SHORT).show();
                            shimmers.get(key).connect(addresses.get(key), "default");
                            setButtonTexts();
                        }
                    }
                }
            }
        });


        disconnectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<Map.Entry<String, Shimmer>> it = shimmers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Shimmer> e = (Map.Entry<String, Shimmer>) it.next();
                    String key = e.getKey();
                    // if (isConnected.get(key)) {
                    e.getValue().stop();
                    isConnected.put(key, false);
                    setButtonTexts();
                    redrawLocalIndicators();
                }
            }
        });

        Button forgetButton = (Button) myInflatedView.findViewById(R.id.button_forget);
        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectAllButton.performClick();
                Iterator<String> it = addresses.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    addresses.put(key, DEFAULT_ADDRESS);
                    redrawLocalIndicators();
                    setButtonTexts();
                }
            }
        });

        return myInflatedView;
    }




    public void setButtonTexts() {
        for (String sensor : C.SENSORS) {
            if (isConnected.get(sensor)) {
                //need to set the 2 buttons to disconnect,, buttons id'd by key
                newButtons.get(sensor).setText("Disconnect");
                oldButtons.get(sensor).setVisibility(View.INVISIBLE);
            } else {
                //need to set one button to connect new and the other to connect ****,
                newButtons.get(sensor).setText("Connect New");
                oldButtons.get(sensor).setVisibility(View.VISIBLE);
                oldButtons.get(sensor).setText("Connect " + workOutSensorCode(sensor));
            }
        }
    }

    private String workOutSensorCode(String sensor) {
        String addr = addresses.get(sensor);
        String code = "";

        if (addr.equals(DEFAULT_ADDRESS)) {
            oldButtons.get(sensor).setVisibility(View.INVISIBLE);
        } else {
            oldButtons.get(sensor).setVisibility(View.VISIBLE);
            code = addr.split(":")[4] + addr.split(":")[5];
        }
        return code;
    }

    public void redrawLocalIndicators() {

        for (String sensor : C.SENSORS) {
            Log.d("redraw", "sensor " + sensor + " is " + isConnected.get(sensor));
            if (!isConnected.get(sensor)) {
                indicators.get(sensor).setImageResource(R.drawable.ic_redcircle);
                addressTexts.get(sensor).setText("");
            } else {
                Log.d("redraw", "hi");
                indicators.get(sensor).setImageResource(R.drawable.ic_greencircle);
                addressTexts.get(sensor).setText(workOutSensorCode(sensor));
            }
        }
    }

    private void initializeIndicators() {
        indicators = new HashMap<>();
        indicators.put(C.LEFT_THIGH, (ImageView) myInflatedView.findViewById(R.id.image_lt));
        indicators.put(C.LEFT_CALF, (ImageView) myInflatedView.findViewById(R.id.image_lc));
        indicators.put(C.LOWER_BACK, (ImageView) myInflatedView.findViewById(R.id.image_lb));
        indicators.put(C.RIGHT_THIGH, (ImageView) myInflatedView.findViewById(R.id.image_rt));
        indicators.put(C.RIGHT_CALF, (ImageView) myInflatedView.findViewById(R.id.image_rc));

    }

    private void initializeAddressTexts() {
        addressTexts = new HashMap<>();
        addressTexts.put(C.RIGHT_CALF, (TextView) myInflatedView.findViewById(R.id.text_rc_code));
        addressTexts.put(C.LEFT_THIGH, (TextView) myInflatedView.findViewById(R.id.text_lt_code));
        addressTexts.put(C.LEFT_CALF, (TextView) myInflatedView.findViewById(R.id.text_lc_code));
        addressTexts.put(C.LOWER_BACK, (TextView) myInflatedView.findViewById(R.id.text_lb_code));
        addressTexts.put(C.RIGHT_THIGH, (TextView) myInflatedView.findViewById(R.id.text_rt_code));
    }

    private void initializeButtons() {
        oldButtons = new HashMap<>();
        newButtons = new HashMap<>();

        newButtons.put(C.LEFT_THIGH, (Button) myInflatedView.findViewById(R.id.button_lt_n));
        oldButtons.put(C.LEFT_THIGH, (Button) myInflatedView.findViewById(R.id.button_lt_o));
        newButtons.put(C.LEFT_CALF, (Button) myInflatedView.findViewById(R.id.button_lc_n));
        oldButtons.put(C.LEFT_CALF, (Button) myInflatedView.findViewById(R.id.button_lc_o));
        newButtons.put(C.LOWER_BACK, (Button) myInflatedView.findViewById(R.id.button_lb_n));
        oldButtons.put(C.LOWER_BACK, (Button) myInflatedView.findViewById(R.id.button_lb_o));
        newButtons.put(C.RIGHT_THIGH, (Button) myInflatedView.findViewById(R.id.button_rt_n));
        oldButtons.put(C.RIGHT_THIGH, (Button) myInflatedView.findViewById(R.id.button_rt_o));
        newButtons.put(C.RIGHT_CALF, (Button) myInflatedView.findViewById(R.id.button_rc_n));
        oldButtons.put(C.RIGHT_CALF, (Button) myInflatedView.findViewById(R.id.button_rc_o));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            if (shimmers.get(currentlyConnecting).getStreamingStatus()) {
                shimmers.get(currentlyConnecting).stop();
            } else {
                String bluetoothAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                clearOtherAddresses(bluetoothAddress);
                setButtonTexts();
                addresses.put(currentlyConnecting, bluetoothAddress);
                shimmers.get(currentlyConnecting).connect(bluetoothAddress, "default"); //currentlyConnecting has been set as clicked button
            }
        } else {
            Toast.makeText(getActivity(), "No Shimmer Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void clearOtherAddresses(String bluetoothAddress) {
        //if another sensor has this address, it looses it :(
        for (String sensor : C.SENSORS) {
            if (addresses.get(sensor).equals(bluetoothAddress)) {
                addresses.put(sensor, DEFAULT_ADDRESS);
            }
        }
    }
}
