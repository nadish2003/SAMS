package com.sams.service;

import com.sams.dao.ClassSessionDAO;
import com.sams.model.ClassSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * ClassSessionService – business logic and validation for Class Scheduling.
 */
public class ClassSessionService {

    private final ClassSessionDAO classSessionDAO;

    public ClassSessionService() {
        this.classSessionDAO = new ClassSessionDAO();
    }

    public ClassSessionService(ClassSessionDAO classSessionDAO) {
        this.classSessionDAO = classSessionDAO;
    }

    public List<ClassSession> getAllSessions() throws SQLException {
        return classSessionDAO.findAll();
    }

    public ClassSession getSessionById(int id) throws SQLException {
        return classSessionDAO.findById(id);
    }

    public List<ClassSession> getSessionsForLecturer(int lecturerId) throws SQLException {
        return classSessionDAO.findByLecturerId(lecturerId);
    }

    public void scheduleSession(int courseId, int subjectId, int lecturerId, LocalDate date, LocalTime time) throws SQLException {
        validateScheduleInput(courseId, subjectId, lecturerId, date, time);
        ClassSession session = new ClassSession(courseId, subjectId, lecturerId, date, time);
        classSessionDAO.create(session);
    }

    public void updateSession(int id, int courseId, int subjectId, int lecturerId, LocalDate date, LocalTime time) throws SQLException {
        validateScheduleInput(courseId, subjectId, lecturerId, date, time);
        ClassSession session = new ClassSession(id, courseId, subjectId, lecturerId, date, time);
        classSessionDAO.update(session);
    }

    public void deleteSession(int id) throws SQLException {
        classSessionDAO.delete(id);
    }

    private void validateScheduleInput(int courseId, int subjectId, int lecturerId, LocalDate date, LocalTime time) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Please select a valid course.");
        }
        if (subjectId <= 0) {
            throw new IllegalArgumentException("Please select a valid subject.");
        }
        if (lecturerId <= 0) {
            throw new IllegalArgumentException("Please select a lecturer.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Please select a class date.");
        }
        if (time == null) {
            throw new IllegalArgumentException("Please select or enter a class time.");
        }
    }
}
