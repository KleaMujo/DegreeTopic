package com.school.degreetopicsmanagement.DataObjects;

public class StudentAssignmentSummary {
    private Long studentId;
    private String studentName;
    private int totalAssignments;
    private int totalResponses;

    public StudentAssignmentSummary(Long studentId, String studentName, int totalAssignments, int totalResponses) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalAssignments = totalAssignments;
        this.totalResponses = totalResponses;
    }

    // 🔑 Required public getters for Thymeleaf to work
    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getTotalAssignments() {
        return totalAssignments;
    }

    public int getTotalResponses() {
        return totalResponses;
    }

    // Optional: Setters (if needed elsewhere)
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setTotalAssignments(int totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }
}
