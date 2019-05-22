package com.distributedsystems.akka.bookstore.database;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private Book book;
    private Date date;

    public Order(Book book, Date date){
        this.book = book;
        this.date = date;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatted_date = dateFormat.format(this.date);
        return this.book.toString() + ", Date" + formatted_date;
    }

    public String[] toEntry(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatted_date = dateFormat.format(this.date);

        String[] entry =  { book.getTitle(), book.getPrice().toString(), formatted_date };
        return entry;
    }
}
