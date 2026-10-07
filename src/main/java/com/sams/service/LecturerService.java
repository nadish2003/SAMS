package com.sams.service;

import com.sams.dao.LecturerDAO;
import com.sams.model.Lecturer;
import com.sams.model.Subject;

import java.sql.SQLException;
import java.util.List;

/**
 * LecturerService – business logic and validation for Lecturer management and subject assignments.
 */
public class LecturerService {

    private final LecturerDAO lecturerDAO;

    public LecturerService() {
        this.lecturerDAO = new LecturerDAO();
    }

    public LecturerService(LecturerDAO lecturerDAO) {
        this.lecturerDAO = lecturerDAO;
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
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer name cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        Lecturer lecturer = new Lecturer(
            name.trim(),
            email != null ? email.trim() : "",
            phone != null ? phone.trim() : "",
            0
        );
        lecturerDAO.createWithUser(lecturer, username.trim(), password);
    }

    public void updateLecturer(int id, String name, String email, String phone) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecturer name cannot be empty.");
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
}
