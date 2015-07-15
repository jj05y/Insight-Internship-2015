package com.shimmerresearch.MultiShimmerRecordReview.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shimmerresearch.multishimmerrecordreview.R;

/**
 * Created by joe on 15/07/15.
 */
public class ConfirmDeleteDialog extends Activity {

    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_delete);

        setResult(Activity.RESULT_CANCELED);

        Button okButton = (Button) findViewById(R.id.button_confirm_delete);
        Button cancelButton = (Button) findViewById(R.id.button_cancel_delete);
        pos = getIntent().getIntExtra("pos", 0);
        Log.d("pos", "from dialog, wanna delete pos " + pos);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("pos", pos);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
}