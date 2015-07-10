package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shimmerresearch.MultiShimmerRecordReview.Adapters.ManageDBAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.Row;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;


public class ManageDBFragment extends Fragment{

    private View myInfatedView;
    private Linker linker;
    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInfatedView = inflater.inflate(R.layout.fragment_manage_db, container, false);

        ListView rowsList = (ListView) myInfatedView.findViewById(R.id.listView_manage_db);
        linker = (Linker) getActivity();
        db = linker.getDb();
        ArrayList<Row> rows = db.getRowStrings();

        ManageDBAdapter adapter = new ManageDBAdapter(getActivity(), rows, db);
        rowsList.setAdapter(adapter);

        return myInfatedView;
    }
}
