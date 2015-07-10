package com.shimmerresearch.MultiShimmerRecordReview.ListItems;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemForReview {

    private String name;
    private  int label;
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

    public ItemForReview(String name, int label, String fileName, int exercise, int rowID, int rep, HashMap<String, ArrayList<Double>> accelMagPoints, HashMap<String, ArrayList<Double>> accelXPoints, HashMap<String, ArrayList<Double>> accelYPoints, HashMap<String, ArrayList<Double>> accelZPoints, HashMap<String, ArrayList<Double>> pitchPoints, HashMap<String, ArrayList<Double>> rollPoints, HashMap<String, ArrayList<Double>> yawPoints) {
        this.name = name;
        this.label = label;
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

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
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
}
