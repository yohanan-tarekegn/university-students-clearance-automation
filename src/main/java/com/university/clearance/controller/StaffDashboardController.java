package com.university.clearance.controller;

import com.university.clearance.MainApp;
import com.university.clearance.model.ClearanceRequest;
import com.university.clearance.model.RequestStatus;
import com.university.clearance.model.Staff;
import com.university.clearance.service.ClearanceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class StaffDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<ClearanceRequest> pendingTable;

    @FXML
    private TableColumn<ClearanceRequest, String> studentIdColumn;

    @FXML
    private TableColumn<ClearanceRequest, String> statusColumn;

    @FXML
    private TextArea commentArea;

    private ClearanceService clearanceService;
    private Staff currentStaff;
    private ObservableList<ClearanceRequest> pendingRequests;

    public void initialize() {
        clearanceService = new ClearanceService();
        currentStaff = (Staff) LoginController.currentUser;
        
        welcomeLabel.setText("Staff Panel: " + currentStaff.getFullName() + " - " + currentStaff.getDepartment());
        
        setupTable();
        refreshTable();
    }

    private void setupTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        pendingRequests = FXCollections.observableArrayList();
        pendingTable.setItems(pendingRequests);
        
        // Listener for selection
        pendingTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    commentArea.clear();
                }
            }
        );
    }

    private void refreshTable() {
        pendingRequests.setAll(clearanceService.getPendingRequestsForDepartment(currentStaff.getDepartment()));
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        processRequest(RequestStatus.APPROVED);
    }

    @FXML
    private void handleReject(ActionEvent event) {
        processRequest(RequestStatus.REJECTED);
    }

    private void processRequest(RequestStatus status) {
        ClearanceRequest selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No request selected.");
            return;
        }
        
        clearanceService.updateRequestStatus(selected.getId(), status, commentArea.getText());
        refreshTable();
        commentArea.clear();
        showAlert("Request " + status);
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
