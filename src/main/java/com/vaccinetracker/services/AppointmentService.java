package com.vaccinetracker.services;

import com.google.gson.reflect.TypeToken;
import com.vaccinetracker.model.Appointment;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing appointments.
 */
public class AppointmentService {
    
    private static final String DATA_FILE = "data/appointments.json";
    private StorageService storageService;
    private List<Appointment> appointments;
    
    public AppointmentService() {
        this.storageService = new StorageService();
        loadAppointments();
    }
    
    private void loadAppointments() {
        Type listType = new TypeToken<ArrayList<Appointment>>(){}.getType();
        appointments = storageService.loadData(DATA_FILE, listType);
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
    }
    
    private void saveAppointments() {
        storageService.saveData(DATA_FILE, appointments);
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
