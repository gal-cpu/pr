package com.example.pr.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order implements Serializable {
    private String orderId;
    private List<ItemCart> items;
    private double totalPrice;
    private String status;
    private User user;
    private long timestamp;

    public Order(String orderId, List<ItemCart> items, double totalPrice, String status, User user, long timestamp) {
        this.orderId = orderId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Order() {
    }

    private double calculateTotalPrice() {
        double sum = 0;
        for (ItemCart item : items) {
            sum += item.getItem().getPrice()*item.getAmount();
        }
        return sum;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<ItemCart> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setItems(List<ItemCart> items) {
        this.items = items;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(this.timestamp));
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(this.timestamp));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", items=" + items +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", timestamp=" + timestamp +
                '}';
    }
}