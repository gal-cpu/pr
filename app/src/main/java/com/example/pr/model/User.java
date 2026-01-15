package com.example.pr.model;

public class User
{
    protected String id;
    protected String fName;
    protected String lName;
    protected String email;
    protected String phone;
    protected String password;
    protected boolean isAd=false;


    public User(String email, String fName, String id, String lName, String password, String phone, boolean isAd) {
        this.email = email;
        this.fName = fName;
        this.id = id;
        this.lName = lName;
        this.password = password;
        this.phone = phone;
        this.isAd = isAd;
    }

    public boolean gatIsAd() {
        return isAd;
    }

    public void setIsAd(boolean ad) {
        isAd = ad;
    }

    public User() {
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




    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", admin='" + isAd + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
