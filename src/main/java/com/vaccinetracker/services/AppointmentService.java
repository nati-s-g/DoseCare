package com.vaccinetracker.services;

import com.vaccinetracker.model.Appointment;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing appointments.
 */
public class AppointmentService {
    
    // Static list to hold all appointments
    private static List<Appointment> appointments = new ArrayList<>();
    
    public AppointmentService() {
        // No initialization needed as data is loaded by StorageService
    }
    
    /**
     * Set all appointment data. Used by StorageService.
     */
    public static void setAllData(List<Appointment> data) {
        if (data != null) {
            appointments = data;
        }
    }
    
    /**
     * Get all appointment data. Used by StorageService.
     */
    public static List<Appointment> getAllData() {
        return appointments;
    }
    
    private void saveAppointments() {
        StorageService.saveData(StorageService.APPOINTMENTS_FILE, appointments);
    }
    
    public Appointment createAppointment(String childId, String vaccineName, java.time.LocalDate date, java.time.LocalTime time, String siteId, String notes) {
        String id = UUID.randomUUID().toString();
        Appointment appointment = new Appointment(id, childId, vaccineName, date, time, siteId, notes);
        appointments.add(appointment);
        saveAppointments();
        return appointment;
    }
    
    public List<Appointment> getAppointmentsByChild(String childId) {
        return appointments.stream()
                .filter(a -> a.getChildId().equals(childId))
                .collect(Collectors.toList());
    }
    
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }
    
    public void updateStatus(String appointmentId, Appointment.AppointmentStatus status) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus(status);
                saveAppointments();
                return;
            }
        }
    }
    
    /**
     * Get appointments for a specific parent's children.
     * Use ChildService to get the list of children IDs first.
     */
    public List<Appointment> getAppointmentsForChildren(List<String> childIds) {
        return appointments.stream()
                .filter(a -> childIds.contains(a.getChildId()))
                .collect(Collectors.toList());
    }
}
