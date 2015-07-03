package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.Util.DataBaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.Util.HorizontalListItemForReview;
import com.shimmerresearch.MultiShimmerRecordReview.Util.HorizontalListViewAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Util.ReviewListViewAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Util.TwoWayView;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;
import java.util.Random;


public class ReviewFragment extends Fragment {

    private Linker linker;
    private DataBaseHandler db;
    private HorizontalListViewAdapter horizontalListViewAdapter;
    private ArrayList<HorizontalListItemForReview> thingsForReview;

    public ReviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_review, container, false);
        linker = (Linker) getActivity();
        db = linker.getDb();


        ////////////////////////////
        // vertical list
        ////////////////////////////
        ListView listView = (ListView) myInflatedView.findViewById(R.id.list_view);

        ArrayList<String> listOfNames = db.getAllNames();
        thingsForReview = new ArrayList<>();



        ReviewListViewAdapter listViewAdapter = new ReviewListViewAdapter(getActivity(), android.R.layout.simple_list_item_1, listOfNames);
        listView.setAdapter(listViewAdapter);
        //listViewAdapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                //list.remove(parent.getItemAtPosition(position));
                TextView temp = (TextView) view.findViewById(android.R.id.text1);
                String name = temp.getText().toString();
                thingsForReview = db.getAllWithName(name);
                horizontalListViewAdapter.clear();
                horizontalListViewAdapter.addAll(thingsForReview);
                horizontalListViewAdapter.notifyDataSetChanged();
            }
        });
        ///////////////////////////// vertical

        //////////////////////////////
        // horizontal list
        ///////////////////////////////


/*
        HorizontalListItemForReview temp = new HorizontalListItemForReview();
        HorizontalListItemForReview temp2 = new HorizontalListItemForReview();
        temp.setName("Temp1");
        temp2.setName("Temp2");
        temp.setLabel("Label 1");
        temp2.setLabel("Label 2");
        temp.setFileName("/storage/emulated/0/SessionRecordings/VID_20150625_154122.mp4");
        temp2.setFileName("/storage/emulated/0/SessionRecordings/VID_20150626_142822.mp4");
        Random rand = new Random();
        ArrayList<Double> tempList1 = new ArrayList<>();
        ArrayList<Double> tempList2 = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            tempList1.add(rand.nextDouble()%18);
            tempList2.add(rand.nextDouble()%9);
        }
        temp.setPoints(tempList1);
        temp2.setPoints(tempList2);

        things.add(temp);
        things.add(temp2);
        things.add(temp);
        things.add(temp2);
        things.add(temp);
        things.add(temp2);*/

        horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), thingsForReview);
        TwoWayView horizontalList = (TwoWayView) myInflatedView.findViewById(R.id.lvItems);
        horizontalList.setAdapter(horizontalListViewAdapter);


        ///////////////////////////// horizontal


        return myInflatedView;
    }



    @Override
    public void onPause() {
        super.onPause();
        horizontalListViewAdapter.releaseMP();
    }


}
