package com.vaccinetracker.services;

import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.Vaccine;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * VaccinationService class manages vaccination records and schedules.
 * 
 * CONCEPT: Service Layer & Business Logic
 * - Handles vaccination record management
 * - Creates vaccination schedules for children
 * - Finds upcoming and overdue vaccinations
 * - Works with ChildService and VaccineService
 */
public class VaccinationService {
    
    // Store vaccination records in a List
    private List<VaccinationRecord> vaccinationRecords;
    private int nextRecordId = 1;  // Auto-increment ID generator
    
    // Reference to other services (composition/dependency)
    private ChildService childService;
    private VaccineService vaccineService;
    
    /**
     * Constructor - initializes the vaccination records list.
     * 
     * @param childService Reference to ChildService
     * @param vaccineService Reference to VaccineService
     */
    public VaccinationService(ChildService childService, VaccineService vaccineService) {
        this.vaccinationRecords = new ArrayList<>();
        this.childService = childService;
        this.vaccineService = vaccineService;
    }
    
    /**
     * Manually add a vaccination record for a child.
     * 
     * @param childId The child's ID
     * @param vaccineId The vaccine ID
     * @param nextDueDate The due date for the vaccine
     * @param vaccinationSiteId The site ID where the vaccine is administered
     * @return The created VaccinationRecord
     */
    public VaccinationRecord addVaccinationRecord(String childId, String vaccineId, LocalDate nextDueDate, String vaccinationSiteId) {
        String recordId = "REC" + String.format("%06d", nextRecordId++);
        VaccinationRecord record = new VaccinationRecord(recordId, childId, vaccineId, nextDueDate, vaccinationSiteId);
        vaccinationRecords.add(record);
        return record;
    }

    /**
     * Create a vaccination schedule for a child based on their age.
     * This is called when a child is registered.
     * 
     * CONCEPT: Complex Business Logic
     * - Calculates which vaccines are due
     * - Creates records for each vaccine
     * - Sets appropriate due dates
     * 
     * @param childId The child's ID
     * @return List of created vaccination records
     */
    public List<VaccinationRecord> createScheduleForChild(String childId) {
        List<VaccinationRecord> schedule = new ArrayList<>();
        
        // Get the child to find their age
        Child child = childService.getChildById(childId);
        if (child == null) {
            return schedule;  // Child not found, return empty list
        }
        
        // Get vaccines that are due for this child's age
        long childAgeInDays = child.getAgeInDays();
        List<Vaccine> dueVaccines = vaccineService.getVaccinesDueForAge((int) childAgeInDays);
        
        // Create a vaccination record for each due vaccine
        for (Vaccine vaccine : dueVaccines) {
            // Calculate due date: child's birth date + recommended age
            LocalDate dueDate = child.getDateOfBirth().plusDays(vaccine.getRecommendedAgeInDays());
            
            // Only create record if due date hasn't passed or is today
            if (!dueDate.isBefore(LocalDate.now())) {
                String recordId = "REC" + String.format("%06d", nextRecordId++);
                VaccinationRecord record = new VaccinationRecord(recordId, childId, 
                                                                vaccine.getVaccineId(), dueDate);
                vaccinationRecords.add(record);
                schedule.add(record);
            }
        }
        
        return schedule;
    }
    
    /**
     * Get all vaccination records for a specific child.
     * 
     * @param childId The child's ID
     * @return List of vaccination records for that child
     */
    public List<VaccinationRecord> getRecordsByChild(String childId) {
        List<VaccinationRecord> childRecords = new ArrayList<>();
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getChildId().equals(childId)) {
                childRecords.add(record);
            }
        }
        return childRecords;
    }
    
    /**
     * Get upcoming vaccinations for a specific child.
     * Returns only PENDING records with future due dates.
     * 
     * CONCEPT: Filtering with Multiple Conditions
     * 
     * @param childId The child's ID
     * @return List of upcoming vaccination records
     */
    public List<VaccinationRecord> getUpcomingVaccinations(String childId) {
        List<VaccinationRecord> upcoming = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getChildId().equals(childId) &&
                record.getStatus() == VaccinationRecord.VaccinationStatus.PENDING &&
                record.getNextDueDate() != null &&
                (record.getNextDueDate().isAfter(today) || record.getNextDueDate().equals(today))) {
                upcoming.add(record);
            }
        }
        
        return upcoming;
    }
    
    /**
     * Get overdue vaccinations for a specific child.
     * Returns PENDING records with past due dates.
     * 
     * @param childId The child's ID
     * @return List of overdue vaccination records
     */
    public List<VaccinationRecord> getOverdueVaccinations(String childId) {
        List<VaccinationRecord> overdue = new ArrayList<>();
        
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getChildId().equals(childId) && record.isOverdue()) {
                overdue.add(record);
            }
        }
        
        return overdue;
    }
    
    /**
     * Mark a vaccination as completed.
     * Updates the record with the administration date.
     * 
     * @param recordId The vaccination record ID
     * @param dateAdministered The date when vaccine was given (null for today)
     * @return true if record was found and updated, false otherwise
     */
    public boolean markVaccinationCompleted(String recordId, LocalDate dateAdministered) {
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getRecordId().equals(recordId)) {
                LocalDate adminDate = (dateAdministered != null) ? dateAdministered : LocalDate.now();
                record.setDateAdministered(adminDate);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all vaccination records in the system.
     * 
     * @return List of all vaccination records
     */
    public List<VaccinationRecord> getAllRecords() {
        return new ArrayList<>(vaccinationRecords);  // Return a copy
    }
    
    /**
     * Get vaccination records by status.
     * 
     * @param status The status to filter by (PENDING or COMPLETED)
     * @return List of records with that status
     */
    public List<VaccinationRecord> getRecordsByStatus(VaccinationRecord.VaccinationStatus status) {
        List<VaccinationRecord> filteredRecords = new ArrayList<>();
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getStatus() == status) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
    
    /**
     * Get the total number of vaccination records.
     * 
     * @return Number of records
     */
    public int getRecordCount() {
        return vaccinationRecords.size();
    }
}

