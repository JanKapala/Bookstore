package com.distributedsystems.akka.bookstore;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseAdapter {
    public static void main(String[] args){




        String database_path = System.getProperty("user.dir") + "\\database";
        String orders_path = database_path + "\\orders.csv";

        File orders = new File(orders_path);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(orders);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

//            // adding header to csv
//            String[] header = { "Name", "Class", "Marks" };
//            writer.writeNext(header);

            // add data to csv
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String formatted_date = dateFormat.format(date);
            String[] data1 = { "Iliad", "120", formatted_date };
            writer.writeNext(data1);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

//    public String getPrice(){
//
//    }
//
//    public String order(){
//
//    }
//
//    public String getText(){
//
//    }
}
