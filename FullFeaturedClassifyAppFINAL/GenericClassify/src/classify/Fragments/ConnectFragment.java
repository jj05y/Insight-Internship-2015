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
import android.widget.ListView;
import android.widget.Toast;

import com.cmw.R;

import java.util.ArrayList;

import classify.Activities.DeviceListActivity;
import classify.Adapters.ConnectSensorsAdapter;
import classify.Constants.C;
import classify.Interfaces.Linker;
import classify.ListItems.ItemSensorForConnectFragment;


public class ConnectFragment extends Fragment {

    private Linker linker;

    private View myInflatedView;

    private ArrayList<ItemSensorForConnectFragment> sensors;
    private int currentlyConnecting;
    private ListView sensorList;
    private ConnectSensorsAdapter adapter;

    public ConnectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_connect, container, false);
        linker = (Linker) getActivity();

        sensors = linker.getSensorsForConnect();
        Log.d("saveString", "#sensors from connect fragment" + sensors.size());

        sensorList = (ListView) myInflatedView.findViewById(R.id.listView_connect_sensors);
        adapter = new ConnectSensorsAdapter(sensors, getActivity(), this);
        sensorList.setAdapter(adapter);

        //todo connect all, disconnect all, and forget all, ouch,

        Button connectAll = (Button) myInflatedView.findViewById(R.id.button_connect_all);
        Button disconnectAll = (Button) myInflatedView.findViewById(R.id.button_disconnect_all);
        Button forgetAll = (Button) myInflatedView.findViewById(R.id.button_forget_all);

        connectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String sensor : C.SENSORS) {
                    String address = linker.getAddressesMap().get(sensor);
                    if (!address.equals(C.DEFAULT_ADDRESS)) {
                        linker.getShimmersMap().get(sensor).connect(address, "default");
                    }
                }
            }
        });

        disconnectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String sensor : C.SENSORS) {
                    linker.getShimmersMap().get(sensor).stop();
                }
                adapter.updateSensors(linker.getSensorsForConnect());
            }

        });

        forgetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linker.getAddressesMap().clear();
                for (String sensor : C.SENSORS) {
                    linker.getAddressesMap().put(sensor, C.DEFAULT_ADDRESS);
                }
                ArrayList<ItemSensorForConnectFragment> things = linker.getSensorsForConnect();
                for (ItemSensorForConnectFragment thing : things) {
                    thing.setSavedAddress(C.DEFAULT_ADDRESS);
                }
                adapter.updateSensors(things);
            }
        });



        return myInflatedView;
    }









    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ItemSensorForConnectFragment sensor = sensors.get(currentlyConnecting);

        if (resultCode == Activity.RESULT_OK) {

            if (sensor.getSensor().getStreamingStatus()) {
                sensor.getSensor().stop();
            } else {
                String bluetoothAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                clearOtherAddresses(bluetoothAddress);
                sensor.setSavedAddress(bluetoothAddress);
                sensor.getSensor().connect(bluetoothAddress, "default"); //currentlyConnecting has been set as clicked button
                adapter.updateSensors(linker.getSensorsForConnect());
            }
        } else {
            Toast.makeText(getActivity(), "No Shimmer Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void clearOtherAddresses(String bluetoothAddress) {
        //if another sensor has this address, it looses it :(
        for (ItemSensorForConnectFragment sensor : sensors) {
            if (sensor.getSavedAddress().equals(bluetoothAddress)) {
                sensor.setSavedAddress(C.DEFAULT_ADDRESS);
            }
        }
    }

    public void setCurrentlyConnecting(int pos) {
        currentlyConnecting = pos;
    }

    public void resetAdapter(ArrayList<ItemSensorForConnectFragment> sens) {
        adapter.updateSensors(sens);
    }
}
