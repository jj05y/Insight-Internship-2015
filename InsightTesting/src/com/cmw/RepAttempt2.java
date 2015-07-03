package com.cmw;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.Scanner;

public class RepAttempt2 {

    private final static int NUMBER_POLES = 8;
    private final static int NUMBER_ZEROS = 8;
    private final static double GAIN = 6.097498035;
    private static int REACH = 20;
    private static double UPPER_BOUND = 10.2;
    private static double LOWER_BOUND = 9.6;
    private static int END_CHOP = REACH + 2;
    private static int REP_STRECH = 50;
    private static String file = "SQ";


    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;


        ArrayList<Double> points = new ArrayList<>();

        double foo;
        System.out.println("bar");


        in = new FileInputStream("justFilteredData/output" + file + ".csv");
        Scanner scan = new Scanner(in);

        while (scan.hasNextDouble()) {

            foo = scan.nextDouble();
            points.add(foo);
        }
        in.close();


        //have all points, now to check,
        ArrayList<FlatSpot> flatSpots = new ArrayList<>();

        for (int i = 0; i < points.size() - REACH; i++) {
            double thisPoint = points.get(i);
            double thatPoint = points.get(i + REACH);

            if (withinBounds(thisPoint) && withinBounds(thatPoint)) {
                //then we have a flat spot.
                System.out.println("flatspotbetween " + i + " and " + (i + REACH));
                //loads of flat spots,,, time to join them,, how,,, need to keep track of them,
                flatSpots.add(new FlatSpot(i, i + 20));

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

            while (i < flatSpots.size() && flatSpots.get(i).contains(newEnd)) {
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
        // i guess so, sometimes there is :/

        //if last flatspot contains last point && first flat contains 1st point, then #reps = #flats - 1
        //mayb not first, but like 22 in or somin call this END CHOP,,,

        // theres always going to be some sort of flat spot at the start,


        ArrayList<ArrayList<Double>> repsList = new ArrayList<>();

        // chop up graph
        for (int i = 0; i < squishedFlatSpots.size() - 1; i++) {

            FlatSpot flat = squishedFlatSpots.get(i);
            FlatSpot nextFlat = squishedFlatSpots.get(i + 1);

            ArrayList<Double> rep = new ArrayList<>();

            int startOfRep;
            //start of rep is the end of the first flat
            if (flat.getSize() > REP_STRECH)
                startOfRep = flat.getEnd() - REP_STRECH;
            else
                startOfRep = flat.getStart();

            int endOfRep;
            // end of rep is the start of the next flat
            if (flat.getSize() > REP_STRECH)
                endOfRep = nextFlat.getStart() + REP_STRECH;
            else
                endOfRep = nextFlat.getEnd();

            for (int j = startOfRep; j <= endOfRep; j++) {
                rep.add(points.get(j));
            }
            repsList.add(rep);
        }

        //if the end point isnt contained in a flat,,,
        if (!squishedFlatSpots.get(squishedFlatSpots.size() - 1).contains(points.size() - END_CHOP)) {
            //,,,, we need to count the data from the last flat to end

            ArrayList<Double> rep = new ArrayList<>();
            FlatSpot flat = squishedFlatSpots.get(squishedFlatSpots.size() - 1);

            int startOfRep;
            if (flat.getSize() > REP_STRECH)
                startOfRep = flat.getEnd() - REP_STRECH;
            else
                startOfRep = flat.getStart();

            int endOfRep = points.size() - 1;

            for (int j = startOfRep; j <= endOfRep; j++) {
                rep.add(points.get(j));
            }
            repsList.add(rep);

        }

        //Time to output reps
        FileWriter out = null;
        BufferedWriter bw = null;

        out = new FileWriter("Reps/output" + file + ".csv");
        bw = new BufferedWriter(out);
        for (int i = 0; i < biggestRep(repsList); i++) {
            for (int j = 0; j < repsList.size(); j++) {
                if (i < repsList.get(j).size())
                    bw.write(repsList.get(j).get(i) + ", ");
            }
            bw.newLine();
        }

        bw.close();
        out.close();


        //todo chop up video using flat spots


    }

    private static int biggestRep(ArrayList<ArrayList<Double>> repsList) {

        int size = 0;

        for (ArrayList<Double> list : repsList) {
            if (list.size() > size) {
                size = list.size();
            }
        }

        return size;
    }

    private static boolean withinBounds(double point) {
        if (point > UPPER_BOUND) return false;
        if (point < LOWER_BOUND) return false;

        return true;
    }
}



