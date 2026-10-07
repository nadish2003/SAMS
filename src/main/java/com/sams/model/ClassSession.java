package com.sams.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ClassSession model representing a scheduled teaching class session.
 */
public class ClassSession {

    private int id;
    private int courseId;
    private int subjectId;
    private int lecturerId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;

    // Display helpers for UI
    private String courseName;
    private String subjectName;
    private String subjectCode;
    private String lecturerName;

    public ClassSession() {
    }

    public ClassSession(int id, int courseId, int subjectId, int lecturerId, LocalDate sessionDate, LocalTime sessionTime) {
        this.id = id;
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
    }

    public ClassSession(int courseId, int subjectId, int lecturerId, LocalDate sessionDate, LocalTime sessionTime) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
    }

    public ClassSession(int id, int courseId, int subjectId, int lecturerId, LocalDate sessionDate, LocalTime sessionTime,
                        String courseName, String subjectName, String subjectCode, String lecturerName) {
        this.id = id;
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.courseName = courseName;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.lecturerName = lecturerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    @Override
    public String toString() {
        return (subjectCode != null ? subjectCode : "Subject " + subjectId) + " - " + sessionDate + " " + sessionTime;
    }
}
