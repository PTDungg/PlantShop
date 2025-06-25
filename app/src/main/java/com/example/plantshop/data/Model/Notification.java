package com.example.plantshop.data.Model;

import java.util.Date;

public class Notification {
    private String id;
    private String orderId;
    private String description;
    private String imageUrl;
    private Date timestamp;

    // Constructor
    public Notification() {
    }

    public Notification(String id, String orderId, String description, String imageUrl, Date timestamp) {
        this.id = id;
        this.orderId = orderId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}