package com.university.clearance.service;

import com.university.clearance.model.Staff;
import com.university.clearance.model.Student;
import com.university.clearance.model.User;

import java.util.Optional;

public class AuthService {
    private final DataStore dataStore;

    public AuthService() {
        this.dataStore = DataStore.getInstance();
    }

    public User authenticate(String email, String password) {
        // Check students
        Optional<Student> student = dataStore.findStudentByEmail(email);
        if (student.isPresent() && student.get().getPassword().equals(password)) {
            return student.get();
        }

        // Check staff
        Optional<Staff> staff = dataStore.findStaffByEmail(email);
        if (staff.isPresent() && staff.get().getPassword().equals(password)) {
            return staff.get();
        }
        
        // Check Admin
        Optional<User> admin = dataStore.findAdminByEmail(email);
        if (admin.isPresent() && admin.get().getPassword().equals(password)) {
            return admin.get();
        }

        return null;
    }
}
