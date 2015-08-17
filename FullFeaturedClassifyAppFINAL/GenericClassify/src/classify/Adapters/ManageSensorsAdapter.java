package classify.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmw.R;
import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;

import classify.Constants.C;
import classify.Interfaces.Linker;
import classify.ListItems.ItemSensorForConnectFragment;

/**
 * Created by joe on 10/08/15.
 */
public class ManageSensorsAdapter extends BaseAdapter {

    private ArrayList<ItemSensorForConnectFragment> things;
    private Context c;
    Linker linker;

    public ManageSensorsAdapter(ArrayList<ItemSensorForConnectFragment> things, Context c) {
        this.things = things;
        this.c = c;
        this.linker = (Linker) c;
    }

    @Override
    public int getCount() {
        return things.size();
    }

    @Override
    public Object getItem(int i) {
        return things.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View myInflatedView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myInflatedView = inflater.inflate(R.layout.row_item_change_sensor_name, null);
        } else {
            myInflatedView = convertView;
        }

        TextView name = (TextView) myInflatedView.findViewById(R.id.text_change_sensors_name);
        EditText editName = (EditText) myInflatedView.findViewById(R.id.editText_change_sensors_name);
        Button save = (Button) myInflatedView.findViewById(R.id.button_changeange_sens0r_name);
        Button remove = (Button) myInflatedView.findViewById(R.id.button_remov_sensor);

        name.setText(things.get(i).getName());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i == 0) {
                    Toast.makeText(c, "Can't Delete Main Sensor", Toast.LENGTH_SHORT).show();
                } else {
                    String name =  things.get(i).getName();
                    C.SENSORS.remove(name);
                    linker.getPlotSensorsMap().put(name, false);
                    linker.getShimmersMap().remove(name);
                    linker.getAddressesMap().remove(name);
                    linker.getPlotSensorsMap().remove(name);
                    things.remove(i);
                    notifyDataSetChanged();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editName.getText().toString();
                if (!newName.equals("") && !C.SENSORS.contains(newName)) {
                    String oldName = things.get(i).getName(); //need this in a min,
                    things.get(i).setName(newName);
                    notifyDataSetChanged();
                    editName.setText("");
                    linker.closeKeyboard();

                    int sensorIndex = C.SENSORS.indexOf(oldName);
                    C.SENSORS.remove(sensorIndex);
                    C.SENSORS.add(sensorIndex, newName);
                    Log.d("saveString", "C.Sensors: " + C.SENSORS);

                    String oldAddr = linker.getAddressesMap().remove(oldName);
                    linker.getAddressesMap().put(newName, oldAddr);

                    boolean oldBool = linker.getPlotSensorsMap().remove(oldName);
                    linker.getPlotSensorsMap().put(newName, oldBool);

                    linker.reBuildShimmerMap();


                } else {
                    Toast.makeText(c, "Enter a unique new name", Toast.LENGTH_SHORT);
                }
            }
        });


        return myInflatedView;
    }




}
