package com.sams.dao;

import com.sams.model.Student;
import com.sams.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO – handles CRUD data access operations for the Student entity.
 */
public class StudentDAO {

    /**
     * Creates a new student and sets the generated ID.
     */
    public void create(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, registration_number, course_id, email, phone) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getRegistrationNumber());
            stmt.setInt(3, student.getCourseId());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Finds all students along with their course name and code.
     */
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.registration_number, s.course_id, s.email, s.phone, " +
                     "c.name AS course_name, c.code AS course_code " +
                     "FROM students s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "ORDER BY s.id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("registration_number"),
                    rs.getInt("course_id"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("course_name"),
                    rs.getString("course_code")
                ));
            }
        }
        return list;
    }

    /**
     * Finds a student by primary key ID.
     */
    public Student findById(int id) throws SQLException {
        String sql = "SELECT s.id, s.name, s.registration_number, s.course_id, s.email, s.phone, " +
                     "c.name AS course_name, c.code AS course_code " +
                     "FROM students s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("registration_number"),
                        rs.getInt("course_id"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course_name"),
                        rs.getString("course_code")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds a student by unique registration number.
     */
    public Student findByRegistrationNumber(String regNo) throws SQLException {
        String sql = "SELECT s.id, s.name, s.registration_number, s.course_id, s.email, s.phone, " +
                     "c.name AS course_name, c.code AS course_code " +
                     "FROM students s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.registration_number = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("registration_number"),
                        rs.getInt("course_id"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course_name"),
                        rs.getString("course_code")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds all students belonging to a specific course.
     */
    public List<Student> findByCourseId(int courseId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.registration_number, s.course_id, s.email, s.phone, " +
                     "c.name AS course_name, c.code AS course_code " +
                     "FROM students s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.course_id = ? " +
                     "ORDER BY s.registration_number ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("registration_number"),
                        rs.getInt("course_id"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course_name"),
                        rs.getString("course_code")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Updates an existing student record.
     */
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, registration_number = ?, course_id = ?, email = ?, phone = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getRegistrationNumber());
            stmt.setInt(3, student.getCourseId());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setInt(6, student.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a student by ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
