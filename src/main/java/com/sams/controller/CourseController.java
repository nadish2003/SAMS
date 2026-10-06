package com.sams.controller;

import com.sams.model.Course;
import com.sams.model.Subject;
import com.sams.service.CourseService;
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
import java.util.ResourceBundle;

/**
 * CourseController – manages courses and their associated subjects.
 * Accessible only to Admin users.
 */
public class CourseController implements Initializable {

    // --- Course UI controls ---
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseIdCol;
    @FXML private TableColumn<Course, String> courseCodeCol;
    @FXML private TableColumn<Course, String> courseNameCol;

    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private Button saveCourseBtn;
    @FXML private Button updateCourseBtn;
    @FXML private Button deleteCourseBtn;
    @FXML private Button clearCourseBtn;

    // --- Subject UI controls ---
    @FXML private Label selectedCourseLabel;
    @FXML private TableView<Subject> subjectTable;
    @FXML private TableColumn<Subject, Integer> subjectIdCol;
    @FXML private TableColumn<Subject, String> subjectCodeCol;
    @FXML private TableColumn<Subject, String> subjectNameCol;

    @FXML private TextField subjectCodeField;
    @FXML private TextField subjectNameField;
    @FXML private Button addSubjectBtn;
    @FXML private Button deleteSubjectBtn;

    // --- Feedback ---
    @FXML private Label messageLabel;

    private final CourseService courseService = new CourseService();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    private Course selectedCourse = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Enforce admin-only access
        if (!Session.isAdmin()) {
            showError("Access Denied: Admin privileges required.");
            disableForm();
            return;
        }

        // Configure course table columns
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseTable.setItems(courseList);

        // Configure subject table columns
        subjectIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        subjectCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        subjectNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        subjectTable.setItems(subjectList);

        // Selection listener for courses table
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectCourse(newVal);
            }
        });

        loadCourses();
    }

    private void disableForm() {
        if (saveCourseBtn != null) saveCourseBtn.setDisable(true);
        if (updateCourseBtn != null) updateCourseBtn.setDisable(true);
        if (deleteCourseBtn != null) deleteCourseBtn.setDisable(true);
        if (addSubjectBtn != null) addSubjectBtn.setDisable(true);
        if (deleteSubjectBtn != null) deleteSubjectBtn.setDisable(true);
    }

    private void loadCourses() {
        try {
            courseList.clear();
            courseList.addAll(courseService.getAllCourses());
        } catch (SQLException e) {
            showError("Database error loading courses: " + e.getMessage());
        }
    }

    private void selectCourse(Course course) {
        selectedCourse = course;
        courseCodeField.setText(course.getCode());
        courseNameField.setText(course.getName());
        selectedCourseLabel.setText("Subjects for: " + course.getCode() + " - " + course.getName());
        loadSubjectsForSelectedCourse();
    }

    private void loadSubjectsForSelectedCourse() {
        if (selectedCourse == null) {
            subjectList.clear();
            return;
        }
        try {
            subjectList.clear();
            subjectList.addAll(courseService.getSubjectsByCourse(selectedCourse.getId()));
        } catch (SQLException e) {
            showError("Error loading subjects: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveCourse(ActionEvent event) {
        String code = courseCodeField.getText();
        String name = courseNameField.getText();

        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            showError("Please enter both course code and name.");
            return;
        }

        try {
            courseService.addCourse(name.trim(), code.trim());
            showSuccess("Course added successfully.");
            clearCourseForm();
            loadCourses();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateCourse(ActionEvent event) {
        if (selectedCourse == null) {
            showError("Please select a course from the table to update.");
            return;
        }
        String code = courseCodeField.getText();
        String name = courseNameField.getText();

        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            showError("Please enter both course code and name.");
            return;
        }

        try {
            courseService.updateCourse(selectedCourse.getId(), name.trim(), code.trim());
            showSuccess("Course updated successfully.");
            clearCourseForm();
            loadCourses();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse(ActionEvent event) {
        if (selectedCourse == null) {
            showError("Please select a course to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete course '" + selectedCourse.getName() + "' and its associated subjects?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                courseService.deleteCourse(selectedCourse.getId());
                showSuccess("Course deleted successfully.");
                clearCourseForm();
                loadCourses();
            } catch (SQLException e) {
                showError("Cannot delete course: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearCourse(ActionEvent event) {
        clearCourseForm();
    }

    private void clearCourseForm() {
        selectedCourse = null;
        courseCodeField.clear();
        courseNameField.clear();
        courseTable.getSelectionModel().clearSelection();
        selectedCourseLabel.setText("Select a course to view and manage subjects");
        subjectList.clear();
        subjectCodeField.clear();
        subjectNameField.clear();
    }

    // --- Subject Handlers ---

    @FXML
    private void handleAddSubject(ActionEvent event) {
        if (selectedCourse == null) {
            showError("Please select a course before adding a subject.");
            return;
        }
        String code = subjectCodeField.getText();
        String name = subjectNameField.getText();

        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            showError("Please enter both subject code and name.");
            return;
        }

        try {
            courseService.addSubject(name.trim(), code.trim(), selectedCourse.getId());
            showSuccess("Subject added successfully.");
            subjectCodeField.clear();
            subjectNameField.clear();
            loadSubjectsForSelectedCourse();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSubject(ActionEvent event) {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            showError("Please select a subject to delete.");
            return;
        }

        try {
            courseService.deleteSubject(selectedSubject.getId());
            showSuccess("Subject deleted successfully.");
            loadSubjectsForSelectedCourse();
        } catch (SQLException e) {
            showError("Database error deleting subject: " + e.getMessage());
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
