package com.shimmerresearch.DataBaseVidView;

import java.util.ArrayList;

/**
 * Created by joe on 10/06/15.
 */
public class Detail {
    private int id;
    private String name;
    private ArrayList<Point> points;
    private String storedFileName;

    public Detail(int id, String name, ArrayList<Point> points, String storedFileName) {
        this.id = id;
        this.name = name;
        this.points = points;

        this.storedFileName = storedFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public Detail(String name, ArrayList<Point> points, String storedFileName) {
        this.name = name;
        this.points = points;
        this.storedFileName = storedFileName;

    }

    public Detail() { }

    public int getId() { return id; }

    public String getName() { return name; }


    public void setPoints(ArrayList<Point> points) { this.points = points; }

    public ArrayList<Point> getPoints() { return points; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

}
