package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.dao.SubjectDAO;
import com.sams.model.Course;
import com.sams.model.Subject;

import java.sql.SQLException;
import java.util.List;

/**
 * CourseService – provides business logic and validation for Course and Subject management.
 */
public class CourseService {

    private final CourseDAO courseDAO;
    private final SubjectDAO subjectDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.subjectDAO = new SubjectDAO();
    }

    public CourseService(CourseDAO courseDAO, SubjectDAO subjectDAO) {
        this.courseDAO = courseDAO;
        this.subjectDAO = subjectDAO;
    }

    // --- Course Operations ---

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }

    public Course getCourseById(int id) throws SQLException {
        return courseDAO.findById(id);
    }

    public void addCourse(String name, String code) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }

        // Duplicate code check
        Course existing = courseDAO.findByCode(code.trim().toUpperCase());
        if (existing != null) {
            throw new IllegalArgumentException("Course code '" + code.trim().toUpperCase() + "' already exists.");
        }

        Course course = new Course(name.trim(), code.trim().toUpperCase());
        courseDAO.create(course);
    }

    public void updateCourse(int id, String name, String code) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }

        // Check if code is taken by another course
        Course existing = courseDAO.findByCode(code.trim().toUpperCase());
        if (existing != null && existing.getId() != id) {
            throw new IllegalArgumentException("Course code '" + code.trim().toUpperCase() + "' is already used by another course.");
        }

        Course course = new Course(id, name.trim(), code.trim().toUpperCase());
        courseDAO.update(course);
    }

    public void deleteCourse(int id) throws SQLException {
        courseDAO.delete(id);
    }

    // --- Subject Operations ---

    public List<Subject> getAllSubjects() throws SQLException {
        return subjectDAO.findAll();
    }

    public List<Subject> getSubjectsByCourse(int courseId) throws SQLException {
        return subjectDAO.findByCourseId(courseId);
    }

    public void addSubject(String name, String code, int courseId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code cannot be empty.");
        }
        if (courseId <= 0) {
            throw new IllegalArgumentException("Please select a valid course.");
        }

        // Check existing subjects for duplicate code
        List<Subject> all = subjectDAO.findAll();
        for (Subject s : all) {
            if (s.getCode().equalsIgnoreCase(code.trim())) {
                throw new IllegalArgumentException("Subject code '" + code.trim().toUpperCase() + "' already exists.");
            }
        }

        Subject subject = new Subject(name.trim(), code.trim().toUpperCase(), courseId);
        subjectDAO.create(subject);
    }

    public void updateSubject(int id, String name, String code, int courseId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code cannot be empty.");
        }
        if (courseId <= 0) {
            throw new IllegalArgumentException("Please select a valid course.");
        }

        List<Subject> all = subjectDAO.findAll();
        for (Subject s : all) {
            if (s.getCode().equalsIgnoreCase(code.trim()) && s.getId() != id) {
                throw new IllegalArgumentException("Subject code '" + code.trim().toUpperCase() + "' is already used by another subject.");
            }
        }

        Subject subject = new Subject(id, name.trim(), code.trim().toUpperCase(), courseId);
        subjectDAO.update(subject);
    }

    public void deleteSubject(int id) throws SQLException {
        subjectDAO.delete(id);
    }
}
