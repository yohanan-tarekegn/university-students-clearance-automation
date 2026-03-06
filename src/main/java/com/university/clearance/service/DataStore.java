package com.university.clearance.service;

import com.university.clearance.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataStore {
    private static DataStore instance;

    private DataStore() {
        // Initialize DB on startup
        DatabaseHandler.initDatabase();
        seedDataIfEmpty();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void seedDataIfEmpty() {
        if (getStudents().isEmpty() && getStaffMembers().isEmpty()) {
            System.out.println("Seeding initial data...");
            saveUser(new Student("S001", "Alice Student", "alice@uni.edu", "password", "Computer Science", "4"));
            saveUser(new Student("S002", "Bob Student", "bob@uni.edu", "password", "Engineering", "4"));
            
            saveUser(new Staff("T001", "Librarian", "lib@uni.edu", "admin", "LIBRARY"));
            saveUser(new Staff("T002", "Finance Officer", "finance@uni.edu", "admin", "FINANCE"));
            saveUser(new Staff("T003", "Dept Head", "dept@uni.edu", "admin", "DEPARTMENT"));
            saveUser(new Staff("T004", "Sports Officer", "sports@uni.edu", "admin", "SPORTS"));
            saveUser(new Staff("T005", "Hostel Warden", "hostel@uni.edu", "admin", "HOSTEL"));
        }
        
        // Ensure Registrar exists independently
        if (!findStaffByEmail("registrar@uni.edu").isPresent()) {
             System.out.println("Seeding Registrar...");
             saveUser(new Staff("T006", "Registrar", "registrar@uni.edu", "admin", "REGISTRAR"));
        }
        
        // Ensure Admin exists independently
        if (!findAdminByEmail("admin@uni.edu").isPresent()) {
             System.out.println("Seeding Admin User...");
             saveUser(new User("A001", "System Admin", "admin@uni.edu", "admin", "ADMIN") {});
        }
    }

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (id, full_name, email, password, role, department, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            
            if (user instanceof Student) {
                pstmt.setString(6, ((Student) user).getDepartment());
                pstmt.setString(7, ((Student) user).getYear());
            } else if (user instanceof Staff) {
                pstmt.setString(6, ((Staff) user).getDepartment());
                pstmt.setNull(7, Types.VARCHAR);
            } else {
                // Admin or generic user
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
            }
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'STUDENT'";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("department"),
                        rs.getString("year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Staff> getStaffMembers() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'STAFF'";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Staff(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllDepartments() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM users WHERE role = 'STAFF' AND department IS NOT NULL";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String dept = rs.getString("department");
                if (!dept.equalsIgnoreCase("REGISTRAR") && !dept.equalsIgnoreCase("ADMIN")) {
                   list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Student> findStudentByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'STUDENT'";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new Student(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("department"),
                        rs.getString("year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Staff> findStaffByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'STAFF'";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new Staff(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findAdminByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'ADMIN'";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        "ADMIN"
                ) {});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // --- Request Methods ---

    public void createRequest(ClearanceRequest request) {
        String sql = "INSERT INTO requests (id, student_id, department, status, comment, requested_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getId());
            pstmt.setString(2, request.getStudentId());
            pstmt.setString(3, request.getDepartment());
            pstmt.setString(4, request.getStatus().name());
            pstmt.setString(5, request.getComment());
            pstmt.setString(6, request.getRequestedAt());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ClearanceRequest> getRequestsForStudent(String studentId) {
        List<ClearanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE student_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ClearanceRequest req = new ClearanceRequest(
                        rs.getString("id"),
                        rs.getString("student_id"),
                        rs.getString("department")
                );
                req.setStatus(RequestStatus.valueOf(rs.getString("status")));
                req.setComment(rs.getString("comment"));
                // requestedAt is string, we'll just ignore setting it back for now as it's not critical for logic
                list.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ClearanceRequest> getPendingRequestsForDepartment(String department) {
        List<ClearanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE department = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                 ClearanceRequest req = new ClearanceRequest(
                        rs.getString("id"),
                        rs.getString("student_id"),
                        rs.getString("department")
                );
                req.setStatus(RequestStatus.valueOf(rs.getString("status")));
                req.setComment(rs.getString("comment"));
                list.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateRequest(ClearanceRequest request) {
        String sql = "UPDATE requests SET status = ?, comment = ? WHERE id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getStatus().name());
            pstmt.setString(2, request.getComment());
            pstmt.setString(3, request.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
