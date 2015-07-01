package com.cmw;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovingAvg {

    private static int DEPTH = 20;


    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;
        FileWriter out = null;
        String[] files = {"DL", "LUL", "SQ", "DL2", "LUL2", "SQ2", "DL3", "LUL3", "SQ3"};

        ArrayList<Double> points = new ArrayList<>();
        ArrayList<Double> filteredPoints = new ArrayList<>();

        for (String file :files) {
            in = new FileInputStream("inputs/data" + file + ".csv" );
            Scanner scan = new Scanner(in);

            int k = 0;
            while (scan.hasNextDouble()) {
                points.add(scan.nextDouble());
                filteredPoints.add(movingAvg(points, k));
                k++;
            }

            out = new FileWriter("outputs/output" + file + ".csv" );
            for (int i = 0; i < points.size(); i++) {
                out.write(points.get(i) + ", " + filteredPoints.get(i) + "\n");
            }

            points.clear();
            filteredPoints.clear();
        }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }


        }

    private static double movingAvg(ArrayList<Double> points, int i) {
        if (points.size() < DEPTH) {
            return points.get(i);
        }

        double sum = 0;
        for (int k = 0; k < DEPTH; k++) {
            sum += points.get(points.size() - 1 - k);
        }


        return sum / DEPTH;
    }


}
