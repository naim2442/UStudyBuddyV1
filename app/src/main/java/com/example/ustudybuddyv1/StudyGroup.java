package com.example.ustudybuddyv1;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.List;

public class StudyGroup implements Serializable {
    private String groupName;
    private int membersCount;
    private String location;
    private String subject;
    private String imageUrl;
    private String creatorId;
    private List<String> members;

    // Full constructor (already exists in your code)
    public StudyGroup(String groupName, int membersCount, String location, String subject, String imageUrl, String creatorId, List<String> members) {
        this.groupName = groupName;
        this.membersCount = membersCount;
        this.location = location;
        this.subject = subject;
        this.imageUrl = imageUrl;
        this.creatorId = creatorId;
        this.members = members;
    }

    // Default constructor for Firebase
    public StudyGroup() {
    }

    // Simplified constructor (added for your case)
    public StudyGroup(String groupName, String location, String creatorId, int membersCount) {
        this.groupName = groupName;
        this.location = location;
        this.creatorId = creatorId;
        this.membersCount = membersCount;
        this.subject = "";  // Default empty subject
        this.imageUrl = ""; // Default empty image URL
        this.members = new ArrayList<>(); // Initialize empty members list
    }

    // Getters and setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    // Method to check if a user is a member
    public boolean isMember(String userId) {
        return members != null && members.contains(userId);
    }
}
