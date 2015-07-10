package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shimmerresearch.MultiShimmerRecordReview.Adapters.ReviewListViewAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Adapters.ViewPagerAdapter;
import com.shimmerresearch.MultiShimmerRecordReview.Constants.C;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.ItemForReview;
import com.shimmerresearch.MultiShimmerRecordReview.MiscUtil.ZoomOutPageTransformer;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;


public class ReviewByNameFragment extends Fragment {

    private ArrayList<String> listOfNames;
    private Linker linker;
    private DatabaseHandler db;
    private ArrayList<ItemForReview> thingsForReview;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;


    public ReviewByNameFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_review_view_name_pager, container, false);
        linker = (Linker) getActivity();
        db = linker.getDb();
        pager = (ViewPager) myInflatedView.findViewById(R.id.pager);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());

        Log.d("page", "opened pager fragment");

        ListView listView = (ListView) myInflatedView.findViewById(R.id.list_view);


        listOfNames = db.getAllNames();


        ReviewListViewAdapter listViewAdapter = new ReviewListViewAdapter(getActivity(), android.R.layout.simple_list_item_1, listOfNames);
        listView.setAdapter(listViewAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                //list.remove(parent.getItemAtPosition(position));
                TextView temp = (TextView) view.findViewById(android.R.id.text1);
                String selectedItem = temp.getText().toString();

                thingsForReview = db.getAllWithName(selectedItem);

                pagerAdapter = new ViewPagerAdapter(thingsForReview, getActivity(), db, C.REVIEW_BY_NAME);
                pager.setAdapter(pagerAdapter);


            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.d("surfaceTesting", "looking at page " + i);
                pagerAdapter.stopMediaPlayer();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        return myInflatedView;
    }




    @Override
    public void onPause() {
        super.onPause();
    }


}
