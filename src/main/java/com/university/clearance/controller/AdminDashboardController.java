package com.university.clearance.controller;

import com.university.clearance.MainApp;
import com.university.clearance.model.Staff;
import com.university.clearance.model.Student;
import com.university.clearance.service.AdminService;
import com.university.clearance.service.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private TextField stdId, stdName, stdEmail, stdPass, stdDept, stdYear;
    @FXML private TextField staffId, staffName, staffEmail, staffPass, staffDept;
    
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> sIdCol, sNameCol, sEmailCol, sDeptCol, sYearCol;

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> tIdCol, tNameCol, tEmailCol, tDeptCol;

    private final AdminService adminService;
    private final DataStore dataStore;
    
    private ObservableList<Student> studentList;
    private ObservableList<Staff> staffList;

    public AdminDashboardController() {
        this.adminService = new AdminService();
        this.dataStore = DataStore.getInstance();
    }
    
    public void initialize() {
        setupStudentTable();
        setupStaffTable();
        refreshTables();
    }

    private void setupStudentTable() {
        sIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        sEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        sDeptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        sYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        
        studentList = FXCollections.observableArrayList();
        studentTable.setItems(studentList);
        
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                stdId.setText(newVal.getId());
                stdName.setText(newVal.getFullName());
                stdEmail.setText(newVal.getEmail());
                stdPass.setText(newVal.getPassword()); // In real app, don't show pass
                stdDept.setText(newVal.getDepartment());
                stdYear.setText(newVal.getYear());
                stdId.setEditable(false); // ID is PK
            } else {
                 clearFields();
                 stdId.setEditable(true);
            }
        });
    }

    private void setupStaffTable() {
        tIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        tNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        tDeptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        staffList = FXCollections.observableArrayList();
        staffTable.setItems(staffList);
        
        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                staffId.setText(newVal.getId());
                staffName.setText(newVal.getFullName());
                staffEmail.setText(newVal.getEmail());
                staffPass.setText(newVal.getPassword());
                staffDept.setText(newVal.getDepartment());
                staffId.setEditable(false);
            } else {
                 clearFields();
                 staffId.setEditable(true);
            }
        });
    }

    private void refreshTables() {
        studentList.setAll(dataStore.getStudents());
        staffList.setAll(dataStore.getStaffMembers());
    }

    @FXML
    private void handleAddStudent(ActionEvent event) {
        if (stdId.getText().isEmpty() || stdName.getText().isEmpty()) {
            showAlert("Error", "Please fill fields.");
            return;
        }
        if (adminService.addStudent(stdId.getText(), stdName.getText(), stdEmail.getText(), stdPass.getText(), stdDept.getText(), stdYear.getText())) {
            refreshTables();
            clearFields();
            stdId.setEditable(true);
            showAlert("Success", "Student added.");
        } else {
             showAlert("Error", "Student creation failed. ID might already exist.");
        }
    }

    @FXML
    private void handleUpdateStudent(ActionEvent event) {
        if (stdId.getText().isEmpty()) return;
        adminService.updateStudent(stdId.getText(), stdName.getText(), stdEmail.getText(), stdPass.getText(), stdDept.getText(), stdYear.getText());
        refreshTables();
        clearFields();
        stdId.setEditable(true);
        showAlert("Success", "Student updated.");
    }

    @FXML
    private void handleDeleteStudent(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        adminService.deleteUser(selected.getId());
        refreshTables();
        clearFields();
        stdId.setEditable(true);
        showAlert("Success", "Student deleted.");
    }

    // --- Staff Handlers ---

    @FXML
    private void handleAddStaff(ActionEvent event) {
        if (staffId.getText().isEmpty()) return;
        if (adminService.addStaff(staffId.getText(), staffName.getText(), staffEmail.getText(), staffPass.getText(), staffDept.getText())) {
            refreshTables();
            clearFields();
            staffId.setEditable(true);
            showAlert("Success", "Staff added.");
        } else {
            showAlert("Error", "Staff creation failed. ID might already exist.");
        }
    }
    
    @FXML
    private void handleUpdateStaff(ActionEvent event) {
        if (staffId.getText().isEmpty()) return;
        adminService.updateStaff(staffId.getText(), staffName.getText(), staffEmail.getText(), staffPass.getText(), staffDept.getText());
        refreshTables();
        clearFields();
        staffId.setEditable(true);
         showAlert("Success", "Staff updated.");
    }

    @FXML
    private void handleDeleteStaff(ActionEvent event) {
         Staff selected = staffTable.getSelectionModel().getSelectedItem();
         if (selected == null) return;
         adminService.deleteUser(selected.getId());
         refreshTables();
         clearFields();
         staffId.setEditable(true);
         showAlert("Success", "Staff deleted.");
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
        studentTable.getSelectionModel().clearSelection();
        staffTable.getSelectionModel().clearSelection();
        stdId.setEditable(true);
        staffId.setEditable(true);
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        LoginController.currentUser = null;
        MainApp.setRoot("login");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
    
    private void clearFields() {
        stdId.clear(); stdName.clear(); stdEmail.clear(); stdPass.clear(); stdDept.clear(); stdYear.clear();
        staffId.clear(); staffName.clear(); staffEmail.clear(); staffPass.clear(); staffDept.clear();
    }
}
