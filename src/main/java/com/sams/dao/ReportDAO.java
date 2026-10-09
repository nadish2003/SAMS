package com.sams.dao;

import com.sams.model.AttendanceReportItem;
import com.sams.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportDAO – executes filtered attendance reporting queries using PreparedStatement.
 */
public class ReportDAO {

    /**
     * Retrieves filtered attendance records based on optional filters.
     *
     * @param studentId   optional student filter
     * @param courseId    optional course filter
     * @param subjectId   optional subject filter
     * @param fromDate    optional start date filter
     * @param toDate      optional end date filter
     * @param lecturerId  optional lecturer filter (for lecturer role scoping)
     * @return list of AttendanceReportItem records
     * @throws SQLException on database query failure
     */
    public List<AttendanceReportItem> getAttendanceReport(Integer studentId,
                                                         Integer courseId,
                                                         Integer subjectId,
                                                         LocalDate fromDate,
                                                         LocalDate toDate,
                                                         Integer lecturerId) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT s.name AS student_name, s.registration_number AS student_reg_no, " +
            "c.name AS course_name, sub.name AS subject_name, sub.code AS subject_code, " +
            "l.name AS lecturer_name, cs.session_date, cs.session_time, a.status, a.marked_at " +
            "FROM attendance a " +
            "JOIN students s ON a.student_id = s.id " +
            "JOIN class_sessions cs ON a.class_session_id = cs.id " +
            "JOIN courses c ON cs.course_id = c.id " +
            "JOIN subjects sub ON cs.subject_id = sub.id " +
            "JOIN lecturers l ON cs.lecturer_id = l.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (studentId != null && studentId > 0) {
            sql.append("AND s.id = ? ");
            params.add(studentId);
        }
        if (courseId != null && courseId > 0) {
            sql.append("AND c.id = ? ");
            params.add(courseId);
        }
        if (subjectId != null && subjectId > 0) {
            sql.append("AND sub.id = ? ");
            params.add(subjectId);
        }
        if (fromDate != null) {
            sql.append("AND cs.session_date >= ? ");
            params.add(Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append("AND cs.session_date <= ? ");
            params.add(Date.valueOf(toDate));
        }
        if (lecturerId != null && lecturerId > 0) {
            sql.append("AND cs.lecturer_id = ? ");
            params.add(lecturerId);
        }

        sql.append("ORDER BY cs.session_date DESC, cs.session_time DESC, s.registration_number ASC");

        List<AttendanceReportItem> results = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) p);
                } else if (p instanceof Date) {
                    stmt.setDate(i + 1, (Date) p);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new AttendanceReportItem(
                        rs.getString("student_name"),
                        rs.getString("student_reg_no"),
                        rs.getString("course_name"),
                        rs.getString("subject_name"),
                        rs.getString("subject_code"),
                        rs.getString("lecturer_name"),
                        rs.getDate("session_date").toLocalDate(),
                        rs.getTime("session_time").toLocalTime(),
                        rs.getString("status"),
                        rs.getTimestamp("marked_at")
                    ));
                }
            }
        }
        return results;
    }
}
