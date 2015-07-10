package com.shimmerresearch.MultiShimmerRecordReview.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.multishimmerrecordreview.R;


public class SaveDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.save_dialog);

        setResult(Activity.RESULT_CANCELED);


        EditText nameEdit = (EditText) findViewById(R.id.edit_name);
        Button saveButton = (Button) findViewById(R.id.button_add_to_data_base);

        Spinner exerciseSpinner = (Spinner) findViewById(R.id.exercise_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, C.EXERCISES);
        exerciseSpinner.setAdapter(adapter);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEdit.getText().toString();
                int exercise = exerciseSpinner.getSelectedItemPosition();
                Log.d(null, "name " + name + " exersise " + exercise);

                if (name.equals("") ) {
                    Toast.makeText(getBaseContext(), "Enter Name", Toast.LENGTH_SHORT).show();
                } else {

                    Intent i = new Intent();
                    i.putExtra("name", name);
                    i.putExtra("exercise", exercise);
                    setResult(Activity.RESULT_OK, i);
                    finish();

                }

            }
        });



    }
}