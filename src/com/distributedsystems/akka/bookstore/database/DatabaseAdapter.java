package com.distributedsystems.akka.bookstore.database;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseAdapter {
    private String database_root_path;
    private String books_folder_path;
    private String orders_file_path;
    private File orders_file;
    private String books_file_path;
    private File books_file;

    public static void main(String[] args){
//        String database_root_path = System.getProperty("user.dir") + "\\databases\\database_1";
//        DatabaseAdapter databaseAdapter = new DatabaseAdapter(database_root_path);
//
//        databaseAdapter.reset();
//        databaseAdapter.initialize();
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Type the name of the book that you want find:");
//        String title = scanner.nextLine();
//
//        Book book = databaseAdapter.findBook(title);
//        System.out.println(book);
//
//        System.out.println("The price of this book is: " + databaseAdapter.getPrice(book.getTitle()));
//
//        System.out.println("If you want order this book enter: 'y' ");
//        String flag = scanner.nextLine();
//        while(flag.equals("y")){
//            databaseAdapter.order(book.getTitle());
//            System.out.println("If you want order this book again enter 'y' ");
//            flag = scanner.nextLine();
//        }
//
//        System.out.println("Thank you for using this service, bye ;)");

    }

    public DatabaseAdapter(String database_root_path){
        this.database_root_path = database_root_path;

        this.books_folder_path = database_root_path + "\\books";

        this.orders_file_path = database_root_path + "\\orders.csv";
        this.orders_file = new File(orders_file_path);

        this.books_file_path = database_root_path + "\\books.csv";
        this.books_file = new File(books_file_path);
    }

    public void resetBooks(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(books_file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();
    }

    public void resetOrders(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(orders_file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();
    }

    public void reset(){
        resetBooks();
        resetOrders();
    }

    public void initializeBooks(){
        try{
            FileWriter fileWriter = new FileWriter(books_file);
            CSVWriter csvwriter = new CSVWriter(fileWriter);

            // adding header to csv
            String[] header = { "Title", "Price" };
            csvwriter.writeNext(header);

            // Create list of entries from files' names in the books.csv file
            File folder = new File(books_folder_path);
            File[] listOfFiles = folder.listFiles();

            for(File entry : listOfFiles){
                if(entry.isFile()){
                    // Title
                    String file_name = entry.getName();
                    Pattern pattern = Pattern.compile("^([^.]*).+$");
                    Matcher matcher = pattern.matcher(file_name);
                    matcher.matches();
                    String title = matcher.group(1);

                    // Price
                    Random generator = new Random();
                    Integer random_int = generator.nextInt(300) + 50;
                    BigDecimal price = new BigDecimal(random_int.toString());

                    // Save
                    Book book = new Book(title, price);
                    csvwriter.writeNext(book.toEntry());
                }
            }

            csvwriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeOrders(){
        try {
            FileWriter fileWriter = new FileWriter(orders_file);
            CSVWriter csvwriter = new CSVWriter(fileWriter);

            // adding header to csv
            String[] header = { "Title", "Price",  "Date"};
            csvwriter.writeNext(header);

            csvwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initialize(){
        initializeBooks();
        initializeOrders();
    }

    public Book findBook(String title){
        Book book = null;
        try {
            FileReader fileReader = new FileReader(books_file);
            CSVReader csvreader = new CSVReader(fileReader);

            // Skip headers
            csvreader.readNext();

            // Find title in the the rest of the file
            String[] nextLine;
            while ((nextLine = csvreader.readNext()) != null) {
                if(title.equals(nextLine[0])){
                    BigDecimal price = new BigDecimal(nextLine[1]);
                    book = new Book(title, price);
                    break;
                }
            }

            csvreader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    public BigDecimal getPrice(String title){
        Book book = findBook(title);
        if(book != null) return book.getPrice();
        return null;
    }

    public Order order(String title){
        Date date = new Date();
        Book book = findBook(title);
        Order order = new Order(book, date);

        try {
            FileWriter fileWriter = new FileWriter(orders_file, true);
            CSVWriter csvwriter = new CSVWriter(fileWriter);
            csvwriter.writeNext(order.toEntry());
            csvwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order;

    }
//
//    public String getText(){
//
//    }
}
