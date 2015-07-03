package com.shimmerresearch.MultiShimmerRecordReview.Util;

import java.util.ArrayList;


public class Detail {
    private int id;
    private String name;
    private String date;
    private String videoFile;
    private String label;
    private String exercise;
    private ArrayList<Double> points;

    public Detail() {
    }

    public Detail(int id, String name, String date, String videoFile, String label, String exercise, ArrayList<Double> points) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.videoFile = videoFile;
        this.label = label;
        this.exercise = exercise;
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Double> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Double> points) {
        this.points = points;
    }
}
