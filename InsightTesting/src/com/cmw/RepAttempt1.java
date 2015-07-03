package com.cmw;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RepAttempt1 {

    private final static int NUMBER_POLES = 8;
    private final static int NUMBER_ZEROS = 8;
    private final static double GAIN = 6.097498035;

////// this never got off the ground, i just calc'd max and min values :/
    public static void main(String[] args) throws IOException {

        System.out.println("hello");
        FileInputStream in = null;

        ArrayList<Double> points = new ArrayList<>();
        double max = 0;
        double min = 0;
        double foo;


        in = new FileInputStream("outputs/outputSQ.csv");
        Scanner scan = new Scanner(in);

        while (scan.hasNextDouble()) {

            foo = scan.nextDouble();
            points.add(foo);
            if (points.size() == 1) {
                max = foo;
                min = foo;

            } else {
                if (foo > max) {
                    max = foo;
                }
                if (foo < min) {
                    min = foo;
                }
            }

        }

        System.out.println("Max: " + max + "  Min: " + min);

        if (in != null) {
            in.close();
        }

    }
}



