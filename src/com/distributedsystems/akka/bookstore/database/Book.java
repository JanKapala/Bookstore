package com.distributedsystems.akka.bookstore.database;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Book {
    private String title;
    private BigDecimal price;

    public Book(String title, BigDecimal price){
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
