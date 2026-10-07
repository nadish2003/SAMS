package com.sams.dao;

import com.sams.model.Lecturer;
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
 * LecturerDAO – handles CRUD data access and subject assignment for lecturers.
 */
public class LecturerDAO {

    /**
     * Creates a user account with role LECTURER and associates it with a new lecturer record.
     */
    public void createWithUser(Lecturer lecturer, String username, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // 1. Insert into users table
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'LECTURER')";
            int userId = 0;
            try (PreparedStatement uStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                uStmt.setString(1, username);
                uStmt.setString(2, password);
                uStmt.executeUpdate();
                try (ResultSet rs = uStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId == 0) {
                throw new SQLException("Failed to create user account for lecturer.");
            }

            // 2. Insert into lecturers table
            String lectSql = "INSERT INTO lecturers (name, email, phone, user_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement lStmt = conn.prepareStatement(lectSql, Statement.RETURN_GENERATED_KEYS)) {
                lStmt.setString(1, lecturer.getName());
                lStmt.setString(2, lecturer.getEmail());
                lStmt.setString(3, lecturer.getPhone());
                lStmt.setInt(4, userId);
                lStmt.executeUpdate();
                try (ResultSet rs = lStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        lecturer.setId(rs.getInt(1));
                    }
                }
            }

            lecturer.setUserId(userId);
            lecturer.setUsername(username);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Finds all lecturers with their username.
     */
    public List<Lecturer> findAll() throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT l.id, l.name, l.email, l.phone, l.user_id, u.username " +
                     "FROM lecturers l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "ORDER BY l.id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Lecturer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("user_id"),
                    rs.getString("username")
                ));
            }
        }
        return list;
    }

    /**
     * Finds a lecturer by primary key ID.
     */
    public Lecturer findById(int id) throws SQLException {
        String sql = "SELECT l.id, l.name, l.email, l.phone, l.user_id, u.username " +
                     "FROM lecturers l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "WHERE l.id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lecturer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("user_id"),
                        rs.getString("username")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds a lecturer by their foreign key user_id.
     */
    public Lecturer findByUserId(int userId) throws SQLException {
        String sql = "SELECT l.id, l.name, l.email, l.phone, l.user_id, u.username " +
                     "FROM lecturers l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "WHERE l.user_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lecturer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("user_id"),
                        rs.getString("username")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing lecturer's contact details.
     */
    public void update(Lecturer lecturer) throws SQLException {
        String sql = "UPDATE lecturers SET name = ?, email = ?, phone = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lecturer.getName());
            stmt.setString(2, lecturer.getEmail());
            stmt.setString(3, lecturer.getPhone());
            stmt.setInt(4, lecturer.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a lecturer and cascades deletion to the user record.
     */
    public void delete(int id) throws SQLException {
        Lecturer lecturer = findById(id);
        if (lecturer == null) return;

        Connection conn = DBConnection.getConnection();
        // Deleting user will cascade to lecturer according to fk_lecturer_user
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturer.getUserId());
            stmt.executeUpdate();
        }
    }

    // --- Subject Assignment Methods ---

    /**
     * Assigns a subject to a lecturer.
     */
    public void assignSubject(int lecturerId, int subjectId) throws SQLException {
        String sql = "INSERT INTO lecturer_subjects (lecturer_id, subject_id) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE lecturer_id = lecturer_id";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }
    }

    /**
     * Unassigns a subject from a lecturer.
     */
    public void unassignSubject(int lecturerId, int subjectId) throws SQLException {
        String sql = "DELETE FROM lecturer_subjects WHERE lecturer_id = ? AND subject_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all subjects assigned to a lecturer.
     */
    public List<Subject> getAssignedSubjects(int lecturerId) throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.code, s.course_id, c.name AS course_name " +
                     "FROM subjects s " +
                     "JOIN lecturer_subjects ls ON s.id = ls.subject_id " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "WHERE ls.lecturer_id = ? " +
                     "ORDER BY s.id ASC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getInt("course_id"),
                        rs.getString("course_name")
                    ));
                }
            }
        }
        return list;
    }
}
