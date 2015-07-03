package com.cmw;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class RepAttempt2AllFiles {

    private static int REACH = 20;
    private static double UPPER_BOUND = 10.2;
    private static double LOWER_BOUND = 9.6;
    private static int END_CHOP = REACH + 2;
    private static int REP_STRECH = 15;

    public static void main(String[] args) throws IOException {

        FileInputStream in = null;
        String[] files = {"DL", "DL2", "DL3", "SQ", "SQ2", "SQ3", "LUL", "LUL2", "LUL3"};


        for (String file : files) {

            System.out.println("***" + file + "***");
            ArrayList<Double> points = new ArrayList<>();
            in = new FileInputStream("justFilteredData/output" + file + ".csv");
            Scanner scan = new Scanner(in);

            double foo;
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
                    //       System.out.println("flatspotbetween " + i + " and " + (i + REACH));
                    //loads of flat spots,,, time to join them,, how,,, need to keep track of them,
                    flatSpots.add(new FlatSpot(i, i + REACH));

                }
            }

            //combine flatspots,

            // if end is within another start or end,
            //

            ArrayList<FlatSpot> squishedFlatSpots = new ArrayList<>();

            for (int i = 0; i < flatSpots.size(); i++) {
                int start = flatSpots.get(i).getStart();
                int newEnd = flatSpots.get(i).getEnd();
                // if end is within another flatspot, new End = that flatspots end,

                while (i < flatSpots.size() && flatSpots.get(i).contains(newEnd)) {
                    newEnd = flatSpots.get(i).getEnd();
                    i++;
                }
                squishedFlatSpots.add(new FlatSpot(start, newEnd));
                System.out.println("NEW FLATSPOT BETWEEN " + start + " and " + newEnd);

            }
            System.out.println();
            System.out.println("Wild guess of " + squishedFlatSpots.size() + " reps");
            System.out.println();


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

                ArrayList<Double> rep = new ArrayList<>();
                FlatSpot flat = squishedFlatSpots.get(i);
                FlatSpot nextFlat = squishedFlatSpots.get(i + 1);

                int startOfRep;
                //start of rep is the end of the first flat
                if (flat.getSize() > REP_STRECH) {
                    startOfRep = flat.getEnd() - REP_STRECH;
                } else {
                    startOfRep = flat.getStart();
                }

                int endOfRep;
                // end of rep is the start of the next flat
                if (flat.getSize() > REP_STRECH) {
                    endOfRep = nextFlat.getStart() + REP_STRECH;
                } else {
                    endOfRep = nextFlat.getEnd();
                }

                rep.addAll(points.subList(startOfRep, endOfRep+1));

                // if its an odd dead lift,, add it to end of last one, else minty
                if (file.contains("DL") && i % 2 == 1) {
                    repsList.get(repsList.size()-1).addAll(rep);
                } else {
                    repsList.add(rep);
                }
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

                rep.addAll(points.subList(startOfRep, endOfRep+1));

                if (file.contains("DL")) {
                    repsList.get(repsList.size()-1).addAll(rep);
                } else {
                    repsList.add(rep);
                }

            }

            //Time to output reps
            FileWriter out = null;
            BufferedWriter bw = null;

            // need to handle deadlifts,, hmmm, squish 2 cols 2gether,

            out = new FileWriter("Reps/outputRep" + file + ".csv");
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



