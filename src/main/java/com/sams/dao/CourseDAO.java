package com.sams.dao;

import com.sams.model.Course;
import com.sams.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO – CRUD data access operations for Course entity.
 */
public class CourseDAO {

    /**
     * Creates a new course and assigns generated ID to the course object.
     */
    public void create(Course course) throws SQLException {
        String sql = "INSERT INTO courses (name, code) VALUES (?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getCode());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    course.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Finds all courses ordered by ID.
     */
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, name, code FROM courses ORDER BY id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("code")
                ));
            }
        }
        return courses;
    }

    /**
     * Finds a single course by primary key ID.
     */
    public Course findById(int id) throws SQLException {
        String sql = "SELECT id, name, code FROM courses WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds a course by unique code.
     */
    public Course findByCode(String code) throws SQLException {
        String sql = "SELECT id, name, code FROM courses WHERE code = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing course.
     */
    public void update(Course course) throws SQLException {
        String sql = "UPDATE courses SET name = ?, code = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getCode());
            stmt.setInt(3, course.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a course by primary key ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
