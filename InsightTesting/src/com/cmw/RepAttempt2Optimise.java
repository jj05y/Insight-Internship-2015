package com.cmw;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RepAttempt2Optimise {

    private final static int NUMBER_POLES = 8;
    private final static int NUMBER_ZEROS = 8;
    private final static double GAIN = 6.097498035;
    private static int REACH = 5;
    private static double UPPER_BOUND = 10.2;
    private static double LOWER_BOUND = 9.6;


    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;

        ArrayList<Double> points = new ArrayList<>();

        double foo;
        System.out.println("foobar");


        in = new FileInputStream("justFilteredData/outputSQ.csv");
        Scanner scan = new Scanner(in);

        while (scan.hasNextDouble()) {

            foo = scan.nextDouble();
            points.add(foo);
        }

        if (in != null) {
            in.close();
        }

        //have all points, now to check,
        ArrayList<FlatSpot> flatSpots = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            int startIndex = i;
            //System.out.println(points.get(i));
            if (withinBounds(points.get(i))) {
                i += REACH;
                while (i < points.size() && withinBounds(points.get(i))) {
                    i+= REACH;
                }
                flatSpots.add(new FlatSpot(startIndex, i));
                System.out.println("NEW FLATSPOT BETWEEN " + startIndex + " and " + i);

            }
        }
    }

    private static boolean withinBounds(double point) {
        if (point > UPPER_BOUND) return false;
        if (point < LOWER_BOUND) return false;

        return true;
    }
}



