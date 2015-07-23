package classify.Constants;


public class C {

    public static final String[] LABELS = {"Label Not Set", "Square", "Circle", "Triangle", "Up/Down", "Left/Right"};
    public static final String[] EXERCISES = {"Type Not Set", "Shape", "Wiggle"};
    //todo - always update corresponding arrays - OR ELSE!
    public static final int[] SHAPE_LABEL_INDEXES = {0, 1, 2};
    public static final int[] WIGGLE_LABEL_INDEXES = {0, 3, 4};


    public final static String MAIN_SENSOR = "mainSensor";
    public static final String[] SENSORS = {MAIN_SENSOR};

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
}
