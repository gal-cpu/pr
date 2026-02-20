package com.example.pr.model;

import androidx.annotation.NonNull;

public class User {
    protected String id;
    protected String fName;
    protected String lName;

    protected String email;
    protected String phone;
    protected String password;
    protected boolean isAd;
    protected Cart cart;

    public User(String email, String fName, String id, String lName,
                String password, String phone, boolean isAd, Cart cart) {
        this.email = email;
        this.fName = fName;
        this.id = id;
        this.lName = lName;
        this.password = password;
        this.phone = phone;
        this.isAd = isAd;
        this.cart = cart;
    }

    public User() {
    }

    public boolean gatIsAd() {
        return isAd;
    }

    public void setIsAd(boolean ad) {
        isAd = ad;
    }

    public String getEmail() {
        return email;
    }

    public String getfName() {
        return fName;
    }

    public String getId() {
        return id;
    }

    public String getlName() {
        return lName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAd() {
        return isAd;
    }

    public void setAd(boolean ad) {
        isAd = ad;
    }

    public Cart getCart() {
        if (cart == null)
            this.cart = new Cart();
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isAd=" + isAd +
                ", cart=" + cart +
                '}';
    }
}
