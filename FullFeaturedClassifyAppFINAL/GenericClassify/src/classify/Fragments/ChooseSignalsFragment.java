package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cmw.R;

import java.util.ArrayList;
import java.util.HashMap;

import classify.Constants.C;
import classify.Interfaces.Linker;


public class ChooseSignalsFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;
    private ArrayList<CheckBox> boxes;
    private HashMap<String, Boolean> sensorsMap;

    public ChooseSignalsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_choose_signals, container, false);
        linker = (Linker) getActivity();

        //Signals
        CheckBox accelMag = (CheckBox) myInflatedView.findViewById(R.id.cb_accel_mag);
        CheckBox accelX = (CheckBox) myInflatedView.findViewById(R.id.cb_accel_x);
        CheckBox accelY = (CheckBox) myInflatedView.findViewById(R.id.cb_accel_y);
        CheckBox accelZ = (CheckBox) myInflatedView.findViewById(R.id.cb_accel_z);
        CheckBox pitch = (CheckBox) myInflatedView.findViewById(R.id.cb_pitch);
        CheckBox roll = (CheckBox) myInflatedView.findViewById(R.id.cb_roll);
        CheckBox yaw = (CheckBox) myInflatedView.findViewById(R.id.cb_yaw);
        CheckBox gyroMag = (CheckBox) myInflatedView.findViewById(R.id.cb_gyro_mag);
        CheckBox gyroX = (CheckBox) myInflatedView.findViewById(R.id.cb_gyro_x);
        CheckBox gyroY = (CheckBox) myInflatedView.findViewById(R.id.cb_gyro_y);
        CheckBox gyroZ = (CheckBox) myInflatedView.findViewById(R.id.cb_gyro_z);
        CheckBox quatW = (CheckBox) myInflatedView.findViewById(R.id.cb_quat_w);
        CheckBox quatX = (CheckBox) myInflatedView.findViewById(R.id.cb_quat_x);
        CheckBox quatY = (CheckBox) myInflatedView.findViewById(R.id.cb_quat_y);
        CheckBox quatZ = (CheckBox) myInflatedView.findViewById(R.id.cb_quat_z);

        //Sensors
        CheckBox mainSensor = (CheckBox) myInflatedView.findViewById(R.id.cb_main_sensor);
        CheckBox sensor2 = (CheckBox) myInflatedView.findViewById(R.id.cb_sensor2);
        CheckBox sensor3 = (CheckBox) myInflatedView.findViewById(R.id.cb_sensor3);
        CheckBox sensor4 = (CheckBox) myInflatedView.findViewById(R.id.cb_sensor4);
        CheckBox sensor5 = (CheckBox) myInflatedView.findViewById(R.id.cb_sensor5);

        HashMap<String, Boolean> signalsMap = linker.getPlotSignalsMap();
        sensorsMap = linker.getPlotSensorsMap();

        //todo change this to work proper,,, load clickedness from the aboved maps

        boxes = new ArrayList<>();
        boxes.add(mainSensor);
        boxes.add(sensor2);
        boxes.add(sensor3);
        boxes.add(sensor4);
        boxes.add(sensor5);
/*
        for (CheckBox box : boxes) {
            String sensor = box.getText().toString();
            boolean properValue = sensorsMap.get(sensor);
            box.setChecked(properValue);
        }*/

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                switch (compoundButton.getId()) {
                    case R.id.cb_accel_mag:
                        signalsMap.put(C.ACCEL_MAG, b);
                        Log.d("Click", "Accel mag is " + signalsMap.get(C.ACCEL_MAG));
                        break;
                    case R.id.cb_accel_x:
                        signalsMap.put(C.ACCEL_X, b);
                        break;
                    case R.id.cb_accel_y:
                        signalsMap.put(C.ACCEL_Y, b);
                        break;
                    case R.id.cb_accel_z:
                        signalsMap.put(C.ACCEL_Z, b);
                        break;
                    case R.id.cb_pitch:
                        signalsMap.put(C.PITCH, b);
                        break;
                    case R.id.cb_roll:
                        signalsMap.put(C.ROLL, b);
                        break;
                    case R.id.cb_yaw:
                        signalsMap.put(C.YAW, b);
                        break;
                    case R.id.cb_gyro_mag:
                        signalsMap.put(C.GYRO_MAG, b);
                        break;
                    case R.id.cb_gyro_x:
                        signalsMap.put(C.GYRO_X, b);
                        break;
                    case R.id.cb_gyro_y:
                        signalsMap.put(C.GYRO_Y, b);
                        break;
                    case R.id.cb_gyro_z:
                        signalsMap.put(C.GYRO_Z, b);
                        break;
                    case R.id.cb_quat_w:
                        signalsMap.put(C.QUAT_W, b);
                        break;
                    case R.id.cb_quat_x:
                        signalsMap.put(C.QUAT_X, b);
                        break;
                    case R.id.cb_quat_y:
                        signalsMap.put(C.QUAT_Y, b);
                        break;
                    case R.id.cb_quat_z:
                        signalsMap.put(C.QUAT_Z, b);
                        break;

                    case R.id.cb_main_sensor:
                        if (C.SENSORS.size() > 0)
                            sensorsMap.put(C.SENSORS.get(0), b);
                        break;
                    case R.id.cb_sensor2:
                        if (C.SENSORS.size() > 1)
                            sensorsMap.put(C.SENSORS.get(1), b);
                        break;
                    case R.id.cb_sensor3:
                        if (C.SENSORS.size() > 2)
                            sensorsMap.put(C.SENSORS.get(2), b);
                        break;
                    case R.id.cb_sensor4:
                        if (C.SENSORS.size() > 3)
                            sensorsMap.put(C.SENSORS.get(3), b);
                        break;
                    case R.id.cb_sensor5:
                        if (C.SENSORS.size() > 4)
                            sensorsMap.put(C.SENSORS.get(4), b);
                        break;


                }
            }
        };

        accelMag.setOnCheckedChangeListener(listener);
        accelX.setOnCheckedChangeListener(listener);
        accelY.setOnCheckedChangeListener(listener);
        accelZ.setOnCheckedChangeListener(listener);
        pitch.setOnCheckedChangeListener(listener);
        roll.setOnCheckedChangeListener(listener);
        yaw.setOnCheckedChangeListener(listener);
        gyroMag.setOnCheckedChangeListener(listener);
        gyroX.setOnCheckedChangeListener(listener);
        gyroY.setOnCheckedChangeListener(listener);
        gyroZ.setOnCheckedChangeListener(listener);
        quatW.setOnCheckedChangeListener(listener);
        quatX.setOnCheckedChangeListener(listener);
        quatY.setOnCheckedChangeListener(listener);
        quatZ.setOnCheckedChangeListener(listener);
        mainSensor.setOnCheckedChangeListener(listener);
        sensor2.setOnCheckedChangeListener(listener);
        sensor3.setOnCheckedChangeListener(listener);
        sensor4.setOnCheckedChangeListener(listener);
        sensor5.setOnCheckedChangeListener(listener);

        Log.d("redraw", "redrawing on create");

        sortOutClickedness();

        return myInflatedView;
    }

    private void sortOutClickedness() {
        int numSensors = C.SENSORS.size();
        for (int i = 0; i < boxes.size(); i++) {
            if (i < numSensors) {
                boxes.get(i).setVisibility(View.VISIBLE);
                boxes.get(i).setClickable(true);
                boxes.get(i).setText(C.SENSORS.get(i));
                boxes.get(i).setChecked(sensorsMap.get(C.SENSORS.get(i)));
                Log.d("redraw", "setting " + boxes.get(i).getText().toString() + " to " + sensorsMap.get(C.SENSORS.get(i)));

            } else {
                boxes.get(i).setVisibility(View.INVISIBLE);
                boxes.get(i).setChecked(false);
                boxes.get(i).setClickable(false);
                Log.d("redraw", "non sensor setting " + boxes.get(i).getText().toString() + " to " + false);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("redraw", "redrawing on resume");
        sortOutClickedness();

    }
}
