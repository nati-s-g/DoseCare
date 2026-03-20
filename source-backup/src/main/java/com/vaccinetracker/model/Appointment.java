package com.vaccinetracker.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Appointment class represents a scheduled vaccination appointment.
 * 
 * CONCEPT: Domain Model
 * - Represents a booking made by a parent for a child
 * - Can be for standard vaccines or additional/optional ones
 */
public class Appointment {
    
    private String appointmentId;
    private String childId;
    private String vaccineName;      // Name of the vaccine (could be custom)
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private String siteId;
    private AppointmentStatus status;
    
    public enum AppointmentStatus {
        REQUESTED,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
    
    public Appointment(String appointmentId, String childId, String vaccineName, LocalDate date, LocalTime time, String siteId, String notes) {
        this.appointmentId = appointmentId;
        this.childId = childId;
        this.vaccineName = vaccineName;
        this.date = date;
        this.time = time;
        this.siteId = siteId;
        this.notes = notes;
        this.status = AppointmentStatus.REQUESTED;
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    
    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
