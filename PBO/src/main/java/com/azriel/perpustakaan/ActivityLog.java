package com.azriel.perpustakaan;

import java.time.LocalDateTime;

public class ActivityLog {
    private Integer id;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    public ActivityLog() {
    }

    public ActivityLog(Integer id, String username, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public ActivityLog(String username, String action, String details) {
        this(null, username, action, details, null);
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
