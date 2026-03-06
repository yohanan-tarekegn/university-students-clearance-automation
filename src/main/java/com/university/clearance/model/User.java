package com.university.clearance.model;

public abstract class User {
    private String id;
    private String fullName;
    private String email;
    private String password; // In a real app, this should be hashed
    private String role; // STUDENT, STAFF, ADMIN

    public User(String id, String fullName, String email, String password, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
