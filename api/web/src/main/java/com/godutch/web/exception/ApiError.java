package com.godutch.web.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiError {
    private final OffsetDateTime timestamp = OffsetDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldErrorDetail> details;

    public ApiError(int status, String error, String message, String path, List<FieldErrorDetail> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldErrorDetail> getDetails() { return details; }

    public static class FieldErrorDetail {
        private final String field;
        private final String message;
        public FieldErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }
        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
