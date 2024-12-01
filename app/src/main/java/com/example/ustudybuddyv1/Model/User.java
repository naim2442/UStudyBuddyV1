package com.example.ustudybuddyv1.Model;

public class User {
    private String userId;                // Firebase UID
    private String name;                  // Full Name
    private String email;                 // Email Address
    private String password;              // User's Password
    private String studentId;             // Student ID
    private String university;            // University name
    private String course;                // Course name
    private String locationPreference;    // User's location preference
    private String cgpa;                  // CGPA of the user
    private String highestEducationLevel; // Highest education level (e.g., Degree in Computer Science)
    private String semesterYear;          // Current semester year

    // Default constructor required for Firestore/Realtime Database
    public User() {
    }

    // Constructor for basic user data (for new user registration, for example)
    public User(String userId, String name, String email, String password, String studentId, String university, String course, String locationPreference) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentId = studentId;
        this.university = university;
        this.course = course;
        this.locationPreference = locationPreference;
    }

    // Constructor for editing user profile (with additional fields like CGPA, education level, and semester year)
    public User(String userId, String name, String email, String password, String studentId, String university, String course, String locationPreference, String cgpa, String highestEducationLevel, String semesterYear) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentId = studentId;
        this.university = university;
        this.course = course;
        this.locationPreference = locationPreference;
        this.cgpa = cgpa;
        this.highestEducationLevel = highestEducationLevel;
        this.semesterYear = semesterYear;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getLocationPreference() {
        return locationPreference;
    }

    public void setLocationPreference(String locationPreference) {
        this.locationPreference = locationPreference;
    }

    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    public String getHighestEducationLevel() {
        return highestEducationLevel;
    }

    public void setHighestEducationLevel(String highestEducationLevel) {
        this.highestEducationLevel = highestEducationLevel;
    }

    public String getSemesterYear() {
        return semesterYear;
    }

    public void setSemesterYear(String semesterYear) {
        this.semesterYear = semesterYear;
    }
}
