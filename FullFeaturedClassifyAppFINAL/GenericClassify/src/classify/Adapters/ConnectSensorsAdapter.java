package classify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmw.R;

import java.util.ArrayList;

import classify.Activities.DeviceListActivity;
import classify.Constants.C;
import classify.Fragments.ConnectFragment;
import classify.Interfaces.Linker;
import classify.ListItems.ItemSensorForConnectFragment;


public class ConnectSensorsAdapter extends BaseAdapter {


    public static final int REQUEST_CODE_SHIMMER = 2;
    ArrayList<ItemSensorForConnectFragment> things;
    Context context;
    ConnectFragment parentFrag;
    Linker linker;

    public ConnectSensorsAdapter(ArrayList<ItemSensorForConnectFragment> things, Context c, ConnectFragment parentFrag) {
        this.things = things;
        this.context = c;
        this.parentFrag = parentFrag;
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
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View myInflatedView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myInflatedView = inflater.inflate(R.layout.row_item_connect_sensor, null);
        } else {
            myInflatedView = convertView;
        }

        TextView name = (TextView) myInflatedView.findViewById(R.id.text_connect_row_name);
        TextView sensorID = (TextView) myInflatedView.findViewById(R.id.text_connect_row_sensor_id);
        ImageView indicator = (ImageView) myInflatedView.findViewById(R.id.imageView_connect_row_image);
        Button connectNew = (Button) myInflatedView.findViewById(R.id.button_connect_row_connect_new);
        Button connectOld = (Button) myInflatedView.findViewById(R.id.button_connect_row_connect_old);

        name.setText(things.get(pos).getName());
        sensorID.setText("       ");

        String savedAddress = things.get(pos).getSavedAddress();
        Log.d("saveAdd", "Saved Adress: "+ savedAddress);
        String code = savedAddress.split(":")[4] + savedAddress.split(":")[5];

        Log.d("fromConnectAdapter", "redrawing");
        if (!things.get(pos).isConnected()) {
            indicator.setImageResource(R.drawable.ic_redcircle);
            sensorID.setText("");
            connectNew.setText("Connect New");
            if (savedAddress.equals(C.DEFAULT_ADDRESS)) {
                connectOld.setText("Connect ****");
                connectOld.setEnabled(false);
            } else {
                connectOld.setText("Connect " + code);
                connectOld.setEnabled(true);
            }
        } else {
            indicator.setImageResource(R.drawable.ic_greencircle);
            connectOld.setEnabled(false);
            sensorID.setText(" - " + code);
            connectNew.setText("Disconnect");
        }

        connectNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!things.get(pos).isConnected()) {
                    Intent i = new Intent(context, DeviceListActivity.class);
                    parentFrag.setCurrentlyConnecting(pos);
                    parentFrag.startActivityForResult(i, REQUEST_CODE_SHIMMER);
                } else {
                    things.get(pos).getSensor().stop();
                    updateSensors(linker.getSensorsForConnect());
                }
            }
        });

        connectOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!things.get(pos).isConnected()) {
                    things.get(pos).getSensor().connect(savedAddress, "default");
                } else {
                    things.get(pos).getSensor().stop();
                    updateSensors(linker.getSensorsForConnect());
                }
            }
        });



        return myInflatedView;
    }

    public void updateSensors(ArrayList<ItemSensorForConnectFragment> things) {
        this.things = new ArrayList<>();
        this.things.addAll(things);
        notifyDataSetChanged();
    }


}
