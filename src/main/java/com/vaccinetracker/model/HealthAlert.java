package com.vaccinetracker.model;

import java.time.LocalDate;

/**
 * HealthAlert class representing community health messages and alerts.
 * 
 * CONCEPT: Domain Model
 * - Represents alerts/notifications about community health
 * - Contains severity levels (using enum)
 * - Used to inform parents about health recommendations
 */
public class HealthAlert {
    
    private String alertId;              // Unique identifier
    private String title;                // Alert title (e.g., "Polio Risk Increasing")
    private String message;              // Detailed message
    private SeverityLevel severityLevel; // How serious the alert is
    private LocalDate dateCreated;       // When the alert was created
    private boolean isActive;            // Whether alert is still active
    private String targetChildId;        // Optional: ID of the child this alert is for
    
    /**
     * Enum to represent alert severity levels.
     * 
     * CONCEPT: Enum
     * - Type-safe constants for severity levels
     */
    public enum SeverityLevel {
        LOW,        // Informational (e.g., "Free vaccination week")
        MEDIUM,     // Important (e.g., "New vaccine available")
        HIGH        // Urgent (e.g., "Disease outbreak in area")
    }
    
    /**
     * Constructor to create a health alert.
     * 
     * @param alertId Unique identifier
     * @param title Alert title
     * @param message Detailed message
     * @param severityLevel Severity level
     */
    public HealthAlert(String alertId, String title, String message, SeverityLevel severityLevel) {
        this(alertId, title, message, severityLevel, null);
    }

    /**
     * Constructor to create a targeted health alert.
     * 
     * @param alertId Unique identifier
     * @param title Alert title
     * @param message Detailed message
     * @param severityLevel Severity level
     * @param targetChildId ID of the child this alert is for
     */
    public HealthAlert(String alertId, String title, String message, SeverityLevel severityLevel, String targetChildId) {
        this.alertId = alertId;
        this.title = title;
        this.message = message;
        this.severityLevel = severityLevel;
        this.dateCreated = LocalDate.now();  // Automatically set to today
        this.isActive = true;                // New alerts are active by default
        this.targetChildId = targetChildId;
    }
    
    public String getTargetChildId() {
        return targetChildId;
    }

    public boolean isTargeted() {
        return targetChildId != null && !targetChildId.isEmpty();
    }
    
    // Getter methods
    public String getAlertId() {
        return alertId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }
    
    public LocalDate getDateCreated() {
        return dateCreated;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }
    
    /**
     * Mark this alert as inactive (no longer relevant).
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Mark this alert as active.
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Check if this is a high priority alert.
     * 
     * @return true if severity is HIGH
     */
    public boolean isHighPriority() {
        return severityLevel == SeverityLevel.HIGH;
    }
    
    /**
     * Override toString to display alert information.
     * 
     * @return Formatted string with alert details
     */
    @Override
    public String toString() {
        return "HealthAlert{" +
                "alertId='" + alertId + '\'' +
                ", title='" + title + '\'' +
                ", severityLevel=" + severityLevel +
                ", dateCreated=" + dateCreated +
                ", isActive=" + isActive +
                '}';
    }
}

