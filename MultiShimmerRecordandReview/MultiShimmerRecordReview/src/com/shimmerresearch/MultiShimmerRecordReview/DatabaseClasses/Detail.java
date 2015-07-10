package com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses;

import java.util.ArrayList;
import java.util.HashMap;


public class Detail {
    private int id;
    private String name;
    private String date;
    private String videoFile;
    private int label;
    private int exercise;
    private HashMap<String,ArrayList<Double>> AccelMagPoints;
    private HashMap<String,ArrayList<Double>> AccelXPoints;
    private HashMap<String,ArrayList<Double>> AccelYPoints;
    private HashMap<String,ArrayList<Double>> AccelZPoints;
    private HashMap<String,ArrayList<Double>> PitchPoints;
    private HashMap<String,ArrayList<Double>> RollPoints;
    private HashMap<String,ArrayList<Double>> YawPoints;
    private int rep;

    public Detail() {
    }

    public Detail(int id, String name, String date, String videoFile, int label, int exercise, HashMap<String, ArrayList<Double>> accelMagPoints, HashMap<String, ArrayList<Double>> accelXPoints, HashMap<String, ArrayList<Double>> accelYPoints, HashMap<String, ArrayList<Double>> accelZPoints, HashMap<String, ArrayList<Double>> pitchPoints, HashMap<String, ArrayList<Double>> rollPoints, HashMap<String, ArrayList<Double>> yawPoints, int rep) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.videoFile = videoFile;
        this.label = label;
        this.exercise = exercise;
        AccelMagPoints = accelMagPoints;
        AccelXPoints = accelXPoints;
        AccelYPoints = accelYPoints;
        AccelZPoints = accelZPoints;
        PitchPoints = pitchPoints;
        RollPoints = rollPoints;
        YawPoints = yawPoints;
        this.rep = rep;
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

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
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
}
