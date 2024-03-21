package com.vedasole.ekartecommercebackend.entity;

public enum Role {
    USER("User"),
    ADMIN("Admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getRole() {
        return this.value;
    }
}