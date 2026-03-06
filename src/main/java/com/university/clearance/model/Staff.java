package com.university.clearance.model;

public class Staff extends User {
    private String department; // The department they are responsible for clearing (e.g., LIBRARY, FINANCE, DEPARTMENT)

    public Staff(String id, String fullName, String email, String password, String department) {
        super(id, fullName, email, password, "STAFF");
        this.department = department;
    }

    public String getDepartment() { return department; }
}
