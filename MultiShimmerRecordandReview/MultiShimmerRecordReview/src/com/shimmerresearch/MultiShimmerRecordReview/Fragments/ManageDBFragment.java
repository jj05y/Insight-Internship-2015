package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.shimmerresearch.MultiShimmerRecordReview.Adapters.ManageDBAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.Row;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.io.File;
import java.util.ArrayList;


public class ManageDBFragment extends Fragment{

    private View myInfatedView;
    private Linker linker;
    private DatabaseHandler db;
    ManageDBAdapter adapter;
    ArrayList<Row> rows;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInfatedView = inflater.inflate(R.layout.fragment_manage_db, container, false);

        ListView rowsList = (ListView) myInfatedView.findViewById(R.id.listView_manage_db);
        linker = (Linker) getActivity();
        db = linker.getDb();
        rows = db.getRowStrings();

        adapter = new ManageDBAdapter(getActivity(), rows, this);
        rowsList.setAdapter(adapter);

        return myInfatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            int pos = data.getIntExtra("pos", 0);
            Log.d("pos", "from activity result, wanna delete pos " + pos);
            File file = new File(rows.get(pos).getFileName());
            file.delete();

            db.deleteDetail(rows.get(pos).getRowID());
            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            //updata the list
            rows = db.getRowStrings();
            adapter.updateRows(rows);

        }
    }
}
