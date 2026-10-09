package com.sams.controller;

import com.sams.model.User;
import com.sams.service.AuthService;
import com.sams.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * DashboardController – manages the role-based dashboard.
 * Shows available modules based on whether the logged-in user is an Admin or Lecturer.
 */
public class DashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleBadgeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button coursesBtn;

    @FXML
    private Button studentsBtn;

    @FXML
    private Button lecturersBtn;

    @FXML
    private Button classesBtn;

    @FXML
    private Button attendanceBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button logoutButton;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Session.getCurrentUser();
        if (user == null) {
            welcomeLabel.setText("Welcome");
            roleBadgeLabel.setText("Not logged in");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getUsername());
        roleBadgeLabel.setText("Role: " + user.getRole());

        // Role-based visibility
        boolean isAdmin = user.isAdmin();
        boolean isLecturer = user.isLecturer();

        // Admin-only modules
        coursesBtn.setVisible(isAdmin);
        coursesBtn.setManaged(isAdmin);

        studentsBtn.setVisible(isAdmin);
        studentsBtn.setManaged(isAdmin);

        lecturersBtn.setVisible(isAdmin);
        lecturersBtn.setManaged(isAdmin);

        // Classes is visible to both Admin and Lecturer
        classesBtn.setVisible(isAdmin || isLecturer);
        classesBtn.setManaged(isAdmin || isLecturer);

        // Attendance is visible to Lecturer only
        attendanceBtn.setVisible(isLecturer);
        attendanceBtn.setManaged(isLecturer);

        // Reports is visible to both Admin and Lecturer
        reportsBtn.setVisible(isAdmin || isLecturer);
        reportsBtn.setManaged(isAdmin || isLecturer);
    }

    @FXML
    private void handleCourses(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/courses.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Course & Subject Management");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading courses screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleStudents(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/students.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 950, 620);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Student Management");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading students screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleLecturers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lecturers.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 980, 620);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Lecturer Management");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading lecturers screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleClasses(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/classes.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 980, 640);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Class Scheduling");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading class scheduling screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleAttendance(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/attendance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 980, 640);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Attendance Marking");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading attendance marking screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1020, 660);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Attendance Reports");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading reports screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 450);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error returning to login screen: " + e.getMessage());
        }
    }
}
