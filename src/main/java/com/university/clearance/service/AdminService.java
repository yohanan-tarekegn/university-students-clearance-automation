package com.university.clearance.service;

import com.university.clearance.model.Student;
import com.university.clearance.model.Staff;
import com.university.clearance.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminService {
    
    private final DataStore dataStore;

    public AdminService() {
        this.dataStore = DataStore.getInstance();
    }

    public boolean addStudent(String id, String name, String email, String password, String dept, String year) {
        Student student = new Student(id, name, email, password, dept, year);
        return dataStore.saveUser(student);
    }

    public boolean addStaff(String id, String name, String email, String password, String dept) {
        Staff staff = new Staff(id, name, email, password, dept);
        return dataStore.saveUser(staff);
    }
    
    // Quick and dirty deletion
    public void deleteUser(String id) {
        // Warning: This doesn't cascade delete requests in this simple impl, but it should.
        deleteRequestsForUser(id);
        
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // deleted brace
    
    private void deleteRequestsForUser(String studentId) {
        String sql = "DELETE FROM requests WHERE student_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateStudent(String id, String name, String email, String password, String dept, String year) {
         // Assuming ID is immutable PK for now.
         String sql = "UPDATE users SET full_name=?, email=?, password=?, department=?, year=? WHERE id=?";
         try (Connection conn = DatabaseHandler.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, name);
             pstmt.setString(2, email);
             pstmt.setString(3, password);
             pstmt.setString(4, dept);
             pstmt.setString(5, year);
             pstmt.setString(6, id);
             pstmt.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }

    public void updateStaff(String id, String name, String email, String password, String dept) {
         String sql = "UPDATE users SET full_name=?, email=?, password=?, department=? WHERE id=?";
         try (Connection conn = DatabaseHandler.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, name);
             pstmt.setString(2, email);
             pstmt.setString(3, password);
             pstmt.setString(4, dept);
             pstmt.setString(5, id);
             pstmt.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }
}
