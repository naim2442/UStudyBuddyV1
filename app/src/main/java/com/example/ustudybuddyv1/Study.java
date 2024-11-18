package com.example.ustudybuddyv1;

public class Study {
    private String title;
    private int progress; // This can represent the completion percentage

    // Constructor
    public Study(String title, int progress) {
        this.title = title;
        this.progress = progress;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for progress
    public int getProgress() {
        return progress;
    }
}
