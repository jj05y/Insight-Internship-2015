package com.shimmerresearch.shimmerlogexample;

import java.io.Serializable;

/**
 * Created by joe on 11/06/15.
 */
public class Point implements Serializable {
    double xVal;
    double yVal;
    double zVal;
    int index;

    public double getxVal() {
        return xVal;
    }


    public void setxVal(double xVal) {
        this.xVal = xVal;

    }


    public double getyVal() {
        return yVal;

    }


    public void setyVal(double yVal) {
        this.yVal = yVal;

    }


    public double getzVal() {
        return zVal;

    }


    public void setzVal(double zVal) {
        this.zVal = zVal;

    }


    public int getIndex() {
        return index;

    }


    public void setIndex(int index) {
        this.index = index;

    }
}