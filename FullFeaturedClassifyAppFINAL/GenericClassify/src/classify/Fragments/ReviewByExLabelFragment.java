package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import com.cmw.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import classify.Adapters.ExpandableListAdapter;
import classify.Adapters.ViewPagerAdapterReviewByExLabel;
import classify.Constants.C;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForReview;
import classify.MiscUtil.ZoomOutPageTransformer;


public class ReviewByExLabelFragment extends Fragment {

    private Linker linker;
    private RepsDatabaseHandler db;
    private ArrayList<ItemForReview> thingsForReview;
    private ViewPager pager;
    private ViewPagerAdapterReviewByExLabel pagerAdapter;
    private List<String> listDataHeaders;
    private HashMap<String, List<String>> listDataChildren;


    public ReviewByExLabelFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_review_view_exlabel_pager, container, false);
        linker = (Linker) getActivity();
        db = linker.getRepsDb();
        pager = (ViewPager) myInflatedView.findViewById(R.id.pager_nameEx);
        //pager.setPageTransformer(true, new ZoomOutPageTransformer());


        //////////////////////////
        // Data for EXP listOfReps view
        //////////////////////////

        listDataHeaders = new ArrayList<>();
        listDataChildren = new HashMap<>();



        for (int i = 1; i < C.EXERCISES.size(); i++) {
            listDataHeaders.add(C.EXERCISES.get(i));
            ArrayList<String> children = new ArrayList<>();
            for (String label : C.LABELS_EXERCISE_MAP.get(C.EXERCISES.get(i))) {
                if (!label.equals(C.AUTO_LABEL)) { // autolabel
                    children.add(label);
                }
            }
            listDataChildren.put(C.EXERCISES.get(i), children);
        }



        ExpandableListView expListView = (ExpandableListView) myInflatedView.findViewById(R.id.expandable_list_view);
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(getActivity(), listDataHeaders, listDataChildren);
        expListView.setAdapter(expandableListAdapter);

/*        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPos, long l) {
                int exercise = groupPos;
                int label = -1;

                thingsForReview = db.getAllWithExerciseAndLabel(exercise, label);

                pagerAdapter = new ViewPagerAdapterReviewByExLabel(thingsForReview, getActivity(), db, C.REVIEW_BY_LABEL);
                pager.setAdapter(pagerAdapter);

                return false;
            }
        });*/

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPos, int childPos, long l) {

                int exercise = groupPos+1;
                //need to find index of label,,,
                String header = listDataHeaders.get(groupPos);
                String labelText = listDataChildren.get(header).get(childPos);
                int labelIndex = (childPos == 0 ? 0 : childPos+1);
                Log.d("labelFind", "I think label is: " + labelText + " with index: " + labelIndex);

                thingsForReview = db.getAllWithExerciseAndLabel(exercise, labelIndex);

                pagerAdapter = new ViewPagerAdapterReviewByExLabel(thingsForReview, getActivity(), db);
                pager.setAdapter(pagerAdapter);

                return true;
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
}
