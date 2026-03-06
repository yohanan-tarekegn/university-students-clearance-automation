package com.university.clearance.model;

public class Student extends User {
    private String department;
    private String year;

    public Student(String id, String fullName, String email, String password, String department, String year) {
        super(id, fullName, email, password, "STUDENT");
        this.department = department;
        this.year = year;
    }

    public String getDepartment() { return department; }
    public String getYear() { return year; }
}
