package com.example.groomver.models;

public class Ad {
    String fotoProduct;
    String nameProduct;
    String descriptionProduct;
    String UID;
    int price;


    public Ad() {

    }

    public Ad(String fotoProduct, String nameProduct, String descriptionProduct, String UID, int price) {
        this.fotoProduct = fotoProduct;
        this.nameProduct = nameProduct;
        this.descriptionProduct = descriptionProduct;
        this.UID = UID;
        this.price = price;
    }

    public String getFotoProduct() {
        return fotoProduct;
    }

    public void setFotoProduct(String fotoProduct) {
        this.fotoProduct = fotoProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getDescriptionProduct() {
        return descriptionProduct;
    }

    public void setDescriptionProduct(String descriptionProduct) {
        this.descriptionProduct = descriptionProduct;
    }

    public String getUID(){
        return UID;
    }

    public void setUID(String UID){
        this.UID = UID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
