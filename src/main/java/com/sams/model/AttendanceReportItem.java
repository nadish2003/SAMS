package com.sams.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * AttendanceReportItem – represents a detailed record for attendance reporting.
 */
public class AttendanceReportItem {

    private String studentName;
    private String studentRegNo;
    private String courseName;
    private String subjectName;
    private String subjectCode;
    private String lecturerName;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private String status;
    private Timestamp markedAt;

    public AttendanceReportItem() {
    }

    public AttendanceReportItem(String studentName, String studentRegNo, String courseName,
                                String subjectName, String subjectCode, String lecturerName,
                                LocalDate sessionDate, LocalTime sessionTime, String status, Timestamp markedAt) {
        this.studentName = studentName;
        this.studentRegNo = studentRegNo;
        this.courseName = courseName;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.lecturerName = lecturerName;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.status = status;
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

    @Override
    public String toString() {
        return studentRegNo + " | " + subjectCode + " | " + sessionDate + " | " + status;
    }
}
