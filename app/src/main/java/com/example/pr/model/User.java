package com.example.pr.model;

public class User
{
    protected String id;
    protected String fName;
    protected String lName;
    protected String email;
    protected String phone;
    protected String password;
    protected boolean admin;

    public User(String id, String fName, String lName, String email, String phone, String password, boolean admin) {
        this.email = email;
        this.fName = fName;
        this.id = id;
        this.lName = lName;
        this.password = password;
        this.phone = phone;
        this.admin=admin;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }
    public boolean getAdmin() {
        return admin;
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
    public void setAdmin(Boolean admin) {
        this.admin = admin;
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
                ", isAdmin=" + admin +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
