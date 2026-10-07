package com.sams.dao;

import com.sams.model.Attendance;
import com.sams.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceDAO – handles attendance marking and retrieval with PreparedStatement.
 */
public class AttendanceDAO {

    /**
     * Inserts a new attendance record or updates status if one already exists for this student + class_session.
     */
    public void saveOrUpdate(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, class_session_id, status) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), marked_at = CURRENT_TIMESTAMP";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getClassSessionId());
            stmt.setString(3, attendance.getStatus());
            stmt.executeUpdate();
        }
    }

    /**
     * Saves or updates a list of attendance records in a single database transaction.
     */
    public void saveAll(List<Attendance> list) throws SQLException {
        if (list == null || list.isEmpty()) return;

        Connection conn = DBConnection.getConnection();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO attendance (student_id, class_session_id, status) " +
                         "VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE status = VALUES(status), marked_at = CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Attendance att : list) {
                    stmt.setInt(1, att.getStudentId());
                    stmt.setInt(2, att.getClassSessionId());
                    stmt.setString(3, att.getStatus());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Loads all enrolled students in the course for a session, along with their recorded status
     * (or default 'ABSENT' if not yet marked).
     */
    public List<Attendance> getAttendanceForSession(int classSessionId, int courseId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT s.id AS student_id, s.name AS student_name, s.registration_number AS student_reg_no, " +
                     "COALESCE(a.id, 0) AS attendance_id, " +
                     "COALESCE(a.status, 'ABSENT') AS status, " +
                     "a.marked_at " +
                     "FROM students s " +
                     "LEFT JOIN attendance a ON s.id = a.student_id AND a.class_session_id = ? " +
                     "WHERE s.course_id = ? " +
                     "ORDER BY s.registration_number ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classSessionId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("student_id"),
                        classSessionId,
                        rs.getString("status"),
                        rs.getTimestamp("marked_at"),
                        rs.getString("student_name"),
                        rs.getString("student_reg_no")
                    ));
                }
            }
        }
        return list;
    }
}
