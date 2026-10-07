package com.sams.model;

/**
 * Student model representing an enrolled student.
 */
public class Student {

    private int id;
    private String name;
    private String registrationNumber;
    private int courseId;
    private String email;
    private String phone;
    private String courseName; // Display helper
    private String courseCode; // Display helper

    public Student() {
    }

    public Student(int id, String name, String registrationNumber, int courseId, String email, String phone) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.courseId = courseId;
        this.email = email;
        this.phone = phone;
    }

    public Student(String name, String registrationNumber, int courseId, String email, String phone) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.courseId = courseId;
        this.email = email;
        this.phone = phone;
    }

    public Student(int id, String name, String registrationNumber, int courseId, String email, String phone, String courseName, String courseCode) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.courseId = courseId;
        this.email = email;
        this.phone = phone;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @Override
    public String toString() {
        return registrationNumber + " - " + name;
    }
}
