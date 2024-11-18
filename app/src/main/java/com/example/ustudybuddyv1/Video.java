package com.example.ustudybuddyv1;

public class Video {
    private String title;
    private int thumbnailResId; // Resource ID for the thumbnail image

    public Video(String title, int thumbnailResId) {
        this.title = title;
        this.thumbnailResId = thumbnailResId;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }
}
