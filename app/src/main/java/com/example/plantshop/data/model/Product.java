package com.example.plantshop.data.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private int price;
    private String category;
    private String description;
    private int quantity;
    private boolean available;

    public Product() {}

    public Product(String id, String name, String imageUrl, int price, String category, String description, int quantity, boolean available) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.available = available;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }
}
