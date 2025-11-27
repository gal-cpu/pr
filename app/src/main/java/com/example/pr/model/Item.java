package com.example.pr.model;

public class Item
{
    protected String id;
    protected String pName; //שם המוצר
    protected String pNote; //הערה בהתאם למוצר
    protected String type; //סוג המוצר
    protected String rate; //דירוג המוצר
    protected String image; //תמונה של המוצר
    protected String price; //מחיר המוצר


    public Item(String id, String image, String pName, String pNote, String price, String rate, String type) {
        this.id = id;
        this.image = image;
        this.pName = pName;
        this.pNote = pNote;
        this.price = price;
        this.rate = rate;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", pName='" + pName + '\'' +
                ", pNote='" + pNote + '\'' +
                ", type='" + type + '\'' +
                ", rate='" + rate + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
