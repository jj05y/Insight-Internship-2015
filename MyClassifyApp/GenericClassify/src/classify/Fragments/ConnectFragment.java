package classify.Fragments;

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


import com.cmw.R;
import com.shimmerresearch.android.Shimmer;

import java.util.HashMap;

import classify.Activities.DeviceListActivity;
import classify.Constants.C;
import classify.Interfaces.Linker;
import classify.ObjectClasses.DataPacket;

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

    private HashMap<String, ImageView> indicators;
    private HashMap<String, TextView> addressTexts;
    private Button testStreamButton;

    private HashMap<String, Button> newButtons;
    private HashMap<String, Button> oldButtons;

    private TextView testText;

    private View myInflatedView;

    public ConnectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_connect, container, false);
        linker = (Linker) getActivity();
        addresses = linker.getAddressesMap();
        shimmers = linker.getShimmersMap();

        initializeIndicators();
        initializeAddressTexts();
        initializeButtons();
        Log.d("redraw", "redrawing");
        redrawLocalIndicators();
        setButtonTexts();


        testText = (TextView) myInflatedView.findViewById(R.id.text_test_stream);
        sortTestStream(new DataPacket());

        testStreamButton = (Button) myInflatedView.findViewById(R.id.button_test_stream);
        testStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shimmers.get(C.MAIN_SENSOR).getShimmerState() == Shimmer.STATE_CONNECTED) {
                    if (!linker.getisTestStreaming()) {
                        testStreamButton.setText("Stop Streaming");
                        shimmers.get(C.MAIN_SENSOR).startStreaming();
                        linker.toggleTestStreaming();
                    } else {
                        testStreamButton.setText("Test Streaming");
                        linker.toggleTestStreaming();
                        shimmers.get(C.MAIN_SENSOR).startStreaming();
                    }

                } else {
                    Toast.makeText(getActivity(), "Connect sensor first", Toast.LENGTH_SHORT).show();
                }
            }
        });




        View.OnClickListener newListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clickedSensor = "";

                switch (view.getId()) {
                    case R.id.button_main_sensor_connect_new:
                        clickedSensor = C.MAIN_SENSOR;
                        break;
                    //add more sensors here
                }

                if (shimmers.get(clickedSensor).getShimmerState() != Shimmer.STATE_CONNECTED) {
                    currentlyConnecting = clickedSensor; //this holds clicked sensor while device list activity is launched
                    Log.d(null, "clicked button is " + clickedSensor);
                    Intent i;
                    i = new Intent(getActivity(), DeviceListActivity.class);

                    startActivityForResult(i, REQUEST_CONNECT_SHIMMER);
                } else {
                    shimmers.get(clickedSensor).stop();
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
                    case R.id.button_main_sensor_connect_old:
                        clickedSensor = C.MAIN_SENSOR;
                        break;
                    //add more sensors here

                }
                if (shimmers.get(clickedSensor).getShimmerState() != Shimmer.STATE_CONNECTED) {
                    Toast.makeText(getActivity(), "Connecting: " + addresses.get(clickedSensor), Toast.LENGTH_SHORT).show();
                    shimmers.get(clickedSensor).connect(addresses.get(clickedSensor), "default");
                    setButtonTexts();
                } else {
                    shimmers.get(clickedSensor).stop();
                    setButtonTexts();
                    redrawLocalIndicators();
                }
            }
        };



        for (String sensor : C.SENSORS) {
            newButtons.get(sensor).setOnClickListener(newListener);
            oldButtons.get(sensor).setOnClickListener(oldListener);
        }

        return myInflatedView;
    }

    public void sortTestStream(DataPacket data) {
        testText.setText(data.toString());

    }


    public void setButtonTexts() {
        for (String sensor : C.SENSORS) {
            if (shimmers.get(sensor).getShimmerState() == Shimmer.STATE_CONNECTED) {
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
            Log.d("redraw", "sensor " + sensor + " is " + (shimmers.get(sensor).getShimmerState() == Shimmer.STATE_CONNECTED));
            if (shimmers.get(sensor).getShimmerState() != Shimmer.STATE_CONNECTED) {
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
        indicators.put(C.MAIN_SENSOR, (ImageView) myInflatedView.findViewById(R.id.image_main_sensor));
    }

    private void initializeAddressTexts() {
        addressTexts = new HashMap<>();
        addressTexts.put(C.MAIN_SENSOR, (TextView) myInflatedView.findViewById(R.id.text_main_sensor_addr));

    }

    private void initializeButtons() {
        oldButtons = new HashMap<>();
        newButtons = new HashMap<>();

        newButtons.put(C.MAIN_SENSOR, (Button) myInflatedView.findViewById(R.id.button_main_sensor_connect_new));
        oldButtons.put(C.MAIN_SENSOR, (Button) myInflatedView.findViewById(R.id.button_main_sensor_connect_old));

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
