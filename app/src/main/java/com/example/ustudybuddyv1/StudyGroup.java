package com.example.ustudybuddyv1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class StudyGroup implements Serializable {
    private String groupName;
    private int membersCount;
    private String location;
    private String subject;
    private String imageUrl;
    private String creatorId;
    private List<String> members;
    private boolean isPublic;

    private String id;

    // Full constructor (for creating StudyGroup objects manually)
    public StudyGroup(String groupName, int membersCount, String location, String subject, String imageUrl, String creatorId, List<String> members, boolean isPublic) {
        this.groupName = groupName;
        this.membersCount = membersCount;
        this.location = location;
        this.subject = subject;
        this.imageUrl = imageUrl;
        this.creatorId = creatorId;
        this.members = members;
        this.isPublic = isPublic;
    }

    // Default constructor for Firebase deserialization
    public StudyGroup() {
        // Initialize fields with default values
        this.groupName = "";
        this.membersCount = 0;
        this.location = "";
        this.subject = "";
        this.imageUrl = "";
        this.creatorId = "";
        this.members = new ArrayList<>();
        this.isPublic = true;
    }

    // Simplified constructor for your case
    public StudyGroup(String groupName, String location, String creatorId, int membersCount) {
        this.groupName = groupName;
        this.location = location;
        this.creatorId = creatorId;
        this.membersCount = membersCount;
        this.subject = "";  // Default empty subject
        this.imageUrl = ""; // Default empty image URL
        this.members = new ArrayList<>(); // Initialize empty members list
        this.isPublic = true; // Default to public when created
    }

    // Getters and setters (same as before)
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public int getMembersCount() { return membersCount; }
    public void setMembersCount(int membersCount) { this.membersCount = membersCount; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }


    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id=id;
    }

    // Method to check if a user is a member
    public boolean isMember(String userId) {
        return members != null && members.contains(userId);
    }

    // Method to add a user to the group
    public void joinGroup(String userId) {
        if (!members.contains(userId)) {
            members.add(userId);
            membersCount++;
        }
    }


}
