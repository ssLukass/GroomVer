package com.example.groomver.models;

import java.io.Serializable;

public class Product {
    private String image;
    private String title;
    private String description;
    private String key;
    private String ownerUID;
    private int price;

    public Product(){

    }

    public Product(String image, String title, String description, String key, String ownerUID, int price) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.key = key;
        this.ownerUID = ownerUID;
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}


