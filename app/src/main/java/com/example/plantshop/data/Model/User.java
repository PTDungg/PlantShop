
package com.example.plantshop.data.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String email;
    private String name;
    private String address;
    private String phone;
    private String role;

    public User() {

    }

    public User(String uid, String email, String name, String address, String phone, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
