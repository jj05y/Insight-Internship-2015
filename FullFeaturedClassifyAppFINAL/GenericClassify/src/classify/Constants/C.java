package classify.Constants;


import java.util.ArrayList;
import java.util.HashMap;

public class C {

    public static final HashMap<String, ArrayList<String>> LABELS_EXERCISE_MAP = new HashMap<>();
    public static final ArrayList<String> EXERCISES = new ArrayList<>();

    public static final String AUTO_LABEL = "Auto-label";
    public static final String LABEL_NOT_SET = "Label Not Set";

    public static final ArrayList<String> SENSORS = new ArrayList<>();

    public final static String ACCEL_MAG = "accelMag";
    public final static String ACCEL_X = "accelX";
    public final static String ACCEL_Y = "accelY";
    public final static String ACCEL_Z = "accelZ";
    public final static String GYRO_X = "gyroX";
    public final static String GYRO_Y = "gyroY";
    public final static String GYRO_Z = "gyroZ";
    public final static String GYRO_MAG = "gyroMag";
    public final static String QUAT_W = "quatW";
    public final static String QUAT_X = "quatX";
    public final static String QUAT_Y = "quatY";
    public final static String QUAT_Z = "quatZ";
    public final static String PITCH = "pitch";
    public final static String ROLL = "roll";
    public final static String YAW = "yaw";
    public final static String[] SIGNALS = {ACCEL_MAG, ACCEL_X, ACCEL_Y, ACCEL_Z, GYRO_X, GYRO_Y, GYRO_Z, GYRO_MAG, QUAT_W, QUAT_X, QUAT_Y, QUAT_Z, PITCH, ROLL, YAW};


    public final static double SAMPLE_RATE = 51.2;
    public static final int ACCEL_RANGE = 3;
    public static final int GSR_RANGE = 0;
    public static final int GYRO_RANGE = 1;
    public static final int MAG_RANGE = 0;

    public static final int NUM_TREES = 40;
    public static final int NUM_ATTRIBUTES = 70;

    public static final String DEFAULT_ADDRESS = "00:00:00:00:00:00";

    public static final String SENSORS_ADDRESS_NAME_STRING = "sensorsaddressesandnames";
}
