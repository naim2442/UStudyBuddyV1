package com.example.ustudybuddyv1.Model;

public class FileDetails {
    private String fileName;
    private String fileUrl;
    private long timestamp;

    public FileDetails() {
        // Default constructor required for Firebase
    }

    public FileDetails(String fileName, String fileUrl, long timestamp) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;

    }

    // Getters and setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
