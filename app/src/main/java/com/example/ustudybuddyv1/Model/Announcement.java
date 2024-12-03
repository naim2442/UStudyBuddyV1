package com.example.ustudybuddyv1.Model;

public class Announcement {
    private String title;
    private String content;
    private String date;

    public Announcement() { }

    public Announcement(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Announcement(String title, String content) {
        this.title = title;
        this.content = content;

    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}
