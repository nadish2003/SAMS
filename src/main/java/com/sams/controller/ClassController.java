package com.sams.controller;

import com.sams.model.ClassSession;
import com.sams.model.Course;
import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.model.User;
import com.sams.service.ClassSessionService;
import com.sams.service.CourseService;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * ClassController – manages class scheduling.
 * Admins can schedule, update and delete classes.
 * Lecturers can view their assigned class schedule.
 */
public class ClassController implements Initializable {

    @FXML private VBox adminFormPane;
    @FXML private Label tableHeadingLabel;

    @FXML private ComboBox<Course> courseComboBox;
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private ComboBox<Lecturer> lecturerComboBox;
    @FXML private DatePicker sessionDatePicker;
    @FXML private ComboBox<String> timeComboBox;

    @FXML private Button scheduleBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;

    @FXML private TableView<ClassSession> sessionTable;
    @FXML private TableColumn<ClassSession, Integer> idCol;
    @FXML private TableColumn<ClassSession, LocalDate> dateCol;
    @FXML private TableColumn<ClassSession, LocalTime> timeCol;
    @FXML private TableColumn<ClassSession, String> courseCol;
    @FXML private TableColumn<ClassSession, String> subjectCol;
    @FXML private TableColumn<ClassSession, String> lecturerCol;

    @FXML private Label messageLabel;

    private final ClassSessionService sessionService = new ClassSessionService();
    private final CourseService courseService = new CourseService();
    private final LecturerService lecturerService = new LecturerService();

    private final ObservableList<ClassSession> sessionList = FXCollections.observableArrayList();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    private final ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();

    private ClassSession selectedSession = null;
    private Lecturer currentLecturer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Session.getCurrentUser();
        boolean isAdmin = Session.isAdmin();
        boolean isLecturer = Session.isLecturer();

        // Configure columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        lecturerCol.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        sessionTable.setItems(sessionList);

        if (isAdmin) {
            adminFormPane.setVisible(true);
            adminFormPane.setManaged(true);
            tableHeadingLabel.setText("All Scheduled Classes");

            // Setup combo boxes
            courseComboBox.setItems(courseList);
            subjectComboBox.setItems(subjectList);
            lecturerComboBox.setItems(lecturerList);

            timeComboBox.setItems(FXCollections.observableArrayList(
                "08:30", "09:00", "09:30", "10:00", "10:30", "11:00",
                "11:30", "12:00", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
            ));

            // Filtering subjects based on selected course
            courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadSubjectsForCourse(newVal.getId());
                } else {
                    subjectList.clear();
                }
            });

            // Table selection listener
            sessionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectSession(newVal);
                }
            });

            loadDropdownData();
            loadSessions();
        } else if (isLecturer && user != null) {
            // Lecturer view: hide admin form
            adminFormPane.setVisible(false);
            adminFormPane.setManaged(false);
            tableHeadingLabel.setText("Your Assigned Teaching Schedule");

            try {
                currentLecturer = lecturerService.getLecturerByUserId(user.getId());
            } catch (SQLException e) {
                showError("Error identifying lecturer profile: " + e.getMessage());
            }
            loadSessions();
        }
    }

    private void loadDropdownData() {
        try {
            courseList.clear();
            courseList.addAll(courseService.getAllCourses());

            lecturerList.clear();
            lecturerList.addAll(lecturerService.getAllLecturers());
        } catch (SQLException e) {
            showError("Error loading courses/lecturers: " + e.getMessage());
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

    private void loadSessions() {
        try {
            sessionList.clear();
            if (Session.isAdmin()) {
                sessionList.addAll(sessionService.getAllSessions());
            } else if (currentLecturer != null) {
                sessionList.addAll(sessionService.getSessionsForLecturer(currentLecturer.getId()));
            }
        } catch (SQLException e) {
            showError("Error loading class schedules: " + e.getMessage());
        }
    }

    private void selectSession(ClassSession session) {
        selectedSession = session;

        // Select Course
        for (Course c : courseList) {
            if (c.getId() == session.getCourseId()) {
                courseComboBox.setValue(c);
                loadSubjectsForCourse(c.getId());
                break;
            }
        }

        // Select Subject
        for (Subject s : subjectList) {
            if (s.getId() == session.getSubjectId()) {
                subjectComboBox.setValue(s);
                break;
            }
        }

        // Select Lecturer
        for (Lecturer l : lecturerList) {
            if (l.getId() == session.getLecturerId()) {
                lecturerComboBox.setValue(l);
                break;
            }
        }

        sessionDatePicker.setValue(session.getSessionDate());
        timeComboBox.setValue(session.getSessionTime().toString());
    }

    @FXML
    private void handleScheduleClass(ActionEvent event) {
        Course course = courseComboBox.getValue();
        Subject subject = subjectComboBox.getValue();
        Lecturer lecturer = lecturerComboBox.getValue();
        LocalDate date = sessionDatePicker.getValue();
        String timeStr = timeComboBox.getValue();

        if (course == null || subject == null || lecturer == null || date == null || timeStr == null || timeStr.trim().isEmpty()) {
            showError("Please fill in all required scheduling fields.");
            return;
        }

        try {
            LocalTime time = parseTime(timeStr.trim());
            sessionService.scheduleSession(course.getId(), subject.getId(), lecturer.getId(), date, time);
            showSuccess("Class scheduled successfully.");
            clearForm();
            loadSessions();
        } catch (DateTimeParseException e) {
            showError("Invalid time format. Use HH:mm (e.g. 09:00).");
        } catch (SQLException e) {
            showError("Database error scheduling class: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateSession(ActionEvent event) {
        if (selectedSession == null) {
            showError("Please select a session from the table to update.");
            return;
        }

        Course course = courseComboBox.getValue();
        Subject subject = subjectComboBox.getValue();
        Lecturer lecturer = lecturerComboBox.getValue();
        LocalDate date = sessionDatePicker.getValue();
        String timeStr = timeComboBox.getValue();

        if (course == null || subject == null || lecturer == null || date == null || timeStr == null || timeStr.trim().isEmpty()) {
            showError("Please fill in all required scheduling fields.");
            return;
        }

        try {
            LocalTime time = parseTime(timeStr.trim());
            sessionService.updateSession(selectedSession.getId(), course.getId(), subject.getId(), lecturer.getId(), date, time);
            showSuccess("Class schedule updated successfully.");
            clearForm();
            loadSessions();
        } catch (DateTimeParseException e) {
            showError("Invalid time format. Use HH:mm (e.g. 09:00).");
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSession(ActionEvent event) {
        if (selectedSession == null) {
            showError("Please select a session to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete scheduled session on " + selectedSession.getSessionDate() + " for " + selectedSession.getSubjectName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                sessionService.deleteSession(selectedSession.getId());
                showSuccess("Session deleted successfully.");
                clearForm();
                loadSessions();
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key")) {
                    showError("Cannot delete session: Attendance records have already been marked for this class.");
                } else {
                    showError("Cannot delete session: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadSessions();
        showSuccess("Schedule refreshed.");
    }

    private void clearForm() {
        selectedSession = null;
        courseComboBox.setValue(null);
        subjectComboBox.setValue(null);
        lecturerComboBox.setValue(null);
        sessionDatePicker.setValue(null);
        timeComboBox.setValue(null);
        sessionTable.getSelectionModel().clearSelection();
    }

    private LocalTime parseTime(String str) {
        if (str.length() == 5) {
            return LocalTime.parse(str);
        }
        if (str.length() == 8) {
            return LocalTime.parse(str);
        }
        return LocalTime.parse(str);
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
