package com.example.plantshop.data.Model;


import java.io.Serializable;


public class OrderItem implements Serializable {
    private String productID;
    private String productName;
    private int quantity;
    private int price;


    public OrderItem() {
    }


    public OrderItem(String productID, String productName, int quantity, int price) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }


    public String getProductID() {
        return productID;
    }


    public void setProductID(String productID) {
        this.productID = productID;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getPrice() {
        return price;
    }


    public void setPrice(int price) {
        this.price = price;
    }
}