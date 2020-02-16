package com.example.techworkshops.model;

public class Workshop {
    private String id;
    private String courseName;
    private String instructorName;
    private String description;

    public Workshop() {
    }

    public Workshop(String courseName, String instructorName, String description) {
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
