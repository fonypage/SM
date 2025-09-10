package com.myinvest.startup_mvp_backend.service;

public class Submission {
    private final String submissionId;
    private final String status;

    public Submission(String submissionId, String status) {
        this.submissionId = submissionId;
        this.status = status;
    }

    public String getSubmissionId() { return submissionId; }
    public String getStatus() { return status; }
}
