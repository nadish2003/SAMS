package com.sams.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the main start screen.
 * Currently just opens a placeholder login screen.
 */
public class MainController {

    @FXML
    private Button openLoginBtn;

    @FXML
    private void handleOpenLogin(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) openLoginBtn.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 600, 450);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SAMS - Login");
            stage.show();
        } catch (java.io.IOException e) {
            System.err.println("Error opening login screen: " + e.getMessage());
        }
    }
}
