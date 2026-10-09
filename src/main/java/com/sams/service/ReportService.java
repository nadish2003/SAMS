package com.sams.service;

import com.sams.dao.ReportDAO;
import com.sams.model.AttendanceReportItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * ReportService – business logic and validation for attendance reporting.
 */
public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public List<AttendanceReportItem> generateAttendanceReport(Integer studentId,
                                                              Integer courseId,
                                                              Integer subjectId,
                                                              LocalDate fromDate,
                                                              LocalDate toDate,
                                                              Integer lecturerId) throws SQLException {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("'From Date' cannot be after 'To Date'.");
        }

        return reportDAO.getAttendanceReport(studentId, courseId, subjectId, fromDate, toDate, lecturerId);
    }
}
