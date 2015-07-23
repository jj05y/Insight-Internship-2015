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

import java.util.HashMap;

import classify.Constants.C;
import classify.Interfaces.Linker;


public class ChooseSignalsFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;

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

        HashMap<String, Boolean> signalsMap = linker.getPlotSignalsMap();
        HashMap<String, Boolean> sensorsMap = linker.getPlotSensorsMap();

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
                        sensorsMap.put(C.MAIN_SENSOR, b);
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



        return myInflatedView;
    }
}
