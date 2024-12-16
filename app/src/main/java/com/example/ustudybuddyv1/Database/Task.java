package com.example.ustudybuddyv1.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String description;
    private long dueDate;

    private int completionPercentage;
    private String priority;
    private boolean isCompleted;
    private boolean isRecurring;

    private String userId;

    public Task(@NonNull String id, String title, String description, long dueDate, String priority, boolean isCompleted, boolean isRecurring, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : "Medium";  // Default to Medium if null
        this.isCompleted = isCompleted;
        this.isRecurring = isRecurring;
        this.userId = userId;
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    // Add getters and setters
    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completion) {
        this.completionPercentage = completion;
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    // Other methods if needed



}


