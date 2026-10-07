package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.model.Attendance;

import java.sql.SQLException;
import java.util.List;

/**
 * AttendanceService – business logic and validation for attendance marking.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public List<Attendance> getAttendanceForSession(int classSessionId, int courseId) throws SQLException {
        if (classSessionId <= 0 || courseId <= 0) {
            throw new IllegalArgumentException("Invalid class session or course.");
        }
        return attendanceDAO.getAttendanceForSession(classSessionId, courseId);
    }

    public void saveAllAttendance(List<Attendance> list) throws SQLException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("No attendance records to save.");
        }
        for (Attendance att : list) {
            validateStatus(att.getStatus());
        }
        attendanceDAO.saveAll(list);
    }

    public void markSingleAttendance(int studentId, int classSessionId, String status) throws SQLException {
        validateStatus(status);
        Attendance att = new Attendance(studentId, classSessionId, status.toUpperCase());
        attendanceDAO.saveOrUpdate(att);
    }

    private void validateStatus(String status) {
        if (status == null || (!status.equalsIgnoreCase("PRESENT") &&
                               !status.equalsIgnoreCase("ABSENT") &&
                               !status.equalsIgnoreCase("LATE"))) {
            throw new IllegalArgumentException("Status must be PRESENT, ABSENT, or LATE.");
        }
    }
}
