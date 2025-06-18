package com.example.plantshop.data.Model;

import java.util.List;

public class Order {
    private String id_order;
    private String orderDate;
    private double totalAmount;
    private String status;
    private String userName;
    private String phone;
    private String address;
    private List<Item> item;

    public Order() {
        // Required empty constructor for Firestore
    }

    public Order(String id_order, String orderDate, double totalAmount, String status,
                 String userName, String phone, String address, List<Item> item) {
        this.id_order = id_order;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.userName = userName;
        this.phone = phone;
        this.address = address;
        this.item = item;
    }

    // Getters and Setters
    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}

