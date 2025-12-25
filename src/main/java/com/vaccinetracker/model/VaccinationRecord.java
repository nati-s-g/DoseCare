package com.vaccinetracker.model;

import java.time.LocalDate;

/**
 * VaccinationRecord class links a Child, Vaccine, and administration dates.
 * 
 * CONCEPT: Association & Composition
 * - This class associates Child with Vaccine
 * - Represents a many-to-many relationship (child has many vaccines, vaccine given to many children)
 * - Contains status information about the vaccination
 */
public class VaccinationRecord {
    
    private String recordId;                // Unique identifier for this record
    private String childId;                 // Which child this record belongs to
    private String vaccineId;               // Which vaccine this record is for
    private LocalDate dateAdministered;     // When vaccine was actually given (null if not yet given)
    private LocalDate nextDueDate;          // When next dose is due (for multi-dose vaccines)
    private VaccinationStatus status;       // Current status (PENDING or COMPLETED)
    private String vaccinationSiteId;       // Where the vaccine was administered

    /**
     * Enum to represent vaccination status.
     * 
     * CONCEPT: Enum
     * - A type-safe way to represent fixed set of constants
     * - Better than using strings ("PENDING", "COMPLETED")
     */
    public enum VaccinationStatus {
        PENDING,    // Vaccine not yet given
        COMPLETED   // Vaccine has been administered
    }
    
    /**
     * Constructor for a new vaccination record (status will be PENDING).
     * 
     * @param recordId Unique identifier
     * @param childId Child identifier
     * @param vaccineId Vaccine identifier
     * @param nextDueDate When the vaccine is due
     */
    public VaccinationRecord(String recordId, String childId, String vaccineId, LocalDate nextDueDate) {
        this.recordId = recordId;
        this.childId = childId;
        this.vaccineId = vaccineId;
        this.nextDueDate = nextDueDate;
        this.status = VaccinationStatus.PENDING;
        this.dateAdministered = null;  // Not yet given
        this.vaccinationSiteId = null;
    }
    
    /**
     * Constructor for a completed vaccination record.
     * 
     * @param recordId Unique identifier
     * @param childId Child identifier
     * @param vaccineId Vaccine identifier
     * @param dateAdministered When it was given
     * @param nextDueDate When next dose is due (can be null if single dose)
     */
    public VaccinationRecord(String recordId, String childId, String vaccineId, 
                            LocalDate dateAdministered, LocalDate nextDueDate) {
        this.recordId = recordId;
        this.childId = childId;
        this.vaccineId = vaccineId;
        this.dateAdministered = dateAdministered;
        this.nextDueDate = nextDueDate;
        this.status = VaccinationStatus.COMPLETED;
        this.vaccinationSiteId = null;
    }

    /**
     * Constructor for a vaccination record with site.
     * 
     * @param recordId Unique identifier
     * @param childId Child identifier
     * @param vaccineId Vaccine identifier
     * @param nextDueDate When the vaccine is due
     * @param vaccinationSiteId Where the vaccine is administered
     */
    public VaccinationRecord(String recordId, String childId, String vaccineId, LocalDate nextDueDate, String vaccinationSiteId) {
        this.recordId = recordId;
        this.childId = childId;
        this.vaccineId = vaccineId;
        this.nextDueDate = nextDueDate;
        this.status = VaccinationStatus.PENDING;
        this.dateAdministered = null;
        this.vaccinationSiteId = vaccinationSiteId;
    }
    
    // Getter methods
    public String getRecordId() {
        return recordId;
    }
    
    public String getChildId() {
        return childId;
    }
    
    public String getVaccineId() {
        return vaccineId;
    }
    
    public LocalDate getDateAdministered() {
        return dateAdministered;
    }
    
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }
    
    public VaccinationStatus getStatus() {
        return status;
    }
    
    // Setter methods
    public void setDateAdministered(LocalDate dateAdministered) {
        this.dateAdministered = dateAdministered;
        // When vaccine is administered, status changes to COMPLETED
        if (dateAdministered != null) {
            this.status = VaccinationStatus.COMPLETED;
        }
    }
    
    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
    
    /**
     * Mark this vaccination as completed.
     * Updates status and sets date administered to today.
     */
    public void markAsCompleted() {
        this.dateAdministered = LocalDate.now();
        this.status = VaccinationStatus.COMPLETED;
    }
    
    /**
     * Check if this vaccination is overdue.
     * 
     * @return true if nextDueDate has passed and status is still PENDING
     */
    public boolean isOverdue() {
        return status == VaccinationStatus.PENDING && 
               nextDueDate != null && 
               LocalDate.now().isAfter(nextDueDate);
    }
    
    /**
     * Override toString to display record information.
     * 
     * @return Formatted string with record details
     */
    @Override
    public String toString() {
        return "VaccinationRecord{" +
                "recordId='" + recordId + '\'' +
                ", childId='" + childId + '\'' +
                ", vaccineId='" + vaccineId + '\'' +
                ", dateAdministered=" + dateAdministered +
                ", nextDueDate=" + nextDueDate +
                ", status=" + status +
                '}';
    }

    public String getVaccinationSiteId() {
        return vaccinationSiteId;
    }

    public void setVaccinationSiteId(String vaccinationSiteId) {
        this.vaccinationSiteId = vaccinationSiteId;
    }
}

