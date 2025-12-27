package com.vaccinetracker.services;

import com.vaccinetracker.model.Vaccine;
import java.util.ArrayList;
import java.util.List;

/**
 * VaccineService class manages vaccine-related operations.
 * 
 * CONCEPT: Service Layer
 * - Manages the list of available vaccines
 * - Provides methods to add, find, and retrieve vaccines
 * - Preloads common vaccines
 */
public class VaccineService {
    
    // Store vaccines in a List
    private static List<Vaccine> vaccines = new ArrayList<>();
    
    /**
     * Constructor - initializes the vaccines list and preloads common vaccines.
     */
    public VaccineService() {
        if (vaccines.isEmpty()) {
            preloadVaccines();  // Add standard vaccines
        }
    }
    
    /**
     * Preload standard vaccines that are commonly used.
     * In a real application, this would load from a database.
     */
    private void preloadVaccines() {
        // Birth vaccines
        addVaccine("VAC001", "BCG", "Bacillus Calmette-Guérin - protects against tuberculosis", 0);
        addVaccine("VAC002", "Hepatitis B (Birth)", "First dose of Hepatitis B vaccine", 0);
        
        // 6 weeks
        addVaccine("VAC003", "DPT", "Diphtheria, Pertussis, Tetanus - first dose", 42);
        addVaccine("VAC004", "Polio (Oral)", "Oral Polio Vaccine - first dose", 42);
        addVaccine("VAC005", "Hepatitis B (6 weeks)", "Second dose of Hepatitis B", 42);
        
        // 10 weeks
        addVaccine("VAC006", "DPT (2nd)", "Diphtheria, Pertussis, Tetanus - second dose", 70);
        addVaccine("VAC007", "Polio (Oral 2nd)", "Oral Polio Vaccine - second dose", 70);
        
        // 14 weeks
        addVaccine("VAC008", "DPT (3rd)", "Diphtheria, Pertussis, Tetanus - third dose", 98);
        addVaccine("VAC009", "Polio (Oral 3rd)", "Oral Polio Vaccine - third dose", 98);
        
        // 9 months
        addVaccine("VAC010", "Measles", "Measles vaccine", 270);
        
        // 18 months
        addVaccine("VAC011", "DPT Booster", "Diphtheria, Pertussis, Tetanus booster", 540);
        addVaccine("VAC012", "Polio Booster", "Oral Polio Vaccine booster", 540);
    }
    
    /**
     * Get all available vaccines.
     * 
     * @return List of all vaccines
     */
    public List<Vaccine> getAllVaccines() {
        return new ArrayList<>(vaccines);
    }

    /**
     * Add a new vaccine to the system.
     * 
     * @param vaccineId Unique identifier
     * @param name Vaccine name
     * @param description What the vaccine prevents
     * @param recommendedAgeInDays Recommended age in days
     * @return The created Vaccine object
     */
    public Vaccine addVaccine(String vaccineId, String name, String description, int recommendedAgeInDays) {
        Vaccine vaccine = new Vaccine(vaccineId, name, description, recommendedAgeInDays);
        vaccines.add(vaccine);
        return vaccine;
    }

    /**
     * Create a new vaccine with an auto-generated ID.
     * 
     * @param name Vaccine name
     * @return The created Vaccine object
     */
    public Vaccine createVaccine(String name) {
        int nextId = vaccines.size() + 1;
        String vaccineId = String.format("VAC%03d", nextId);
        // Ensure uniqueness
        while (getVaccineById(vaccineId) != null) {
            nextId++;
            vaccineId = String.format("VAC%03d", nextId);
        }
        return addVaccine(vaccineId, name, "Custom added vaccine", 0);
    }
    
    /**
     * Find a vaccine by its vaccine ID.
     * 
     * @param vaccineId The vaccine ID to search for
     * @return Vaccine object if found, null otherwise
     */
    public Vaccine getVaccineById(String vaccineId) {
        for (Vaccine vaccine : vaccines) {
            if (vaccine.getVaccineId().equals(vaccineId)) {
                return vaccine;
            }
        }
        return null;
    }
    
    /**
     * Find vaccines by name (case-insensitive partial match).
     * 
     * @param name The name to search for
     * @return List of matching vaccines
     */
    public List<Vaccine> getVaccinesByName(String name) {
        List<Vaccine> matchingVaccines = new ArrayList<>();
        String searchName = name.toLowerCase();
        for (Vaccine vaccine : vaccines) {
            if (vaccine.getName().toLowerCase().contains(searchName)) {
                matchingVaccines.add(vaccine);
            }
        }
        return matchingVaccines;
    }
    
    /**
     * Get all available vaccines.
     * 
     * @return List of all vaccines
     */
    public List<Vaccine> getAvailableVaccines() {
        return new ArrayList<>(vaccines);  // Return a copy
    }
    
    /**
     * Get vaccines that are due for a child of a specific age.
     * 
     * CONCEPT: Filtering & Condition Checking
     * - Filters vaccines based on child's age
     * 
     * @param childAgeInDays Child's age in days
     * @return List of vaccines that the child is eligible for
     */
    public List<Vaccine> getVaccinesDueForAge(int childAgeInDays) {
        List<Vaccine> dueVaccines = new ArrayList<>();
        for (Vaccine vaccine : vaccines) {
            if (vaccine.isEligible(childAgeInDays)) {
                dueVaccines.add(vaccine);
            }
        }
        return dueVaccines;
    }
    
    /**
     * Get the total number of vaccines in the system.
     * 
     * @return Number of vaccines
     */
    public int getVaccineCount() {
        return vaccines.size();
    }
}

