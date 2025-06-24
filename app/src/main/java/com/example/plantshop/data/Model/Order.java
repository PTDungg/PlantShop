package com.example.plantshop.data.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String orderDate;
    private int totalAmount;
    private boolean status;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    private List<OrderItem> items;


    public Order(List<OrderItem> items) {
        this.items = new ArrayList<>();
    }


    public Order(String orderId, String orderDate, int totalAmount, boolean status,
                 String customerName,String customerEmail, String customerPhone, String customerAddress, List<OrderItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.items = items != null ? items : new ArrayList<>();
    }


    public String getOrderId() {
        return orderId;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getOrderDate() {
        return orderDate;
    }


    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    public int getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }


    public boolean isStatus() {
        return status;
    }


    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }


    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }


    public String getCustomerAddress() {
        return customerAddress;
    }


    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }


    public List<OrderItem> getItems() {
        return items;
    }


    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}