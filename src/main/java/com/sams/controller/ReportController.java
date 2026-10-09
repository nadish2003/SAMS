package com.sams.controller;

import com.sams.model.*;
import com.sams.service.CourseService;
import com.sams.service.LecturerService;
import com.sams.service.ReportService;
import com.sams.service.StudentService;
import com.sams.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ReportController – handles attendance reports generation and filtering.
 * Filterable by student, course, subject, and date range.
 */
public class ReportController implements Initializable {

    @FXML private ComboBox<Student> studentComboBox;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Label totalRecordsLabel;
    @FXML private Label presentSummaryLabel;
    @FXML private Label absentSummaryLabel;
    @FXML private Label lateSummaryLabel;

    @FXML private TableView<AttendanceReportItem> reportTable;
    @FXML private TableColumn<AttendanceReportItem, String> regNoCol;
    @FXML private TableColumn<AttendanceReportItem, String> nameCol;
    @FXML private TableColumn<AttendanceReportItem, String> courseCol;
    @FXML private TableColumn<AttendanceReportItem, String> subjectCol;
    @FXML private TableColumn<AttendanceReportItem, LocalDate> dateCol;
    @FXML private TableColumn<AttendanceReportItem, LocalTime> timeCol;
    @FXML private TableColumn<AttendanceReportItem, String> lecturerCol;
    @FXML private TableColumn<AttendanceReportItem, String> statusCol;

    @FXML private Label messageLabel;

    private final ReportService reportService = new ReportService();
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();
    private final LecturerService lecturerService = new LecturerService();

    private final ObservableList<AttendanceReportItem> reportList = FXCollections.observableArrayList();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<Subject> subjectList = FXCollections.observableArrayList();

    private Lecturer currentLecturer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Session.getCurrentUser();
        if (user == null) {
            showError("User session not found.");
            return;
        }

        // Configure table columns
        regNoCol.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
        lecturerCol.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        reportTable.setItems(reportList);

        // Bind dropdown lists
        studentComboBox.setItems(studentList);
        courseComboBox.setItems(courseList);
        subjectComboBox.setItems(subjectList);

        // If course changes, filter subject dropdown
        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSubjectsForCourse(newVal.getId());
            } else {
                loadAllSubjects();
            }
        });

        // If lecturer, scope to their profile
        if (Session.isLecturer()) {
            try {
                currentLecturer = lecturerService.getLecturerByUserId(user.getId());
            } catch (SQLException e) {
                showError("Error identifying lecturer profile: " + e.getMessage());
            }
        }

        loadFilterData();
        handleGenerateReport(null);
    }

    private void loadFilterData() {
        try {
            studentList.clear();
            studentList.addAll(studentService.getAllStudents());

            courseList.clear();
            courseList.addAll(courseService.getAllCourses());

            loadAllSubjects();
        } catch (SQLException e) {
            showError("Error loading filter options: " + e.getMessage());
        }
    }

    private void loadAllSubjects() {
        try {
            subjectList.clear();
            subjectList.addAll(courseService.getAllSubjects());
        } catch (SQLException e) {
            showError("Error loading subjects: " + e.getMessage());
        }
    }

    private void loadSubjectsForCourse(int courseId) {
        try {
            subjectList.clear();
            subjectList.addAll(courseService.getSubjectsByCourse(courseId));
        } catch (SQLException e) {
            showError("Error loading subjects: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {
        Student selectedStudent = studentComboBox.getValue();
        Course selectedCourse = courseComboBox.getValue();
        Subject selectedSubject = subjectComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        Integer studentId = selectedStudent != null ? selectedStudent.getId() : null;
        Integer courseId = selectedCourse != null ? selectedCourse.getId() : null;
        Integer subjectId = selectedSubject != null ? selectedSubject.getId() : null;
        Integer lecturerId = null;

        if (Session.isLecturer()) {
            if (currentLecturer == null) {
                showError("Access Denied: Lecturer profile required.");
                return;
            }
            lecturerId = currentLecturer.getId();
        }

        try {
            List<AttendanceReportItem> results = reportService.generateAttendanceReport(
                studentId, courseId, subjectId, fromDate, toDate, lecturerId
            );
            reportList.clear();
            reportList.addAll(results);
            updateSummaryStatistics();
            showSuccess("Report generated: " + results.size() + " records found.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error generating report: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetFilters(ActionEvent event) {
        studentComboBox.setValue(null);
        courseComboBox.setValue(null);
        subjectComboBox.setValue(null);
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        loadAllSubjects();
        handleGenerateReport(null);
    }

    private void updateSummaryStatistics() {
        int total = reportList.size();
        int present = 0;
        int absent = 0;
        int late = 0;

        for (AttendanceReportItem item : reportList) {
            String s = item.getStatus();
            if ("PRESENT".equalsIgnoreCase(s)) {
                present++;
            } else if ("LATE".equalsIgnoreCase(s)) {
                late++;
            } else {
                absent++;
            }
        }

        double presentPct = total > 0 ? (present * 100.0 / total) : 0.0;
        double absentPct = total > 0 ? (absent * 100.0 / total) : 0.0;
        double latePct = total > 0 ? (late * 100.0 / total) : 0.0;

        totalRecordsLabel.setText("Total Records: " + total);
        presentSummaryLabel.setText(String.format("Present: %d (%.1f%%)", present, presentPct));
        absentSummaryLabel.setText(String.format("Absent: %d (%.1f%%)", absent, absentPct));
        lateSummaryLabel.setText(String.format("Late: %d (%.1f%%)", late, latePct));
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 750, 500);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Dashboard");
            stage.show();
        } catch (IOException e) {
            showError("Error returning to dashboard: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        messageLabel.setText(msg);
    }
}
