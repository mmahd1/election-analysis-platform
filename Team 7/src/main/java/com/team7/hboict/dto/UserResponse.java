package com.team7.hboict.dto;

public class UserResponse {
    private String email;
    private String firstName;
    private String lastName;
    private boolean admin;

    public UserResponse(String email) {
        this.email = email;
    }

    public UserResponse(String email, String firstName, String lastName, boolean admin) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAdmin() {
        return admin;
    }
}
