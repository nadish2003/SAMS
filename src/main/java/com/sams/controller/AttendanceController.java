package com.sams.controller;

import com.sams.model.Attendance;
import com.sams.model.ClassSession;
import com.sams.model.Lecturer;
import com.sams.model.User;
import com.sams.service.AttendanceService;
import com.sams.service.ClassSessionService;
import com.sams.service.LecturerService;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * AttendanceController – handles attendance marking for lecturers.
 * Lecturers can select their class session, mark Present/Absent/Late per student,
 * and save/update attendance records in MySQL.
 */
public class AttendanceController implements Initializable {

    @FXML private ComboBox<ClassSession> sessionComboBox;
    @FXML private Label sessionDetailsLabel;

    @FXML private Label totalCountLabel;
    @FXML private Label presentCountLabel;
    @FXML private Label absentCountLabel;
    @FXML private Label lateCountLabel;
    @FXML private Button saveAttendanceBtn;

    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> regNoCol;
    @FXML private TableColumn<Attendance, String> nameCol;
    @FXML private TableColumn<Attendance, String> statusCol;
    @FXML private TableColumn<Attendance, Timestamp> markedAtCol;

    @FXML private Label messageLabel;

    private final AttendanceService attendanceService = new AttendanceService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final LecturerService lecturerService = new LecturerService();

    private final ObservableList<ClassSession> sessionList = FXCollections.observableArrayList();
    private final ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    private Lecturer currentLecturer = null;
    private ClassSession currentSession = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Session.getCurrentUser();

        // Enforce lecturer-only access
        if (!Session.isLecturer() || user == null) {
            showError("Access Denied: Attendance marking is reserved for Lecturers.");
            if (saveAttendanceBtn != null) saveAttendanceBtn.setDisable(true);
            return;
        }

        // Configure table columns
        regNoCol.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        markedAtCol.setCellValueFactory(new PropertyValueFactory<>("markedAt"));

        // Make status column an editable ComboBox (PRESENT, ABSENT, LATE)
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("PRESENT", "ABSENT", "LATE"));
        statusCol.setOnEditCommit(event -> {
            Attendance att = event.getRowValue();
            if (att != null && event.getNewValue() != null) {
                att.setStatus(event.getNewValue());
                updateCounts();
            }
        });

        attendanceTable.setItems(attendanceList);
        sessionComboBox.setItems(sessionList);

        // Selection listener on session combo box
        sessionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                onSessionSelected(newVal);
            }
        });

        loadLecturerSessions(user);
    }

    private void loadLecturerSessions(User user) {
        try {
            currentLecturer = lecturerService.getLecturerByUserId(user.getId());
            if (currentLecturer == null) {
                showError("No lecturer profile linked to this user account.");
                return;
            }

            sessionList.clear();
            sessionList.addAll(sessionService.getSessionsForLecturer(currentLecturer.getId()));
            if (sessionList.isEmpty()) {
                sessionDetailsLabel.setText("No scheduled classes found for your profile.");
            }
        } catch (SQLException e) {
            showError("Error loading assigned sessions: " + e.getMessage());
        }
    }

    private void onSessionSelected(ClassSession session) {
        currentSession = session;
        sessionDetailsLabel.setText("Course: " + session.getCourseName() +
                                    " | Subject: " + session.getSubjectName() +
                                    " | Date: " + session.getSessionDate() + " " + session.getSessionTime());
        loadAttendanceForSession();
    }

    private void loadAttendanceForSession() {
        if (currentSession == null) {
            attendanceList.clear();
            return;
        }

        try {
            attendanceList.clear();
            attendanceList.addAll(attendanceService.getAttendanceForSession(currentSession.getId(), currentSession.getCourseId()));
            updateCounts();
            showSuccess("Loaded " + attendanceList.size() + " students for this class.");
        } catch (SQLException e) {
            showError("Error loading attendance list: " + e.getMessage());
        }
    }

    private void updateCounts() {
        int total = attendanceList.size();
        int present = 0;
        int absent = 0;
        int late = 0;

        for (Attendance a : attendanceList) {
            String s = a.getStatus();
            if ("PRESENT".equalsIgnoreCase(s)) {
                present++;
            } else if ("LATE".equalsIgnoreCase(s)) {
                late++;
            } else {
                absent++;
            }
        }

        totalCountLabel.setText("Total Students: " + total);
        presentCountLabel.setText("Present: " + present);
        absentCountLabel.setText("Absent: " + absent);
        lateCountLabel.setText("Late: " + late);
    }

    @FXML
    private void handleMarkAllPresent(ActionEvent event) {
        if (attendanceList.isEmpty()) return;
        for (Attendance a : attendanceList) {
            a.setStatus("PRESENT");
        }
        attendanceTable.refresh();
        updateCounts();
        showSuccess("All students marked PRESENT.");
    }

    @FXML
    private void handleMarkAllAbsent(ActionEvent event) {
        if (attendanceList.isEmpty()) return;
        for (Attendance a : attendanceList) {
            a.setStatus("ABSENT");
        }
        attendanceTable.refresh();
        updateCounts();
        showSuccess("All students marked ABSENT.");
    }

    @FXML
    private void handleSaveAttendance(ActionEvent event) {
        if (currentSession == null) {
            showError("Please select a class session first.");
            return;
        }
        if (currentLecturer == null || currentSession.getLecturerId() != currentLecturer.getId()) {
            showError("Access Denied: You can only record attendance for your own scheduled classes.");
            return;
        }
        if (attendanceList.isEmpty()) {
            showError("No student attendance records to save.");
            return;
        }

        try {
            attendanceService.saveAllAttendance(new ArrayList<>(attendanceList));
            showSuccess("Attendance successfully saved for " + attendanceList.size() + " students.");
            loadAttendanceForSession(); // Refresh timestamps from DB
        } catch (SQLException e) {
            showError("Database error saving attendance: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
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
