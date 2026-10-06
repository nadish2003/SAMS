package com.sams.model;

/**
 * Subject model representing an academic module/subject belonging to a course.
 */
public class Subject {

    private int id;
    private String name;
    private String code;
    private int courseId;
    private String courseName; // Optional display helper

    public Subject() {
    }

    public Subject(int id, String name, String code, int courseId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseId = courseId;
    }

    public Subject(String name, String code, int courseId) {
        this.name = name;
        this.code = code;
        this.courseId = courseId;
    }

    public Subject(int id, String name, String code, int courseId, String courseName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseId = courseId;
        this.courseName = courseName;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
