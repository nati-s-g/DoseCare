package com.vaccinetracker.services;

import com.vaccinetracker.model.HealthAlert;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertService class manages health alerts and community messages.
 * 
 * CONCEPT: Service Layer
 * - Manages health alerts for the community
 * - Provides methods to create, retrieve, and manage alerts
 * - Filters active/inactive alerts
 */
public class AlertService {
    
    // Store health alerts in a List
    private static List<HealthAlert> healthAlerts = new ArrayList<>();
    private static int nextAlertId = 1;  // Auto-increment ID generator
    
    /**
     * Constructor - initializes the alerts list.
     */
    public AlertService() {
        // No longer automatically initializing sample alerts here
        // This is now handled by StorageService on first run
    }
    
    /**
     * Initialize with some sample health alerts for demonstration.
     */
    public static void initializeSampleAlerts() {
        if (!healthAlerts.isEmpty()) return;

        // Create some sample alerts
        createAlert("Free Vaccination Week", 
                   "This week, all vaccination sites are offering free vaccinations for children under 5 years.",
                   HealthAlert.SeverityLevel.LOW);
        
        createAlert("New Measles Vaccine Available", 
                   "The new measles vaccine is now available at all vaccination sites. Contact your nearest site for scheduling.",
                   HealthAlert.SeverityLevel.MEDIUM);
        
        createAlert("Polio Awareness Campaign", 
                   "Join the polio vaccination campaign. All children between 0-5 years are eligible for free vaccination.",
                   HealthAlert.SeverityLevel.MEDIUM);
    }
    
    /**
     * Create a new health alert.
     * 
     * @param title Alert title
     * @param message Alert message
     * @param severityLevel Severity level
     * @return The created HealthAlert object
     */
    public static HealthAlert createAlert(String title, String message, HealthAlert.SeverityLevel severityLevel) {
        return createTargetedAlert(title, message, severityLevel, null);
    }

    /**
     * Create a new targeted health alert.
     * 
     * @param title Alert title
     * @param message Detailed message
     * @param severityLevel Severity level
     * @param targetChildId ID of the child this alert is for
     * @return The created HealthAlert object
     */
    public static HealthAlert createTargetedAlert(String title, String message, HealthAlert.SeverityLevel severityLevel, String targetChildId) {
        String alertId = "ALT" + String.format("%04d", nextAlertId++);
        HealthAlert alert = new HealthAlert(alertId, title, message, severityLevel, targetChildId);
        healthAlerts.add(alert);
        StorageService.saveAll();
        return alert;
    }
    
    /**
     * Get a health alert by its ID.
     * 
     * @param alertId The alert ID to search for
     * @return HealthAlert object if found, null otherwise
     */
    public HealthAlert getAlertById(String alertId) {
        for (HealthAlert alert : healthAlerts) {
            if (alert.getAlertId().equals(alertId)) {
                return alert;
            }
        }
        return null;
    }
    
    /**
     * Get all active health alerts.
     * Only returns alerts that are currently active.
     * 
     * CONCEPT: Filtering Collections
     * 
     * @return List of active alerts
     */
    public List<HealthAlert> getActiveAlerts() {
        List<HealthAlert> activeAlerts = new ArrayList<>();
        for (HealthAlert alert : healthAlerts) {
            if (alert.isActive() && !alert.isTargeted()) {
                activeAlerts.add(alert);
            }
        }
        return activeAlerts;
    }

    /**
     * Get active alerts relevant to a parent (general + targeted).
     * 
     * @param childIds List of child IDs belonging to the parent
     * @return List of relevant alerts
     */
    public List<HealthAlert> getAlertsForParent(List<String> childIds) {
        List<HealthAlert> relevantAlerts = new ArrayList<>();
        for (HealthAlert alert : healthAlerts) {
            if (alert.isActive()) {
                if (!alert.isTargeted()) {
                    // Add general alerts
                    relevantAlerts.add(alert);
                } else if (childIds != null && childIds.contains(alert.getTargetChildId())) {
                    // Add targeted alerts for this parent's children
                    relevantAlerts.add(alert);
                }
            }
        }
        return relevantAlerts;
    }
    
    /**
     * Get high priority alerts (severity level HIGH).
     * 
     * @return List of high priority alerts
     */
    public List<HealthAlert> getHighPriorityAlerts() {
        List<HealthAlert> highPriorityAlerts = new ArrayList<>();
        for (HealthAlert alert : healthAlerts) {
            if (alert.isActive() && alert.isHighPriority()) {
                highPriorityAlerts.add(alert);
            }
        }
        return highPriorityAlerts;
    }
    
    /**
     * Get all alerts (active and inactive).
     * 
     * @return List of all alerts
     */
    public List<HealthAlert> getAllAlerts() {
        return new ArrayList<>(healthAlerts);  // Return a copy
    }
    
    /**
     * Deactivate an alert (mark it as no longer relevant).
     * 
     * @param alertId The alert ID to deactivate
     * @return true if alert was found and deactivated, false otherwise
     */
    public boolean deactivateAlert(String alertId) {
        HealthAlert alert = getAlertById(alertId);
        if (alert != null) {
            alert.deactivate();
            StorageService.saveAll();
            return true;
        }
        return false;
    }
    
    /**
     * Activate an alert.
     * 
     * @param alertId The alert ID to activate
     * @return true if alert was found and activated, false otherwise
     */
    public boolean activateAlert(String alertId) {
        HealthAlert alert = getAlertById(alertId);
        if (alert != null) {
            alert.activate();
            StorageService.saveAll();
            return true;
        }
        return false;
    }
    
    /**
     * Get the total number of active alerts.
     * 
     * @return Number of active alerts
     */
    public int getActiveAlertCount() {
        return getActiveAlerts().size();
    }

    // Static access for StorageService
    public static List<HealthAlert> getAllData() {
        return healthAlerts;
    }

    public static void setAllData(List<HealthAlert> data) {
        healthAlerts = data;
    }
}

