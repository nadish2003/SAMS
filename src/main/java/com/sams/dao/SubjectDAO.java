package com.sams.dao;

import com.sams.model.Subject;
import com.sams.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * SubjectDAO – CRUD data access operations for Subject entity.
 */
public class SubjectDAO {

    /**
     * Creates a new subject and assigns the generated ID.
     */
    public void create(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (name, code, course_id) VALUES (?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getCode());
            stmt.setInt(3, subject.getCourseId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    subject.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Finds all subjects with associated course name.
     */
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.code, s.course_id, c.name AS course_name " +
                     "FROM subjects s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "ORDER BY s.id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("code"),
                    rs.getInt("course_id"),
                    rs.getString("course_name")
                ));
            }
        }
        return subjects;
    }

    /**
     * Finds a single subject by ID.
     */
    public Subject findById(int id) throws SQLException {
        String sql = "SELECT s.id, s.name, s.code, s.course_id, c.name AS course_name " +
                     "FROM subjects s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getInt("course_id"),
                        rs.getString("course_name")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds all subjects belonging to a specific course.
     */
    public List<Subject> findByCourseId(int courseId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.code, s.course_id, c.name AS course_name " +
                     "FROM subjects s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.course_id = ? " +
                     "ORDER BY s.id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getInt("course_id"),
                        rs.getString("course_name")
                    ));
                }
            }
        }
        return subjects;
    }

    /**
     * Updates an existing subject.
     */
    public void update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, code = ?, course_id = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getCode());
            stmt.setInt(3, subject.getCourseId());
            stmt.setInt(4, subject.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a subject by ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
