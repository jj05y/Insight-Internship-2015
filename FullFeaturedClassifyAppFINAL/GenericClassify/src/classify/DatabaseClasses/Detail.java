package classify.DatabaseClasses;

import java.util.ArrayList;
import java.util.HashMap;


public class Detail {
    private int id;
    private String name;
    private String date;
    private String videoFile;
    private int actualLabel;
    private int predictedLabel;
    private int exercise;
    private HashMap<String,ArrayList<Double>> AccelMagPoints;
    private HashMap<String,ArrayList<Double>> AccelXPoints;
    private HashMap<String,ArrayList<Double>> AccelYPoints;
    private HashMap<String,ArrayList<Double>> AccelZPoints;
    private HashMap<String,ArrayList<Double>> PitchPoints;
    private HashMap<String,ArrayList<Double>> RollPoints;
    private HashMap<String,ArrayList<Double>> YawPoints;
    private HashMap<String,ArrayList<Double>> quatWPoints;
    private HashMap<String,ArrayList<Double>> quatXPoints;
    private HashMap<String,ArrayList<Double>> quatYPoints;
    private HashMap<String,ArrayList<Double>> quatZPoints;
    private HashMap<String,ArrayList<Double>> gyroXPoints;
    private HashMap<String,ArrayList<Double>> gyroYPoints;
    private HashMap<String,ArrayList<Double>> gyroZPoints;
    private HashMap<String,ArrayList<Double>> gyroMagPoints;
    private int rep;
    private String featureString;

    public Detail() {
    }

    public Detail(int id, String name, String date, String videoFile, int actualLabel, int predictedLabel, int exercise, HashMap<String, ArrayList<Double>> accelMagPoints, HashMap<String, ArrayList<Double>> accelXPoints, HashMap<String, ArrayList<Double>> accelYPoints, HashMap<String, ArrayList<Double>> accelZPoints, HashMap<String, ArrayList<Double>> pitchPoints, HashMap<String, ArrayList<Double>> rollPoints, HashMap<String, ArrayList<Double>> yawPoints, HashMap<String, ArrayList<Double>> quatWPoints, HashMap<String, ArrayList<Double>> quatXPoints, HashMap<String, ArrayList<Double>> quatYPoints, HashMap<String, ArrayList<Double>> quatZPoints, HashMap<String, ArrayList<Double>> gyroXPoints, HashMap<String, ArrayList<Double>> gyroYPoints, HashMap<String, ArrayList<Double>> gyroZPoints, HashMap<String, ArrayList<Double>> gyroMagPoints, int rep, String featureString) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.videoFile = videoFile;
        this.actualLabel = actualLabel;
        this.predictedLabel = predictedLabel;
        this.exercise = exercise;
        this.AccelMagPoints = accelMagPoints;
        this.AccelXPoints = accelXPoints;
        this.AccelYPoints = accelYPoints;
        this.AccelZPoints = accelZPoints;
        this.PitchPoints = pitchPoints;
        this.RollPoints = rollPoints;
        this.YawPoints = yawPoints;
        this.quatWPoints = quatWPoints;
        this.quatXPoints = quatXPoints;
        this.quatYPoints = quatYPoints;
        this.quatZPoints = quatZPoints;
        this.gyroXPoints = gyroXPoints;
        this.gyroYPoints = gyroYPoints;
        this.gyroZPoints = gyroZPoints;
        this.gyroMagPoints = gyroMagPoints;
        this.rep = rep;
        this.featureString = featureString;
    }

    public String getFeatureString() {
        return featureString;
    }

    public void setFeatureString(String featureString) {
        this.featureString = featureString;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public int getActualLabel() {
        return actualLabel;
    }

    public void setActualLabel(int actualLabel) {
        this.actualLabel = actualLabel;
    }

    public int getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(int predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public HashMap<String, ArrayList<Double>> getAccelMagPoints() {
        return AccelMagPoints;
    }

    public void setAccelMagPoints(HashMap<String, ArrayList<Double>> accelMagPoints) {
        AccelMagPoints = accelMagPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelXPoints() {
        return AccelXPoints;
    }

    public void setAccelXPoints(HashMap<String, ArrayList<Double>> accelXPoints) {
        AccelXPoints = accelXPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelYPoints() {
        return AccelYPoints;
    }

    public void setAccelYPoints(HashMap<String, ArrayList<Double>> accelYPoints) {
        AccelYPoints = accelYPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelZPoints() {
        return AccelZPoints;
    }

    public void setAccelZPoints(HashMap<String, ArrayList<Double>> accelZPoints) {
        AccelZPoints = accelZPoints;
    }

    public HashMap<String, ArrayList<Double>> getPitchPoints() {
        return PitchPoints;
    }

    public void setPitchPoints(HashMap<String, ArrayList<Double>> pitchPoints) {
        PitchPoints = pitchPoints;
    }

    public HashMap<String, ArrayList<Double>> getRollPoints() {
        return RollPoints;
    }

    public void setRollPoints(HashMap<String, ArrayList<Double>> rollPoints) {
        RollPoints = rollPoints;
    }

    public HashMap<String, ArrayList<Double>> getYawPoints() {
        return YawPoints;
    }

    public void setYawPoints(HashMap<String, ArrayList<Double>> yawPoints) {
        YawPoints = yawPoints;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public HashMap<String, ArrayList<Double>> getQuatWPoints() {
        return quatWPoints;
    }

    public void setQuatWPoints(HashMap<String, ArrayList<Double>> quatWPoints) {
        this.quatWPoints = quatWPoints;
    }

    public HashMap<String, ArrayList<Double>> getQuatXPoints() {
        return quatXPoints;
    }

    public void setQuatXPoints(HashMap<String, ArrayList<Double>> quatXPoints) {
        this.quatXPoints = quatXPoints;
    }

    public HashMap<String, ArrayList<Double>> getQuatYPoints() {
        return quatYPoints;
    }

    public void setQuatYPoints(HashMap<String, ArrayList<Double>> quatYPoints) {
        this.quatYPoints = quatYPoints;
    }

    public HashMap<String, ArrayList<Double>> getQuatZPoints() {
        return quatZPoints;
    }

    public void setQuatZPoints(HashMap<String, ArrayList<Double>> quatZPoints) {
        this.quatZPoints = quatZPoints;
    }

    public HashMap<String, ArrayList<Double>> getGyroXPoints() {
        return gyroXPoints;
    }

    public void setGyroXPoints(HashMap<String, ArrayList<Double>> gyroXPoints) {
        this.gyroXPoints = gyroXPoints;
    }

    public HashMap<String, ArrayList<Double>> getGyroYPoints() {
        return gyroYPoints;
    }

    public void setGyroYPoints(HashMap<String, ArrayList<Double>> gyroYPoints) {
        this.gyroYPoints = gyroYPoints;
    }

    public HashMap<String, ArrayList<Double>> getGyroZPoints() {
        return gyroZPoints;
    }

    public void setGyroZPoints(HashMap<String, ArrayList<Double>> gyroZPoints) {
        this.gyroZPoints = gyroZPoints;
    }

    public HashMap<String, ArrayList<Double>> getGyroMagPoints() {
        return gyroMagPoints;
    }

    public void setGyroMagPoints(HashMap<String, ArrayList<Double>> gyroMagPoints) {
        this.gyroMagPoints = gyroMagPoints;
    }
}
