package com.example.pr.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Cart {
    protected ArrayList<Item> itemArrayList;

    public Cart(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public Cart() {
        this.itemArrayList = new ArrayList<>();
    }


    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public void addItem(Item item) {
        if (this.itemArrayList == null)
            this.itemArrayList = new ArrayList<>();

        this.itemArrayList.add(item);
    }


    @NonNull
    @Override
    public String toString() {
        return "Cart{" +
                "itemArrayList=" + itemArrayList +
                '}';
    }
}
