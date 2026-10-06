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
 * DashboardController – manages the dashboard placeholder screen.
 */
public class DashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Session.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        } else {
            welcomeLabel.setText("Welcome to SAMS");
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
