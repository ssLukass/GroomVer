package com.example.groomver.models;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {
    private String image;
    private String title;
    private String description;
    private String key;
    private String ownerUID;
    private int price;
    private boolean isFavorite;
    private long creationDate;
    private String city;


    public Product() {}

    public Product(String image, String title, String description, String key, String ownerUID, int price, boolean isFavorite, long creationDate, String city) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.key = key;
        this.ownerUID = ownerUID;
        this.price = price;
        this.isFavorite = isFavorite;
        this.creationDate = creationDate;
        this.city = city;
    }



    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return key.equals(product.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
