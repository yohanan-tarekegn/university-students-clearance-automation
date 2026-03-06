package com.university.clearance.model;

import java.time.LocalDateTime;

public class ClearanceRequest {
    private String id;
    private String studentId;
    private String department; // The department this request is for
    private RequestStatus status;
    private String comment;
    private String requestedAt; // Store as String for simple JSON serialization

    public ClearanceRequest(String id, String studentId, String department) {
        this.id = id;
        this.studentId = studentId;
        this.department = department;
        this.status = RequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now().toString();
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getDepartment() { return department; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getRequestedAt() { return requestedAt; }
}
