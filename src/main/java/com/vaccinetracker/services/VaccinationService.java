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
    private static List<VaccinationRecord> vaccinationRecords = new ArrayList<>();
    private static int nextRecordId = 1;  // Auto-increment ID generator
    
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
        // vaccinationRecords is static, so no need to initialize here if already done
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

    // Static access for StorageService
    public static List<VaccinationRecord> getAllData() {
        return vaccinationRecords;
    }

    public static void setAllData(List<VaccinationRecord> data) {
        vaccinationRecords = data;
    }

    public static void initializeDummyData() {
        // Check if we already have the demo pending records
        boolean hasPendingRecords = false;
        for (VaccinationRecord r : vaccinationRecords) {
            if (r.getStatus() == VaccinationRecord.VaccinationStatus.PENDING && "SITE-001".equals(r.getVaccinationSiteId())) {
                hasPendingRecords = true;
                break;
            }
        }
        
        if (hasPendingRecords) return;
        
        // Ensure children exist first
        if (ChildService.getAllData().isEmpty()) {
            ChildService.initializeDummyData();
        }
        
        List<Child> children = ChildService.getAllData();
        if (children.size() < 3) return;
        
        // Create pending vaccinations for the first 3 children
        for (int i = 0; i < 3; i++) {
            Child child = children.get(i);
            String recordId = "REC" + String.format("%06d", nextRecordId++);
            // Assign a pending vaccination (e.g., BCG or OPV)
            // Using "VAC001" (BCG) as a default example
            VaccinationRecord record = new VaccinationRecord(recordId, child.getChildId(), "VAC001", LocalDate.now().plusDays(7));
            record.setVaccinationSiteId("SITE-001"); // Assign to default site
            vaccinationRecords.add(record);
        }
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
     * Record a vaccination administration with site information.
     * 
     * @param recordId The vaccination record ID
     * @param dateAdministered The date when vaccine was given
     * @param siteId The site where it was administered
     * @return true if successful
     */
    public boolean recordVaccination(String recordId, LocalDate dateAdministered, String siteId) {
        for (VaccinationRecord record : vaccinationRecords) {
            if (record.getRecordId().equals(recordId)) {
                LocalDate adminDate = (dateAdministered != null) ? dateAdministered : LocalDate.now();
                record.setDateAdministered(adminDate);
                record.setVaccinationSiteId(siteId);
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

