package classify.Interfaces;

import com.shimmerresearch.android.Shimmer;

import java.util.HashMap;

import classify.DatabaseClasses.DatabaseHandler;

/**
 * Created by joe on 23/06/15.
 */
public interface Linker {

    HashMap<String,String> getAddressesMap();

    HashMap<String,Shimmer> getShimmersMap();

    DatabaseHandler getDb();

    void toggleIsPlotting();

    boolean getIsPlotting();

    HashMap<String,Boolean> getPlotSensorsMap();

    HashMap<String,Boolean> getPlotSignalsMap();

    boolean getisTestStreaming();

    void toggleTestStreaming();

    void openDrawer();

    String[] findSpecificLabels(int exercise);

    int findIndex(Object label);



}
