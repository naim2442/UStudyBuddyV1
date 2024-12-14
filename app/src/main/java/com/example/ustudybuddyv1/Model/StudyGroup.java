package com.example.ustudybuddyv1.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudyGroup implements Serializable {
    private String groupId;
    private String groupName;
    private String creatorId;
    private String location;
    private String decodedLocationName; // Decoded name from lat/long
    private List<String> members;
    private int membersCount;
    private boolean isPublic;
    private String subject;

    public boolean isRecommended; // This field will indicate if the group is recommended
    private Map<String, Object> files; // This will hold the files for the group

    private String dateTime; // Store both date and time as a String (e.g., "2024-12-01 15:00")
    private String description;
    private List<String> tags;
    private String imageUrl; // URL for uploaded image

    // Default constructor for Firebase
    public StudyGroup() {}

    // Constructor to initialize all fields
    public StudyGroup(String groupId, String groupName, String creatorId, String location,
                      String decodedLocationName, List<String> members, int membersCount,
                      boolean isPublic, String subject, String dateTime, String description,
                      List<String> tags, String imageUrl, boolean isRecommended, Map<String, Object> files) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.location = location;
        this.decodedLocationName = decodedLocationName;
        this.members = members;
        this.membersCount = membersCount;
        this.isPublic = isPublic;
        this.subject = subject;
        this.dateTime = dateTime;
        this.description = description;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.isRecommended = isRecommended;
        this.files = files;
    }


    public Map<String, Object> getFiles() {
        return files;
    }

    public void setFiles(Map<String, Object> files) {
        this.files = files;
    }

    // Getters and Setters for all fields
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDecodedLocationName() {
        return decodedLocationName;
    }

    public void setDecodedLocationName(String decodedLocationName) {
        this.decodedLocationName = decodedLocationName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Helper method to check if a user is already a member
    public boolean isMember(String userId) {
        return members != null && members.contains(userId);
    }

    // Helper method to add a user to the group
    public void joinGroup(String userId) {
        if (members == null) {
            members = new ArrayList<>();  // Initialize the list if it's null
        }
        if (!members.contains(userId)) {
            members.add(userId);
            membersCount = members.size();  // Update the membersCount based on the list size
        }
    }

    public void leaveGroup(String userId) {
        if (members != null) {
            members.remove(userId);
        }
    }
}
