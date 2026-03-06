package com.university.clearance.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "clearance_db";
    // NOTE: You might need to change these credentials
    private static final String USER = "root"; 
    private static final String PASS = "oracle";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            // Create Database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            
            // Use the database
            stmt.executeUpdate("USE " + DB_NAME);
            
            // Create Users Table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "full_name VARCHAR(100), " +
                    "email VARCHAR(100) UNIQUE, " +
                    "password VARCHAR(100), " +
                    "role VARCHAR(20), " +
                    "department VARCHAR(50), " + // For Staff & Student
                    "year VARCHAR(10) " +        // For Student
                    ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create Requests Table
            String createRequestsTable = "CREATE TABLE IF NOT EXISTS requests (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "student_id VARCHAR(50), " +
                    "department VARCHAR(50), " +
                    "status VARCHAR(20), " +
                    "comment TEXT, " +
                    "requested_at VARCHAR(50), " +
                    "FOREIGN KEY (student_id) REFERENCES users(id)" +
                    ")";
            stmt.executeUpdate(createRequestsTable);
            
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
