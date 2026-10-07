package com.sams.dao;

import com.sams.model.ClassSession;
import com.sams.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassSessionDAO – CRUD data access for scheduled class sessions.
 */
public class ClassSessionDAO {

    /**
     * Creates a new scheduled class session.
     */
    public void create(ClassSession session) throws SQLException {
        String sql = "INSERT INTO class_sessions (course_id, subject_id, lecturer_id, session_date, session_time) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, session.getCourseId());
            stmt.setInt(2, session.getSubjectId());
            stmt.setInt(3, session.getLecturerId());
            stmt.setDate(4, Date.valueOf(session.getSessionDate()));
            stmt.setTime(5, Time.valueOf(session.getSessionTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Finds all class sessions with course, subject, and lecturer names.
     */
    public List<ClassSession> findAll() throws SQLException {
        List<ClassSession> list = new ArrayList<>();
        String sql = "SELECT cs.id, cs.course_id, cs.subject_id, cs.lecturer_id, cs.session_date, cs.session_time, " +
                     "c.name AS course_name, s.name AS subject_name, s.code AS subject_code, l.name AS lecturer_name " +
                     "FROM class_sessions cs " +
                     "JOIN courses c ON cs.course_id = c.id " +
                     "JOIN subjects s ON cs.subject_id = s.id " +
                     "JOIN lecturers l ON cs.lecturer_id = l.id " +
                     "ORDER BY cs.session_date DESC, cs.session_time DESC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Finds a single class session by ID.
     */
    public ClassSession findById(int id) throws SQLException {
        String sql = "SELECT cs.id, cs.course_id, cs.subject_id, cs.lecturer_id, cs.session_date, cs.session_time, " +
                     "c.name AS course_name, s.name AS subject_name, s.code AS subject_code, l.name AS lecturer_name " +
                     "FROM class_sessions cs " +
                     "JOIN courses c ON cs.course_id = c.id " +
                     "JOIN subjects s ON cs.subject_id = s.id " +
                     "JOIN lecturers l ON cs.lecturer_id = l.id " +
                     "WHERE cs.id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds all scheduled classes for a specific lecturer.
     */
    public List<ClassSession> findByLecturerId(int lecturerId) throws SQLException {
        List<ClassSession> list = new ArrayList<>();
        String sql = "SELECT cs.id, cs.course_id, cs.subject_id, cs.lecturer_id, cs.session_date, cs.session_time, " +
                     "c.name AS course_name, s.name AS subject_name, s.code AS subject_code, l.name AS lecturer_name " +
                     "FROM class_sessions cs " +
                     "JOIN courses c ON cs.course_id = c.id " +
                     "JOIN subjects s ON cs.subject_id = s.id " +
                     "JOIN lecturers l ON cs.lecturer_id = l.id " +
                     "WHERE cs.lecturer_id = ? " +
                     "ORDER BY cs.session_date DESC, cs.session_time DESC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Updates an existing scheduled class session.
     */
    public void update(ClassSession session) throws SQLException {
        String sql = "UPDATE class_sessions SET course_id = ?, subject_id = ?, lecturer_id = ?, session_date = ?, session_time = ? " +
                     "WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getCourseId());
            stmt.setInt(2, session.getSubjectId());
            stmt.setInt(3, session.getLecturerId());
            stmt.setDate(4, Date.valueOf(session.getSessionDate()));
            stmt.setTime(5, Time.valueOf(session.getSessionTime()));
            stmt.setInt(6, session.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a scheduled session by ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM class_sessions WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private ClassSession mapRow(ResultSet rs) throws SQLException {
        return new ClassSession(
            rs.getInt("id"),
            rs.getInt("course_id"),
            rs.getInt("subject_id"),
            rs.getInt("lecturer_id"),
            rs.getDate("session_date").toLocalDate(),
            rs.getTime("session_time").toLocalTime(),
            rs.getString("course_name"),
            rs.getString("subject_name"),
            rs.getString("subject_code"),
            rs.getString("lecturer_name")
        );
    }
}
