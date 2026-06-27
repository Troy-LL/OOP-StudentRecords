package com.srms.model;

import java.time.LocalDateTime;

public class Student extends Person {

    private String studentId;
    private String email;
    private String phone;
    private String course;
    private int yearLevel;
    private LocalDateTime createdAt;

    public Student() {
    }

    public Student(String studentId, String firstName, String lastName, String email,
                   String phone, String course, int yearLevel) {
        super(firstName, lastName);
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.yearLevel = yearLevel;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getDisplayLabel() {
        return studentId + " - " + getFullName().trim();
    }
}
