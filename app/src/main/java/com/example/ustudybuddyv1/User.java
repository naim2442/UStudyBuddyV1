package com.example.ustudybuddyv1;

public class User {
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private String faculty;

    // Default constructor required for Firestore
    public User() {
    }

    // Constructor with parameters
    public User(String userId, String username, String fullName, String email, String faculty) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.faculty = faculty;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
