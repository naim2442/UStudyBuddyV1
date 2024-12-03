package com.example.ustudybuddyv1.Model;

public class StudyTip {

    private String title;
    private String message;
    private String authorId;
    private String authorName;
    private long timestamp;

    // Default constructor required for Firebase
    public StudyTip() {
    }

    // Constructor for initializing the study tip
    public StudyTip(String title, String message, String authorId, String authorName) {
        this.title = title;
        this.message = message;
        this.authorId = authorId;
        this.authorName = authorName;
        this.timestamp = System.currentTimeMillis(); // Add timestamp for when the tip is created
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
