package com.shimmerresearch.MultiShimmerRecordReview.Util;

import java.util.ArrayList;


public class HorizontalListItemForReview {

    String name;
    String label;
    String fileName;
    String exercise;
    ArrayList<Double> points;
    int rowID;



    public HorizontalListItemForReview(int rowID, String name, String exercise, String label, String fileName, ArrayList<Double> points) {
        this.rowID = rowID;
        this.name = name;
        this.label = label;
        this.fileName = fileName;
        this.points = points;
        this.exercise = exercise;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public HorizontalListItemForReview() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<Double> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Double> points) {
        this.points = points;
    }
}
