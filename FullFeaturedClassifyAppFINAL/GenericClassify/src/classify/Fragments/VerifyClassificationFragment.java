package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmw.R;

import java.util.ArrayList;

import classify.Adapters.ViewPagerAdapterVerify;
import classify.Constants.C;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForVerification;

/**
 * Created by joe on 22/07/15.
 */
public class VerifyClassificationFragment extends Fragment {

    private View myInflatedView;
    private ViewPager pager;
    private ViewPagerAdapterVerify pagerAdapter;
    private RepsDatabaseHandler db;
    private Linker linker;
    private ArrayList<ItemForVerification> items;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_verify_classification, container, false);

        linker = (Linker) getActivity();
        db = linker.getRepsDb();


        //need to pass this an int to indicate what exercise
        int exercise = getArguments().getInt("exercise");
        items = db.getItemsForVerification(exercise);
        Log.d("verifyEx", "exercise is: " + C.EXERCISES.get(exercise));

        pager = (ViewPager) myInflatedView.findViewById(R.id.pager_verify_classification);
        pagerAdapter = new ViewPagerAdapterVerify(items, getActivity(), db, this);
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

    public void nextPage() {
        if (pager.getCurrentItem() == items.size()-1) {
            Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();
            linker.openDrawer();
        } else {
            Log.d("nextPage", "changing page from " + pager.getCurrentItem());
            pager.setCurrentItem(pager.getCurrentItem() + 1, false);
        }
    }
}