package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ParserForData {

    public static void main(String[] args) throws IOException {

        FileInputStream in;
        FileWriter out;
        out = new FileWriter("outfile");
        BufferedWriter bw = new BufferedWriter(out);


        in = new FileInputStream("filename");
        Scanner scan = new Scanner(in);

        while (scan.hasNextLine()) {

            String line = scan.nextLine();

           /* ///////////////////////
            // Choose Rows
            /////////////////////
            if (line.split(",")[1].equals("N")) {
                System.out.println(line);
                bw.write(line + "\n");
            }


            /////////////////
            // white space
            ////////////////////
            if (line.contains("\t")) {
                line.replace("\t", ",");
            }
            if (line.contains("    ")) {
                line.replace("    ", ",");
            }
            if (line.contains("   ")) {
                line.replace("   ", ",");
            }
            if (line.contains("  ")) {
                line.replace("  ", ",");
            }
            if (line.contains(" ")) {
                line.replace(" ", ",");
            }*/

            /////////////////
            // Delete Col :/
            //////////////////

            int colToDel  = 1;
            //chop
            String[] choppedLine = line.split(",");
            List<String> crapbits = Arrays.asList(choppedLine);
            ArrayList<String> bits = new ArrayList<String>();

            for (String cb: crapbits) {
               bits.add(cb);
            }
            bits.remove(colToDel);
            //bits.remove(anothercol);

            //reasemble
            String newline = "";
            for (String bit: bits) {
                newline += bit + ",";
            }
            bw.write(newline + "\n");




        }

        in.close();
        bw.close();
        out.close();



    }
}
