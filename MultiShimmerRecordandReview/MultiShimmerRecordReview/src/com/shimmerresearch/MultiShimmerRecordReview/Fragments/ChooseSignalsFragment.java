package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.HashMap;


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

        //Sensors
        CheckBox lt = (CheckBox) myInflatedView.findViewById(R.id.cb_lt);
        CheckBox lc = (CheckBox) myInflatedView.findViewById(R.id.cb_lc);
        CheckBox rt = (CheckBox) myInflatedView.findViewById(R.id.cb_rt);
        CheckBox rc = (CheckBox) myInflatedView.findViewById(R.id.cb_rc);
        CheckBox lb = (CheckBox) myInflatedView.findViewById(R.id.cb_lb);

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

                        case R.id.cb_lt:
                        sensorsMap.put(C.LEFT_THIGH, b);
                        break;
                         case R.id.cb_lc:
                        sensorsMap.put(C.LEFT_CALF, b);
                        break;
                         case R.id.cb_rt:
                        sensorsMap.put(C.RIGHT_THIGH, b);
                        break;
                         case R.id.cb_rc:
                        sensorsMap.put(C.RIGHT_CALF, b);
                        break;
                         case R.id.cb_lb:
                        sensorsMap.put(C.LOWER_BACK, b);
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
        lt.setOnCheckedChangeListener(listener);
        lc.setOnCheckedChangeListener(listener);
        rt.setOnCheckedChangeListener(listener);
        rc.setOnCheckedChangeListener(listener);
        lb.setOnCheckedChangeListener(listener);



        return myInflatedView;
    }
}
