package classify.Interfaces;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.HashMap;

import classify.DatabaseClasses.LabelsAndExercisesDatabaseHandler;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.ListItems.ItemSensorForConnectFragment;

/**
 * Created by joe on 23/06/15.
 */
public interface Linker {

    HashMap<String,String> getAddressesMap();

    HashMap<String,Shimmer> getShimmersMap();

    RepsDatabaseHandler getRepsDb();

    LabelsAndExercisesDatabaseHandler getLabelsExerciseDb();

    void toggleIsPlotting();

    boolean getIsPlotting();

    HashMap<String,Boolean> getPlotSensorsMap();

    HashMap<String,Boolean> getPlotSignalsMap();

    void openDrawer();

    void populateLabelsAndExercises();

    ArrayList<ItemSensorForConnectFragment> getSensorsForConnect();

    void closeKeyboard();

    void reBuildShimmerMap();

}
