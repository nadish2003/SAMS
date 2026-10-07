package com.sams.model;

import java.sql.Timestamp;

/**
 * Attendance model representing a student's attendance record for a class session.
 */
public class Attendance {

    private int id;
    private int studentId;
    private int classSessionId;
    private String status; // "PRESENT", "ABSENT", "LATE"
    private Timestamp markedAt;

    // Display helpers
    private String studentName;
    private String studentRegNo;

    public Attendance() {
        this.status = "ABSENT";
    }

    public Attendance(int studentId, int classSessionId, String status) {
        this.studentId = studentId;
        this.classSessionId = classSessionId;
        this.status = status;
    }

    public Attendance(int id, int studentId, int classSessionId, String status, Timestamp markedAt) {
        this.id = id;
        this.studentId = studentId;
        this.classSessionId = classSessionId;
        this.status = status;
        this.markedAt = markedAt;
    }

    public Attendance(int id, int studentId, int classSessionId, String status, Timestamp markedAt,
                      String studentName, String studentRegNo) {
        this.id = id;
        this.studentId = studentId;
        this.classSessionId = classSessionId;
        this.status = status;
        this.markedAt = markedAt;
        this.studentName = studentName;
        this.studentRegNo = studentRegNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClassSessionId() {
        return classSessionId;
    }

    public void setClassSessionId(int classSessionId) {
        this.classSessionId = classSessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(Timestamp markedAt) {
        this.markedAt = markedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    @Override
    public String toString() {
        return studentRegNo + " - " + studentName + ": " + status;
    }
}
