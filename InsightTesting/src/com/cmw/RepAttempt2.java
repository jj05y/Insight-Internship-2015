package com.cmw;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RepAttempt2 {

    private final static int NUMBER_POLES = 8;
    private final static int NUMBER_ZEROS = 8;
    private final static double GAIN = 6.097498035;
    private static int REACH = 20;
    private static double UPPER_BOUND = 10.2;
    private static double LOWER_BOUND = 9.6;


    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;


        ArrayList<Double> points = new ArrayList<>();

        double foo;
        System.out.println("bar");


        in = new FileInputStream("justFilteredData/outputLUL2.csv");
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

        for (int i = 0; i < points.size() - REACH; i++) {
            double thisPoint = points.get(i);
            double thatPoint = points.get(i + REACH);

            if (withinBounds(thisPoint) && withinBounds(thatPoint)) {
                //then we have a flat spot.
                System.out.println("flatspotbetween " + i + " and " + (i+REACH));
                //loads of flat spots,,, time to join them,, how,,, need to keep track of them,
                flatSpots.add(new FlatSpot(i, i+20));

            }
        }

        //combine flatspots,

        // if end is within another start or end,
        //

        ArrayList<FlatSpot> squishedFlatSpots = new ArrayList<>();

        for (int i = 0; i < flatSpots.size(); i++) {
            int start = flatSpots.get(i).getStart();
            int newEnd = flatSpots.get(i).getEnd();
            // if end is within another flatspot, new End = that flatspots end, phwaar,

            while ( i < flatSpots.size() && flatSpots.get(i).contains(newEnd)) {
                newEnd = flatSpots.get(i).getEnd();
                i++;
            }
            squishedFlatSpots.add(new FlatSpot(start, newEnd));
            System.out.println("NEW FLATSPOT BETWEEN " + start + " and " + newEnd);

        }

        // so now i know where all the flat spots are,,,
        // the reps are, after 1st flat spot,
        // infact, after every flat spot,
        //is there a flat spot at the end?
        // i guess so,


    }

    private static boolean withinBounds(double point) {
        if (point > UPPER_BOUND) return false;
        if (point < LOWER_BOUND) return false;

        return true;
    }
}



