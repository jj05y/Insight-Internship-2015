package classify.ListItems;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemForReview {

    private String name;
    private  int actualLabel;
    private int predictedLabel;
    private String fileName;
    private int exercise;
    private int rowID;
    private int rep;
    private HashMap<String,ArrayList<Double>> accelMagPoints;
    private HashMap<String,ArrayList<Double>> accelXPoints;
    private HashMap<String,ArrayList<Double>> accelYPoints;
    private HashMap<String,ArrayList<Double>> accelZPoints;
    private HashMap<String,ArrayList<Double>> pitchPoints;
    private HashMap<String,ArrayList<Double>> rollPoints;
    private HashMap<String,ArrayList<Double>> yawPoints;
    private HashMap<String,ArrayList<Double>> quatWPoints;
    private HashMap<String,ArrayList<Double>> quatXPoints;
    private HashMap<String,ArrayList<Double>> quatYPoints;
    private HashMap<String,ArrayList<Double>> quatZPoints;
    private HashMap<String,ArrayList<Double>> gyroXPoints;
    private HashMap<String,ArrayList<Double>> gyroYPoints;
    private HashMap<String,ArrayList<Double>> gyroZPoints;
    private HashMap<String,ArrayList<Double>> gyroMagPoints;

    public ItemForReview(String name, int actualLabel, int predictedLabel, String fileName, int exercise, int rowID, int rep, HashMap<String, ArrayList<Double>> accelMagPoints, HashMap<String, ArrayList<Double>> accelXPoints, HashMap<String, ArrayList<Double>> accelYPoints, HashMap<String, ArrayList<Double>> accelZPoints, HashMap<String, ArrayList<Double>> pitchPoints, HashMap<String, ArrayList<Double>> rollPoints, HashMap<String, ArrayList<Double>> yawPoints, HashMap<String, ArrayList<Double>> quatWPoints, HashMap<String, ArrayList<Double>> quatXPoints, HashMap<String, ArrayList<Double>> quatYPoints, HashMap<String, ArrayList<Double>> quatZPoints, HashMap<String, ArrayList<Double>> gyroXPoints, HashMap<String, ArrayList<Double>> gyroYPoints, HashMap<String, ArrayList<Double>> gyroZPoints, HashMap<String, ArrayList<Double>> gyroMagPoints) {
        this.name = name;
        this.actualLabel = actualLabel;
        this.fileName = fileName;
        this.exercise = exercise;
        this.rowID = rowID;
        this.rep = rep;
        this.accelMagPoints = accelMagPoints;
        this.accelXPoints = accelXPoints;
        this.accelYPoints = accelYPoints;
        this.accelZPoints = accelZPoints;
        this.pitchPoints = pitchPoints;
        this.rollPoints = rollPoints;
        this.yawPoints = yawPoints;
        this.quatWPoints = quatWPoints;
        this.quatXPoints = quatXPoints;
        this.quatYPoints = quatYPoints;
        this.quatZPoints = quatZPoints;
        this.gyroXPoints = gyroXPoints;
        this.gyroYPoints = gyroYPoints;
        this.gyroZPoints = gyroZPoints;
        this.gyroMagPoints = gyroMagPoints;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public ItemForReview(String name) {
        this.name = name;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public ItemForReview() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActualLabel() {
        return actualLabel;
    }

    public void setActualLabel(int actualLabel) {
        this.actualLabel = actualLabel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HashMap<String, ArrayList<Double>> getAccelMagPoints() {
        return accelMagPoints;
    }

    public void setAccelMagPoints(HashMap<String, ArrayList<Double>> accelMagPoints) {
        this.accelMagPoints = accelMagPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelXPoints() {
        return accelXPoints;
    }

    public void setAccelXPoints(HashMap<String, ArrayList<Double>> accelXPoints) {
        this.accelXPoints = accelXPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelYPoints() {
        return accelYPoints;
    }

    public void setAccelYPoints(HashMap<String, ArrayList<Double>> accelYPoints) {
        this.accelYPoints = accelYPoints;
    }

    public HashMap<String, ArrayList<Double>> getAccelZPoints() {
        return accelZPoints;
    }

    public void setAccelZPoints(HashMap<String, ArrayList<Double>> accelZPoints) {
        this.accelZPoints = accelZPoints;
    }

    public HashMap<String, ArrayList<Double>> getPitchPoints() {
        return pitchPoints;
    }

    public void setPitchPoints(HashMap<String, ArrayList<Double>> pitchPoints) {
        this.pitchPoints = pitchPoints;
    }

    public HashMap<String, ArrayList<Double>> getRollPoints() {
        return rollPoints;
    }

    public void setRollPoints(HashMap<String, ArrayList<Double>> rollPoints) {
        this.rollPoints = rollPoints;
    }

    public HashMap<String, ArrayList<Double>> getYawPoints() {
        return yawPoints;
    }

    public void setYawPoints(HashMap<String, ArrayList<Double>> yawPoints) {
        this.yawPoints = yawPoints;
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
