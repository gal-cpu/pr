package com.example.pr.model;

import androidx.annotation.NonNull;

public class Item
{
    protected String id;
    protected String pName; //שם המוצר
    protected String pNote; //הערה בהתאם למוצר

    protected String type; //סוג המוצר
    protected double rate;
    protected  double sumRate;//דירוג המוצר

    protected int numCounter;
    protected String image; //תמונה של המוצר
    protected double price; //מחיר המוצר


    public Item(String id, String image, int numCounter, String pName, String pNote, double price, double rate, double sumRate, String type) {
        this.id = id;
        this.image = image;
        this.numCounter = numCounter;
        this.pName = pName;
        this.pNote = pNote;
        this.price = price;
        this.rate = rate;
        this.sumRate = sumRate;
        this.type = type;
    }

    public Item() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpNote() {
        return pNote;
    }

    public void setpNote(String pNote) {
        this.pNote = pNote;
    }


    public int getNumCounter() {
        return numCounter;
    }

    public void setNumCounter(int numCounter) {
        this.numCounter = numCounter;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSumRate() {
        return sumRate;
    }

    public void setSumRate(double sumRate) {
        this.sumRate = sumRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", pName='" + pName + '\'' +
                ", pNote='" + pNote + '\'' +
                ", rate=" + rate +'\'' +
                ", price=" + price +
                '}';
    }
}
