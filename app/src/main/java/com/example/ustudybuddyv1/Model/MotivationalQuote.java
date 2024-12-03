package com.example.ustudybuddyv1.Model;

public class MotivationalQuote {
    private String quote;
    private String author;

    public MotivationalQuote() {
        // Default constructor required for calls to DataSnapshot.getValue(MotivationalQuote.class)
    }

    public MotivationalQuote(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

