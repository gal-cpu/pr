package com.example.pr.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FavoriteList implements Serializable {
    protected ArrayList<Item> favoriteItemsList;

    public FavoriteList(ArrayList<Item> favoriteItemsList) {
        this.favoriteItemsList = favoriteItemsList;
    }

    public FavoriteList() {
        this.favoriteItemsList = new ArrayList<>();
    }

    public ArrayList<Item> getFavoriteItemsList() {
        return favoriteItemsList;
    }

    public void setFavoriteItemsList(ArrayList<Item> favoriteItemsList) {
        this.favoriteItemsList = favoriteItemsList;
    }
}
