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
        // Placeholder – login screen will be added in a later phase
        System.out.println("Open Login clicked – login screen not yet implemented");
    }
}
