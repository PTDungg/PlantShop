package com.example.plantshop.utils;

public class RoleManager {
    public enum Role {
        ADMIN,
        USER,
        GUEST
    }

    private static Role currentRole = Role.GUEST;

    public static Role getCurrentRole() {
        return currentRole;
    }

    public static void setCurrentRole(Role role) {
        currentRole = role;
    }
}
