package com.shimmerresearch.MultiShimmerRecordReview.Interfaces;

import com.shimmerresearch.MultiShimmerRecordReview.Util.DataBaseHandler;
import com.shimmerresearch.android.Shimmer;

import java.util.HashMap;

/**
 * Created by joe on 23/06/15.
 */
public interface Linker {

    HashMap<String,String> getAddressesMap();

    HashMap<String,Shimmer> getShimmersMap();

    HashMap<String, Boolean> getIsConnectedMap();

    DataBaseHandler getDb();

    void toggleIsPlotting();

    boolean getIsPlotting();
}
