package com.example.ustudybuddyv1.Model;

public class Message {
    private String senderId;
    private String messageText;
    private long timestamp;

    private String fileUrl; // New field for file URL

    // Default constructor for Firebase
    public Message() {}

    // Constructor
    public Message(String senderId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    //getter for file message
    // Constructor for file message
    public Message(String senderId, String messageText, long timestamp, String fileUrl) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.fileUrl = fileUrl;
    }

    // Getters
    public String getSenderId() { return senderId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }

    public String getFileUrl() { return fileUrl; } // Getter for file URL
}
