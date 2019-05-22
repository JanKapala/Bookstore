package com.distributedsystems.akka.bookstore.database;

import java.math.BigInteger;

public class Book {
    private String title;
    private BigInteger price;

    public Book(String title, BigInteger price){
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    @Override
    public String toString(){
        return "Title: " + this.title + ", price: " + this.price;
    }

    public String[] toEntry(){
        String[] entry =  { title, price.toString() };
        return entry;
    }
}
