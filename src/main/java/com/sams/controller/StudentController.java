package com.sams.controller;

import com.sams.model.Course;
import com.sams.model.Student;
import com.sams.service.CourseService;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * StudentController – manages students and their course enrollment.
 * Accessible only to Admin users.
 */
public class StudentController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField regNoField;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private Button addStudentBtn;
    @FXML private Button updateStudentBtn;
    @FXML private Button deleteStudentBtn;
    @FXML private Button clearBtn;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> idCol;
    @FXML private TableColumn<Student, String> regNoCol;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> courseCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> phoneCol;

    @FXML private Label messageLabel;

    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private Student selectedStudent = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Session.isAdmin()) {
            showError("Access Denied: Admin privileges required.");
            if (addStudentBtn != null) addStudentBtn.setDisable(true);
            if (updateStudentBtn != null) updateStudentBtn.setDisable(true);
            if (deleteStudentBtn != null) deleteStudentBtn.setDisable(true);
            return;
        }

        // Configure table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        regNoCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        studentTable.setItems(studentList);

        // Course combobox
        courseComboBox.setItems(courseList);

        // Table selection listener
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectStudent(newVal);
            }
        });

        loadCourses();
        loadStudents();
    }

    private void loadCourses() {
        try {
            courseList.clear();
            courseList.addAll(courseService.getAllCourses());
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    private void loadStudents() {
        try {
            studentList.clear();
            studentList.addAll(studentService.getAllStudents());
        } catch (SQLException e) {
            showError("Error loading students: " + e.getMessage());
        }
    }

    private void selectStudent(Student student) {
        selectedStudent = student;
        nameField.setText(student.getName());
        regNoField.setText(student.getRegistrationNumber());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());

        // Select corresponding course in ComboBox
        for (Course c : courseList) {
            if (c.getId() == student.getCourseId()) {
                courseComboBox.setValue(c);
                break;
            }
        }
    }

    @FXML
    private void handleAddStudent(ActionEvent event) {
        String name = nameField.getText();
        String regNo = regNoField.getText();
        Course course = courseComboBox.getValue();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name == null || name.trim().isEmpty() ||
            regNo == null || regNo.trim().isEmpty() ||
            course == null) {
            showError("Please enter name, registration number and select a course.");
            return;
        }

        try {
            studentService.addStudent(name.trim(), regNo.trim(), course.getId(), email, phone);
            showSuccess("Student registered successfully.");
            clearForm();
            loadStudents();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStudent(ActionEvent event) {
        if (selectedStudent == null) {
            showError("Please select a student from the table to update.");
            return;
        }

        String name = nameField.getText();
        String regNo = regNoField.getText();
        Course course = courseComboBox.getValue();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name == null || name.trim().isEmpty() ||
            regNo == null || regNo.trim().isEmpty() ||
            course == null) {
            showError("Please enter name, registration number and select a course.");
            return;
        }

        try {
            studentService.updateStudent(selectedStudent.getId(), name.trim(), regNo.trim(), course.getId(), email, phone);
            showSuccess("Student profile updated successfully.");
            clearForm();
            loadStudents();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteStudent(ActionEvent event) {
        if (selectedStudent == null) {
            showError("Please select a student to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student '" + selectedStudent.getName() + "' (" + selectedStudent.getRegistrationNumber() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                studentService.deleteStudent(selectedStudent.getId());
                showSuccess("Student record deleted successfully.");
                clearForm();
                loadStudents();
            } catch (SQLException e) {
                showError("Cannot delete student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        selectedStudent = null;
        nameField.clear();
        regNoField.clear();
        courseComboBox.setValue(null);
        emailField.clear();
        phoneField.clear();
        studentTable.getSelectionModel().clearSelection();
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
