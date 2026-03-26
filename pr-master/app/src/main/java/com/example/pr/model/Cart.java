package com.example.pr.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Cart {
    protected ArrayList<ItemCart> itemArrayList;

    public Cart(ArrayList<ItemCart> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public Cart() {
        this.itemArrayList = new ArrayList<>();
    }


    public ArrayList<ItemCart> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<ItemCart> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public void addItem(ItemCart item) {

        boolean found=false;
        if (this.itemArrayList == null)
            this.itemArrayList = new ArrayList<>();
        if(this.itemArrayList.size()>0){

            for(ItemCart itcart:this.itemArrayList){

                if(itcart.getItem().getId().equals(item.getItem().id)){

                    found=true;
                }
            }


        }
        if(found){
            item.amount++;

        }
        else
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
