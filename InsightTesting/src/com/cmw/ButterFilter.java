package com.cmw;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ButterFilter {

    private final static int NUMBER_POLES = 8;
    private final static int NUMBER_ZEROS = 8;
    private final static double GAIN = 6.097498035;


    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;
        FileWriter out = null;

        ArrayList<Double> points = new ArrayList<>();
        ArrayList<Double> filteredPoints = new ArrayList<>();


        try {
            in = new FileInputStream("dataSQ.csv");
            Scanner scan = new Scanner(in);

            while (scan.hasNextDouble()) {
                points.add(scan.nextDouble());
                filteredPoints.add(filterButterFixed(filteredPoints, points));
            }

            out = new FileWriter("output.csv");
            for (int i = 0; i < points.size(); i++) {
                out.write(points.get(i) + ", " + filteredPoints.get(i) + "\n");
            }


        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }

    }




    static private double filterButterFixed(ArrayList<Double> newData, ArrayList<Double> oldData) //function to do butterworth filtering (n=8, fc=20Hz) (http://www-users.cs.york.ac.uk/~fisher/cgi-bin/mkfscript)
    {
        double[] xv;
        double[] yv;
        double result;

        xv = new double[NUMBER_POLES + 1];
        int start = oldData.size() - (NUMBER_POLES + 1);
        if (start < 0)
            start = 0;
        List<Double> xvL = oldData.subList(start, oldData.size());
        int diff = xv.length - xvL.size();

        for (int i = 0; i < xvL.size(); i++) {
            xv[diff + i] = xvL.get(i);
        }

        yv = new double[NUMBER_ZEROS];
        start = newData.size() - (NUMBER_ZEROS);
        if (start < 0)
            start = 0;
        List<Double> yvL = newData.subList(start, newData.size());
        diff = yv.length - yvL.size();
        for (int i = 0; i < yvL.size(); i++) {
            yv[diff + i] = yvL.get(i);
        }
        for (int i = 0; i < xv.length; i++) {
            xv[i] = xv[i] / GAIN;
        }
        result = (xv[0] + xv[8]) + 8 * (xv[1] + xv[7]) + 28 * (xv[2] + xv[6])
                + 56 * (xv[3] + xv[5]) + 70 * xv[4]
                + (-0.0268965572 * yv[0]) + (-0.3097392114 * yv[1])
                + (-1.5888819744 * yv[2]) + (-4.7528602453 * yv[3])
                + (-9.0940029435 * yv[4]) + (-11.4399227870 * yv[5])
                + (-9.2880746605 * yv[6]) + (-4.4840549977 * yv[7]);
        return result;
    }


}
