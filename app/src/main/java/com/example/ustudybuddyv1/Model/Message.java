package com.example.ustudybuddyv1.Model;

public class Message {
    private String senderId;
    private String messageText;
    private long timestamp;

    // Default constructor for Firebase
    public Message() {}

    // Constructor
    public Message(String senderId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSenderId() { return senderId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }
}
