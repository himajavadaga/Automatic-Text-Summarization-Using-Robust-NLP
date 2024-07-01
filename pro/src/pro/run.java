/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Sudeshna
 */
public class run {

    /**
     * @param args the command line arguments
     *
     *
     *
     */
    static String input, file = null, msg = "";
    static File f1;

    private static void readFile(String file) {
        /*
         * read the input file for summarization
         */
        FileReader file_in = null;
        String data;
//        System.out.print("Enter the File Name :");
//        Scanner sc = new Scanner(System.in);
//        file = sc.nextLine();

        input = "";           //initializing the input

        try {
            System.out.println(file);
            file_in = new FileReader(file);

            BufferedReader in = new BufferedReader(file_in);
            while ((data = in.readLine()) != null) {
                //System.out.print(data);
                input += data;
                input += " ";
            }
            input = input.toLowerCase(); //converts to lower case
            System.out.println("Input Recieved: \n" + input);
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(Pro.class.getName()).log(Level.SEVERE, null, ex);
            //File not found! error raised for the user!
            System.out.println("\n\tFile Not Found!");
            msg = "File Not Found!";
            throw null;
        } catch (IOException ex) {
            //File reading Error
            System.out.println("\n\tFile Format Not Supported! please use a .txt format");
            msg = "File Format Not Supported! please use a .txt format";
            throw null;
        } catch (Exception ex) {
            //handling some random error
            System.out.println("\n\tSome Unexpected Error has occured! program terminated.");
            msg = "Some Unexpected Error has occured! please try again.";
            throw null;
        }
    }

    public static String Summarist(String f) {
        try {
            if(f==null)
            {     msg="Please Select a File to Summarize.";
                   throw null;
            }
            else if(!f.substring(f.length()-4).equalsIgnoreCase(".txt")){
                msg="File Format not supported! Please Select *.txt File! ";
                throw null;
            }
            readFile(f);
            f1 = new File(f);
//        System.out.println(f.getAbsolutePath().substring(0, f.getAbsolutePath().length()-f.getName().length()) +"\n"+f.getName());
            msg = Pro.summary(input, f1.getAbsolutePath().substring(0, f1.getAbsolutePath().length() - f1.getName().length()), f1.getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return msg;
        }
    }
}
