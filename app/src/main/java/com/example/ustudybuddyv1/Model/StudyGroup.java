package com.example.ustudybuddyv1.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudyGroup implements Serializable {
    private String groupId;
    private String groupName;
    private String creatorId;
    private String location;
    private List<String> members;
    private int membersCount;
    private boolean isPublic;
    private String subject;

    // Default constructor for Firebase
    public StudyGroup() {
        // Empty constructor is required for Firebase to deserialize the object
    }

    // Constructor to initialize all fields
    public StudyGroup(String groupId, String groupName, String creatorId, String location,
                      List<String> members, int membersCount, boolean isPublic, String subject) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.location = location;
        this.members = members;
        this.membersCount = membersCount;
        this.isPublic = isPublic;
        this.subject = subject;
    }

    // Getter and Setter methods for all fields
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

    // Helper method to check if a user is already a member
    public boolean isMember(String userId) {
        return members != null && members.contains(userId);
    }

    // Helper method to add a user to the group
    // In StudyGroup class:

    public void joinGroup(String userId) {
        if (members == null) {
            members = new ArrayList<>();  // Initialize the list if it's null
        }
        if (!members.contains(userId)) {
            members.add(userId);
            membersCount++;  // Increment the member count
        }
    }

}
