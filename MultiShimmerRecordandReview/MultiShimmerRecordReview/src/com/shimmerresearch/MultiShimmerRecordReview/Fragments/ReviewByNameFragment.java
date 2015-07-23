package com.shimmerresearch.MultiShimmerRecordReview.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shimmerresearch.MultiShimmerRecordReview.Adapters.ViewPagerAdapterReviewByName;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.MultiShimmerRecordReview.Interfaces.Linker;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.ItemForReview;
import com.shimmerresearch.MultiShimmerRecordReview.MiscUtil.ZoomOutPageTransformer;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;
import java.util.HashMap;


public class ReviewByNameFragment extends Fragment {

    private Linker linker;
    private DatabaseHandler db;
    private ArrayList<ItemForReview> thingsForReview;
    private ViewPager pager;
    private ViewPagerAdapterReviewByName pagerAdapter;


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

        if (db.getAllNames().isEmpty()) {
            thingsForReview = new ArrayList<>();
            thingsForReview.add(new ItemForReview("", 0, "", 0, 0, 0, new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>(), new HashMap<String, ArrayList<Double>>()));
        } else {
            thingsForReview = (db.getAllWithName(db.getAllNames().get(0)));
        }


        pagerAdapter = new ViewPagerAdapterReviewByName(thingsForReview, getActivity(), db, this, 0);
        pager.setAdapter(pagerAdapter);



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

    public void reloadPages(String selectedName, int selectedPos) { //this is called from whithin the view pager adapter
        thingsForReview = (db.getAllWithName(selectedName));

        pagerAdapter = new ViewPagerAdapterReviewByName(thingsForReview, getActivity(), db, this, selectedPos);
        pager.setAdapter(pagerAdapter);

    }

}
