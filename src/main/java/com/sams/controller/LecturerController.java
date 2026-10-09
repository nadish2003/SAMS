package com.sams.controller;

import com.sams.model.Lecturer;
import com.sams.model.Subject;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * LecturerController – handles Lecturer management and Subject assignments.
 * Accessible only to Admin users.
 */
public class LecturerController implements Initializable {

    // Lecturer form fields
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private Button addLecturerBtn;
    @FXML private Button updateLecturerBtn;
    @FXML private Button deleteLecturerBtn;
    @FXML private Button clearBtn;

    // Lecturers Table
    @FXML private TableView<Lecturer> lecturerTable;
    @FXML private TableColumn<Lecturer, Integer> idCol;
    @FXML private TableColumn<Lecturer, String> nameCol;
    @FXML private TableColumn<Lecturer, String> emailCol;
    @FXML private TableColumn<Lecturer, String> phoneCol;
    @FXML private TableColumn<Lecturer, String> usernameCol;

    // Subject Assignment controls
    @FXML private Label selectedLecturerLabel;
    @FXML private ComboBox<Subject> availableSubjectComboBox;
    @FXML private Button assignSubjectBtn;
    @FXML private Button unassignSubjectBtn;

    @FXML private TableView<Subject> assignedSubjectTable;
    @FXML private TableColumn<Subject, String> subCodeCol;
    @FXML private TableColumn<Subject, String> subNameCol;
    @FXML private TableColumn<Subject, String> subCourseCol;

    @FXML private Label messageLabel;

    private final LecturerService lecturerService = new LecturerService();
    private final CourseService courseService = new CourseService();

    private final ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();
    private final ObservableList<Subject> availableSubjectList = FXCollections.observableArrayList();
    private final ObservableList<Subject> assignedSubjectList = FXCollections.observableArrayList();
    private Lecturer selectedLecturer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Session.isAdmin()) {
            showError("Access Denied: Admin privileges required.");
            disableForm();
            return;
        }

        // Configure lecturer table
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        lecturerTable.setItems(lecturerList);

        // Configure assigned subjects table
        subCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        subNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        subCourseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        assignedSubjectTable.setItems(assignedSubjectList);

        // Configure available subjects combobox
        availableSubjectComboBox.setItems(availableSubjectList);

        // Lecturer table selection listener
        lecturerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectLecturer(newVal);
            }
        });

        loadLecturers();
        loadAvailableSubjects();
    }

    private void disableForm() {
        if (addLecturerBtn != null) addLecturerBtn.setDisable(true);
        if (updateLecturerBtn != null) updateLecturerBtn.setDisable(true);
        if (deleteLecturerBtn != null) deleteLecturerBtn.setDisable(true);
        if (assignSubjectBtn != null) assignSubjectBtn.setDisable(true);
        if (unassignSubjectBtn != null) unassignSubjectBtn.setDisable(true);
    }

    private void loadLecturers() {
        try {
            lecturerList.clear();
            lecturerList.addAll(lecturerService.getAllLecturers());
        } catch (SQLException e) {
            showError("Error loading lecturers: " + e.getMessage());
        }
    }

    private void loadAvailableSubjects() {
        try {
            availableSubjectList.clear();
            availableSubjectList.addAll(courseService.getAllSubjects());
        } catch (SQLException e) {
            showError("Error loading subjects: " + e.getMessage());
        }
    }

    private void selectLecturer(Lecturer lecturer) {
        selectedLecturer = lecturer;
        nameField.setText(lecturer.getName());
        emailField.setText(lecturer.getEmail());
        phoneField.setText(lecturer.getPhone());
        usernameField.setText(lecturer.getUsername());
        usernameField.setDisable(true); // Don't allow changing username once created
        passwordField.clear();
        passwordField.setDisable(true);

        selectedLecturerLabel.setText("Managing subjects for: " + lecturer.getName());
        loadAssignedSubjects();
    }

    private void loadAssignedSubjects() {
        if (selectedLecturer == null) {
            assignedSubjectList.clear();
            return;
        }
        try {
            assignedSubjectList.clear();
            assignedSubjectList.addAll(lecturerService.getAssignedSubjects(selectedLecturer.getId()));
        } catch (SQLException e) {
            showError("Error loading assigned subjects: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddLecturer(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name == null || name.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            showError("Please enter name, username, and password.");
            return;
        }

        try {
            lecturerService.addLecturer(name.trim(), email, phone, username.trim(), password.trim());
            showSuccess("Lecturer registered successfully.");
            clearForm();
            loadLecturers();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateLecturer(ActionEvent event) {
        if (selectedLecturer == null) {
            showError("Please select a lecturer from the table to update.");
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name == null || name.trim().isEmpty()) {
            showError("Lecturer name cannot be empty.");
            return;
        }

        try {
            lecturerService.updateLecturer(selectedLecturer.getId(), name.trim(), email, phone);
            showSuccess("Lecturer details updated successfully.");
            clearForm();
            loadLecturers();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteLecturer(ActionEvent event) {
        if (selectedLecturer == null) {
            showError("Please select a lecturer to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete lecturer '" + selectedLecturer.getName() + "' and their user account?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                lecturerService.deleteLecturer(selectedLecturer.getId());
                showSuccess("Lecturer record deleted successfully.");
                clearForm();
                loadLecturers();
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key")) {
                    showError("Cannot delete lecturer: Scheduled classes depend on this lecturer.");
                } else {
                    showError("Cannot delete lecturer: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        selectedLecturer = null;
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        usernameField.clear();
        usernameField.setDisable(false);
        passwordField.clear();
        passwordField.setDisable(false);

        lecturerTable.getSelectionModel().clearSelection();
        selectedLecturerLabel.setText("Select a lecturer to manage assigned subjects");
        assignedSubjectList.clear();
    }

    // --- Subject Assignment Handlers ---

    @FXML
    private void handleAssignSubject(ActionEvent event) {
        if (selectedLecturer == null) {
            showError("Please select a lecturer first before assigning a subject.");
            return;
        }

        Subject subject = availableSubjectComboBox.getValue();
        if (subject == null) {
            showError("Please select a subject to assign.");
            return;
        }

        try {
            lecturerService.assignSubject(selectedLecturer.getId(), subject.getId());
            showSuccess("Subject '" + subject.getName() + "' assigned successfully.");
            loadAssignedSubjects();
        } catch (SQLException e) {
            showError("Error assigning subject: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUnassignSubject(ActionEvent event) {
        if (selectedLecturer == null) {
            showError("Please select a lecturer first.");
            return;
        }

        Subject subject = assignedSubjectTable.getSelectionModel().getSelectedItem();
        if (subject == null) {
            showError("Please select an assigned subject to remove.");
            return;
        }

        try {
            lecturerService.unassignSubject(selectedLecturer.getId(), subject.getId());
            showSuccess("Subject assignment removed successfully.");
            loadAssignedSubjects();
        } catch (SQLException e) {
            showError("Error removing subject assignment: " + e.getMessage());
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
