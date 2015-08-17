package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmw.R;

import java.util.ArrayList;
import java.util.HashMap;

import classify.Adapters.ViewPagerAdapterReviewByName;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForReview;
import classify.MiscUtil.ZoomOutPageTransformer;


public class ReviewByNameFragment extends Fragment {

    private Linker linker;
    private RepsDatabaseHandler db;
    private ArrayList<ItemForReview> thingsForReview;
    private ViewPager pager;
    private ViewPagerAdapterReviewByName pagerAdapter;


    public ReviewByNameFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_review_view_name_pager, container, false);
        linker = (Linker) getActivity();
        db = linker.getRepsDb();

        pager = (ViewPager) myInflatedView.findViewById(R.id.pager_name);
        //pager.setPageTransformer(true, new ZoomOutPageTransformer());

        Log.d("page", "opened pager fragment");

        if (db.getAllNames().isEmpty()) {
            thingsForReview = new ArrayList<>();
            thingsForReview.add(new ItemForReview("", 0, 0, "", 0, 0, 0,
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>(),
                    new HashMap<String, ArrayList<Double>>()));
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
