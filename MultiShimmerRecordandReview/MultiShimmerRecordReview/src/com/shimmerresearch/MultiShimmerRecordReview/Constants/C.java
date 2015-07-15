package com.shimmerresearch.MultiShimmerRecordReview.Constants;


public class C {

    public static final String[] LABELS = {"Label Not Set", "Good", "Bad"};
    public static final String[] EXERCISES = {"Exercise Not Set", "Squat", "Lunge", "Deadlift"};
    public static final int DL_INDEX = 2;

    public final static String LEFT_THIGH = "leftthigh";
    public final static String RIGHT_THIGH = "rightthigh";
    public final static String LEFT_CALF = "leftcalf";
    public final static String RIGHT_CALF = "rightcalf";
    public final static String LOWER_BACK = "lowerback";
    public static final String[] SENSORS = {LEFT_THIGH, LEFT_CALF, RIGHT_THIGH, RIGHT_CALF, LOWER_BACK};

    public final static String ACCEL_MAG  = "accelMag";
    public final static String ACCEL_X = "accelX";
    public final static String ACCEL_Y = "accelY";
    public final static String ACCEL_Z = "accelZ";
    public final static String PITCH = "pitch";
    public final static String ROLL = "roll" ;
    public final static String YAW = "yaw";
    public final static String[] SIGNALS = {ACCEL_MAG, ACCEL_X, ACCEL_Y, ACCEL_Z, PITCH, ROLL, YAW};

    public static final String REVIEW_BY_NAME = "reviewByName";
    public static final String REVIEW_BY_LABEL = "reviewByLabel";

    public final static double SAMPLE_RATE = 51.2;
    public static final int ACCEL_RANGE = 3;
    public static final int GSR_RANGE = 0;
    public static final int GYRO_RANGE = 1;
    public static final int MAG_RANGE = 0;
    ;
}
