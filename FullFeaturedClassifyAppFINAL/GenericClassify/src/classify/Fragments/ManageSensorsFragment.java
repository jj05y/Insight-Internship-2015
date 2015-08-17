package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cmw.R;
import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;

import classify.Adapters.ManageSensorsAdapter;
import classify.Constants.C;
import classify.Interfaces.Linker;
import classify.ListItems.ItemSensorForConnectFragment;


public class ManageSensorsFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;
    private ArrayList<ItemSensorForConnectFragment> things;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_manage_sensors, container, false);
        linker = (Linker) getActivity();

        things = linker.getSensorsForConnect();
        ListView sensorsList = (ListView) myInflatedView.findViewById(R.id.listView_manage_sensors);
        ManageSensorsAdapter adapter = new ManageSensorsAdapter(things, getActivity());
        sensorsList.setAdapter(adapter);

        Button addNew = (Button) myInflatedView.findViewById(R.id.button_add_new_sensor);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new sensor, to C.Sensors,,
                //add new Item to linker.getSensorsForConnect
                //need to add a new sensor too to shimmers map!
                //and add to addresses map, all names are now UNIQUE!
                if (C.SENSORS.size() < 5) {
                    String newName = "Sensor" + (things.size() + 1);
                    C.SENSORS.add(newName);
                    linker.getAddressesMap().put(newName, C.DEFAULT_ADDRESS);
                    linker.reBuildShimmerMap();
                    linker.getPlotSensorsMap().put(newName, false);
                    things.add(new ItemSensorForConnectFragment(newName, C.DEFAULT_ADDRESS, linker.getShimmersMap().get(newName)));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Max of 5 Sensors", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return myInflatedView;
    }
}