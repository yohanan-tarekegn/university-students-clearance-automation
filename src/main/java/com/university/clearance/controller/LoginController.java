package com.university.clearance.controller;

import com.university.clearance.MainApp;
import com.university.clearance.model.Staff;
import com.university.clearance.model.Student;
import com.university.clearance.model.User;
import com.university.clearance.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService;
    
    // Session state
    public static User currentUser;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = authService.authenticate(email, password);

        if (user != null) {
            currentUser = user;
            try {
                if (user instanceof Student) {
                    MainApp.setRoot("student_dashboard");
                } else if (user instanceof Staff) {
                    MainApp.setRoot("staff_dashboard");
                } else if (user.getRole().equals("ADMIN")) {
                    MainApp.setRoot("admin_dashboard");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load dashboard.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
