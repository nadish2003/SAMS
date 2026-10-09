package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * StudentService – business logic and validation for Student management.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        return studentDAO.findByCourseId(courseId);
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public void addStudent(String name, String regNo, int courseId, String email, String phone) throws SQLException {
        validateStudentInput(name, regNo, courseId, email, phone);

        // Duplicate registration number check
        Student existing = studentDAO.findByRegistrationNumber(regNo.trim().toUpperCase());
        if (existing != null) {
            throw new IllegalArgumentException("A student with registration number '" + regNo.trim().toUpperCase() + "' is already registered.");
        }

        Student student = new Student(
            name.trim(),
            regNo.trim().toUpperCase(),
            courseId,
            email != null ? email.trim() : "",
            phone != null ? phone.trim() : ""
        );
        studentDAO.create(student);
    }

    public void updateStudent(int id, String name, String regNo, int courseId, String email, String phone) throws SQLException {
        validateStudentInput(name, regNo, courseId, email, phone);

        // Check if new registration number is taken by another student
        Student existing = studentDAO.findByRegistrationNumber(regNo.trim().toUpperCase());
        if (existing != null && existing.getId() != id) {
            throw new IllegalArgumentException("Registration number '" + regNo.trim().toUpperCase() + "' is already used by another student.");
        }

        Student student = new Student(
            id,
            name.trim(),
            regNo.trim().toUpperCase(),
            courseId,
            email != null ? email.trim() : "",
            phone != null ? phone.trim() : ""
        );
        studentDAO.update(student);
    }

    public void deleteStudent(int id) throws SQLException {
        studentDAO.delete(id);
    }

    private void validateStudentInput(String name, String regNo, int courseId, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        if (courseId <= 0) {
            throw new IllegalArgumentException("Please select an enrolled course.");
        }
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Invalid email format (e.g. student@example.com).");
            }
        }
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.trim().matches("^[0-9+\\-\\s]{7,15}$")) {
                throw new IllegalArgumentException("Invalid phone number format.");
            }
        }
    }
}
