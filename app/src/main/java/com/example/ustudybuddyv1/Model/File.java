package com.example.ustudybuddyv1.Model;

public class File {
    private String name;
    private String url;

    // Default constructor required for Firebase
    public File() {}

    // Constructor to initialize file details
    public File(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
