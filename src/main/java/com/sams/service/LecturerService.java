package com.sams.service;

import com.sams.dao.LecturerDAO;
import com.sams.dao.UserDAO;
import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * LecturerService – business logic and validation for Lecturer management and subject assignments.
 */
public class LecturerService {

    private final LecturerDAO lecturerDAO;
    private final UserDAO userDAO;

    public LecturerService() {
        this.lecturerDAO = new LecturerDAO();
        this.userDAO = new UserDAO();
    }

    public LecturerService(LecturerDAO lecturerDAO, UserDAO userDAO) {
        this.lecturerDAO = lecturerDAO;
        this.userDAO = userDAO;
    }

    public List<Lecturer> getAllLecturers() throws SQLException {
        return lecturerDAO.findAll();
    }

    public Lecturer getLecturerById(int id) throws SQLException {
        return lecturerDAO.findById(id);
    }

    public Lecturer getLecturerByUserId(int userId) throws SQLException {
        return lecturerDAO.findByUserId(userId);
    }

    public void addLecturer(String name, String email, String phone, String username, String password) throws SQLException {
        validateLecturerInput(name, username, password, email, phone);

        // Check duplicate username
        User existingUser = userDAO.findByUsername(username.trim());
        if (existingUser != null) {
            throw new IllegalArgumentException("Username '" + username.trim() + "' is already taken. Please choose another.");
        }

        Lecturer lecturer = new Lecturer(
            name.trim(),
            email != null ? email.trim() : "",
            phone != null ? phone.trim() : "",
            0
        );
        lecturerDAO.createWithUser(lecturer, username.trim(), password.trim());
    }

    public void updateLecturer(int id, String name, String email, String phone) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer name cannot be empty.");
        }
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Invalid email format (e.g. lecturer@sams.lk).");
            }
        }
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.trim().matches("^[0-9+\\-\\s]{7,15}$")) {
                throw new IllegalArgumentException("Invalid phone number format.");
            }
        }

        Lecturer lecturer = new Lecturer(
            id,
            name.trim(),
            email != null ? email.trim() : "",
            phone != null ? phone.trim() : "",
            0
        );
        lecturerDAO.update(lecturer);
    }

    public void deleteLecturer(int id) throws SQLException {
        lecturerDAO.delete(id);
    }

    public List<Subject> getAssignedSubjects(int lecturerId) throws SQLException {
        return lecturerDAO.getAssignedSubjects(lecturerId);
    }

    public void assignSubject(int lecturerId, int subjectId) throws SQLException {
        if (lecturerId <= 0 || subjectId <= 0) {
            throw new IllegalArgumentException("Invalid lecturer or subject selected.");
        }
        lecturerDAO.assignSubject(lecturerId, subjectId);
    }

    public void unassignSubject(int lecturerId, int subjectId) throws SQLException {
        if (lecturerId <= 0 || subjectId <= 0) {
            throw new IllegalArgumentException("Invalid lecturer or subject selected.");
        }
        lecturerDAO.unassignSubject(lecturerId, subjectId);
    }

    private void validateLecturerInput(String name, String username, String password, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer name cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (password.trim().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Invalid email format (e.g. lecturer@sams.lk).");
            }
        }
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.trim().matches("^[0-9+\\-\\s]{7,15}$")) {
                throw new IllegalArgumentException("Invalid phone number format.");
            }
        }
    }
}
