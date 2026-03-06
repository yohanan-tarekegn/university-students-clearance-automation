package com.university.clearance.controller;

import com.university.clearance.MainApp;
import com.university.clearance.model.ClearanceRequest;
import com.university.clearance.model.Student;
import com.university.clearance.service.ClearanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<ClearanceRequest> requestTable;

    @FXML
    private TableColumn<ClearanceRequest, String> departmentColumn;

    @FXML
    private TableColumn<ClearanceRequest, String> statusColumn;
    
    @FXML
    private TableColumn<ClearanceRequest, String> commentColumn;

    @FXML
    private ComboBox<String> departmentComboBox;

    private ClearanceService clearanceService;
    private Student currentStudent;
    private ObservableList<ClearanceRequest> requestList;

    public void initialize() {
        clearanceService = new ClearanceService();
        currentStudent = (Student) LoginController.currentUser;
        
        welcomeLabel.setText("Welcome, " + currentStudent.getFullName() + " (" + currentStudent.getId() + ")");
        
        setupTable();
        // Dynamic departments first
        departmentComboBox.getItems().setAll(com.university.clearance.service.DataStore.getInstance().getAllDepartments());
        
        refreshTable();
    }

    private void setupTable() {
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        commentColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getComment() == null ? "-" : cellData.getValue().getComment()));
        
        requestList = FXCollections.observableArrayList();
        requestTable.setItems(requestList);
    }

    private void refreshTable() {
        requestList.setAll(clearanceService.getRequestsForStudent(currentStudent.getId()));
        
        boolean fullyCleared = clearanceService.isFullyCleared(currentStudent.getId());
        if (fullyCleared) {
            welcomeLabel.setText("CONGRATULATIONS! YOU ARE FULLY CLEARED.");
            departmentComboBox.setDisable(true);
        } else {
             boolean eligible = clearanceService.isEligibleForFinalClearance(currentStudent.getId());
             if (eligible) {
                 if (!departmentComboBox.getItems().contains("REGISTRAR")) {
                     departmentComboBox.getItems().add("REGISTRAR");
                     showAlert("You are now eligible to submit to Registrar!");
                 }
             }
        }
    }

    @FXML
    private void handleRequestClearance(ActionEvent event) {
        String dept = departmentComboBox.getValue();
        if (dept == null || dept.isEmpty()) {
            showAlert("Please select a department.");
            return;
        }

        String result = clearanceService.createRequest(currentStudent.getId(), dept);
        refreshTable();
        showAlert(result);
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        LoginController.currentUser = null;
        MainApp.setRoot("login");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
