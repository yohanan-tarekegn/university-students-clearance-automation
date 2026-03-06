package com.university.clearance.service;

import com.university.clearance.model.ClearanceRequest;
import com.university.clearance.model.RequestStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClearanceService {
    private final DataStore dataStore;

    public ClearanceService() {
        this.dataStore = DataStore.getInstance();
    }

    public String createRequest(String studentId, String department) {
        // Fetch all requests for this student and department
        List<ClearanceRequest> specificRequests = dataStore.getRequestsForStudent(studentId).stream()
                .filter(r -> r.getDepartment().equals(department))
                .collect(Collectors.toList());

        // 1. Check if ANY request is already PENDING or APPROVED -> Block
        for (ClearanceRequest r : specificRequests) {
            if (r.getStatus() == RequestStatus.PENDING) {
                return "Request failed: You already have a PENDING request for " + department;
            }
            if (r.getStatus() == RequestStatus.APPROVED) {
                return "Request failed: You are already APPROVED for " + department;
            }
        }

        // 2. Recycle REJECTED request if found
        java.util.Optional<ClearanceRequest> rejectedRequest = specificRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .findFirst();

        if (rejectedRequest.isPresent()) {
            ClearanceRequest r = rejectedRequest.get();
            r.setStatus(RequestStatus.PENDING);
            r.setComment(""); // Clear rejection reason
            dataStore.updateRequest(r);
            return "Request re-submitted (Recycled previous rejection)";
        }

        // 3. No existing requests at all, create new
        ClearanceRequest request = new ClearanceRequest(UUID.randomUUID().toString(), studentId, department);
        dataStore.createRequest(request);
        return "Request sent successfully to " + department;
    }

    public List<ClearanceRequest> getRequestsForStudent(String studentId) {
        return dataStore.getRequestsForStudent(studentId);
    }

    public List<ClearanceRequest> getPendingRequestsForDepartment(String department) {
        return dataStore.getPendingRequestsForDepartment(department);
    }

    public void updateRequestStatus(String requestId, RequestStatus status, String comment) {
        ClearanceRequest dummy = new ClearanceRequest(requestId, "", ""); // ID is what matters for update
        dummy.setStatus(status);
        dummy.setComment(comment);
        dataStore.updateRequest(dummy);
    }


    public boolean isEligibleForFinalClearance(String studentId) {
        List<ClearanceRequest> requests = getRequestsForStudent(studentId);
        
        // Dynamic check: All departments in the system must be approved
        List<String> requiredDepts = dataStore.getAllDepartments();
        
        for (String dept : requiredDepts) {
            boolean isApproved = requests.stream()
                    .anyMatch(r -> r.getDepartment().equalsIgnoreCase(dept) && r.getStatus() == RequestStatus.APPROVED);
            
            if (!isApproved) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isFullyCleared(String studentId) {
         return getRequestsForStudent(studentId).stream()
                 .anyMatch(r -> r.getDepartment().equalsIgnoreCase("REGISTRAR") && r.getStatus() == RequestStatus.APPROVED);
    }
}
