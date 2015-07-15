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
import com.shimmerresearch.driver.Configuration;
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

    private Button ltNew;
    private Button ltOld;
    private Button lcNew;
    private Button lcOld;
    private Button lbNew;
    private Button lbOld;
    private Button rtNew;
    private Button rtOld;
    private Button rcNew;
    private Button rcOld;

    private TextView ltCode;
    private TextView lcCode;
    private TextView lbCode;
    private TextView rtCode;
    private TextView rcCode;


    private View myInflatedView;

    public ConnectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_connect, container, false);
        linker = (Linker) getActivity();
        addresses = linker.getAddressesMap();
        shimmers = linker.getShimmersMap();
        if (indicators == null) {
            initializeIndicators();
        } else {
            redrawLocalIndicators();
        }
        isConnected = linker.getIsConnectedMap();

        Configuration.setTooLegacyObjectClusterSensorNames();

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

        ltCode = (TextView) myInflatedView.findViewById(R.id.text_lt_code);
        lcCode = (TextView) myInflatedView.findViewById(R.id.text_lc_code);
        lbCode = (TextView) myInflatedView.findViewById(R.id.text_lb_code);
        rtCode = (TextView) myInflatedView.findViewById(R.id.text_rt_code);
        rcCode = (TextView) myInflatedView.findViewById(R.id.text_rc_code);


        ltNew = (Button) myInflatedView.findViewById(R.id.button_lt_n);
        ltOld = (Button) myInflatedView.findViewById(R.id.button_lt_o);
        lcNew = (Button) myInflatedView.findViewById(R.id.button_lc_n);
        lcOld = (Button) myInflatedView.findViewById(R.id.button_lc_o);
        lbNew = (Button) myInflatedView.findViewById(R.id.button_lb_n);
        lbOld = (Button) myInflatedView.findViewById(R.id.button_lb_o);
        rtNew = (Button) myInflatedView.findViewById(R.id.button_rt_n);
        rtOld = (Button) myInflatedView.findViewById(R.id.button_rt_o);
        rcNew = (Button) myInflatedView.findViewById(R.id.button_rc_n);
        rcOld = (Button) myInflatedView.findViewById(R.id.button_rc_o);

        ltNew.setOnClickListener(newListener);
        ltOld.setOnClickListener(oldListener);
        lcNew.setOnClickListener(newListener);
        lcOld.setOnClickListener(oldListener);
        lbNew.setOnClickListener(newListener);
        lbOld.setOnClickListener(oldListener);
        rtNew.setOnClickListener(newListener);
        rtOld.setOnClickListener(oldListener);
        rcNew.setOnClickListener(newListener);
        rcOld.setOnClickListener(oldListener);


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


        redrawLocalIndicators();
        setButtonTexts();

        return myInflatedView;
    }

    public void setButtonTexts() {
        Iterator it = isConnected.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Boolean> e = (Map.Entry<String, Boolean>) it.next();
            Button newButton = null;
            Button oldButton = null;

            switch (e.getKey()) {
                case C.LEFT_THIGH:
                    newButton = ltNew;
                    oldButton = ltOld;
                    break;
                case C.LEFT_CALF:
                    newButton = lcNew;
                    oldButton = lcOld;
                    break;
                case C.LOWER_BACK:
                    newButton = lbNew;
                    oldButton = lbOld;
                    break;
                case C.RIGHT_THIGH:
                    newButton = rtNew;
                    oldButton = rtOld;
                    break;
                case C.RIGHT_CALF:
                    newButton = rcNew;
                    oldButton = rcOld;
                    break;
            }

            if (e.getValue()) {
                //need to set the 2 buttons to disconnect,, buttons id'd by key
                newButton.setText("Disconnect");
                oldButton.setVisibility(View.INVISIBLE);
            } else {
                //need to set one button to connect new and the other to connect ****,
                newButton.setText("Connect New");
                oldButton.setVisibility(View.VISIBLE);
                oldButton.setText("Connect " + workOutSensorCode(e.getKey()));
            }

        }
    }

    private String workOutSensorCode(String key) {
        String addr = addresses.get(key);
        String code = "";
        Button b = null;

        switch (key) {
            case C.LEFT_THIGH:
                b = ltOld;
                break;
            case C.LEFT_CALF:
                b = lcOld;
                break;
            case C.LOWER_BACK:
                b = lbOld;
                break;
            case C.RIGHT_THIGH:
                b = rtOld;
                break;
            case C.RIGHT_CALF:
                b = rcOld;
                break;
        }
        if (addr.equals(DEFAULT_ADDRESS)) {
            b.setVisibility(View.INVISIBLE);
        } else {
            b.setVisibility(View.VISIBLE);
            code = addr.split(":")[4] + addr.split(":")[5];
        }
        return code;
    }

    public void redrawLocalIndicators() {
        if (!isConnected.get(C.LEFT_THIGH)) {
            ((ImageView) myInflatedView.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_redcircle);
            ltCode.setText("");
        } else {
            ((ImageView) myInflatedView.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_greencircle);
            ltCode.setText(workOutSensorCode(C.LEFT_THIGH));
        }

        if (!isConnected.get(C.LEFT_CALF)) {
            ((ImageView) myInflatedView.findViewById(R.id.imageView2)).setImageResource(R.drawable.ic_redcircle);
            lcCode.setText("");
        } else {
            ((ImageView) myInflatedView.findViewById(R.id.imageView2)).setImageResource(R.drawable.ic_greencircle);
            lcCode.setText(workOutSensorCode(C.LEFT_CALF));
        }

        if (!isConnected.get(C.RIGHT_THIGH)) {
            ((ImageView) myInflatedView.findViewById(R.id.imageView5)).setImageResource(R.drawable.ic_redcircle);
            rtCode.setText("");
        } else {
            ((ImageView) myInflatedView.findViewById(R.id.imageView5)).setImageResource(R.drawable.ic_greencircle);
            rtCode.setText(workOutSensorCode(C.RIGHT_THIGH));
        }

        if (!isConnected.get(C.RIGHT_CALF)) {
            ((ImageView) myInflatedView.findViewById(R.id.imageView6)).setImageResource(R.drawable.ic_redcircle);
            rcCode.setText("");
        } else {
            ((ImageView) myInflatedView.findViewById(R.id.imageView6)).setImageResource(R.drawable.ic_greencircle);
            rcCode.setText(workOutSensorCode(C.RIGHT_CALF));
        }

        if (!isConnected.get(C.LOWER_BACK)) {
            ((ImageView) myInflatedView.findViewById(R.id.imageView3)).setImageResource(R.drawable.ic_redcircle);
            lbCode.setText("");
        } else {
            ((ImageView) myInflatedView.findViewById(R.id.imageView3)).setImageResource(R.drawable.ic_greencircle);
            lbCode.setText(workOutSensorCode(C.LOWER_BACK));
        }

    }

    private void initializeIndicators() {
        indicators = new HashMap<>();
        indicators.put(C.LEFT_THIGH, (ImageView) myInflatedView.findViewById(R.id.imageView));
        indicators.put(C.LEFT_CALF, (ImageView) myInflatedView.findViewById(R.id.imageView2));
        indicators.put(C.RIGHT_THIGH, (ImageView) myInflatedView.findViewById(R.id.imageView3));
        indicators.put(C.RIGHT_CALF, (ImageView) myInflatedView.findViewById(R.id.imageView5));
        indicators.put(C.LOWER_BACK, (ImageView) myInflatedView.findViewById(R.id.imageView6));

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
        Iterator<Map.Entry<String, String>> it = (Iterator<Map.Entry<String, String>>) addresses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> e = it.next();
            if (e.getValue().equals(bluetoothAddress)) {
                addresses.put(e.getKey(), DEFAULT_ADDRESS);
            }
        }
    }




}
