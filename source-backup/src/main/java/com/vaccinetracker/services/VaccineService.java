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
     * Constructor - initializes the vaccines list.
     */
    public VaccineService() {
        // No longer automatically preloading vaccines here
        // This is now handled by StorageService on first run
    }
    
    /**
     * Preload standard vaccines that are commonly used.
     * In a real application, this would load from a database.
     */
    public static void preloadVaccines() {
        // Birth vaccines
        addVaccineIfMissing("VAC001", "BCG", "Bacillus Calmette-Guérin - protects against tuberculosis", 0);
        addVaccineIfMissing("VAC002", "Hepatitis B (Birth)", "First dose of Hepatitis B vaccine", 0);
        
        // 6 weeks
        addVaccineIfMissing("VAC003", "DPT", "Diphtheria, Pertussis, Tetanus - first dose", 42);
        addVaccineIfMissing("VAC004", "Polio (Oral)", "Oral Polio Vaccine - first dose", 42);
        addVaccineIfMissing("VAC005", "Hepatitis B (6 weeks)", "Second dose of Hepatitis B", 42);
        
        // 8 weeks (56 days)
        addVaccineIfMissing("VAC020", "6-in-1 (1st dose)", "Diphtheria, hepatitis B, Hib, polio, tetanus, whooping cough", 56);
        addVaccineIfMissing("VAC021", "Rotavirus (1st dose)", "Rotavirus vaccine", 56);
        addVaccineIfMissing("VAC022", "MenB (1st dose)", "Meningococcal group B bacteria", 56);

        // 10 weeks
        addVaccineIfMissing("VAC006", "DPT (2nd)", "Diphtheria, Pertussis, Tetanus - second dose", 70);
        addVaccineIfMissing("VAC007", "Polio (Oral 2nd)", "Oral Polio Vaccine - second dose", 70);
        
        // 12 weeks (84 days)
        addVaccineIfMissing("VAC023", "6-in-1 (2nd dose)", "Diphtheria, hepatitis B, Hib, polio, tetanus, whooping cough", 84);
        addVaccineIfMissing("VAC024", "MenB (2nd dose)", "Meningococcal group B bacteria", 84);
        addVaccineIfMissing("VAC025", "Rotavirus (2nd dose)", "Rotavirus vaccine", 84);

        // 14 weeks
        addVaccineIfMissing("VAC008", "DPT (3rd)", "Diphtheria, Pertussis, Tetanus - third dose", 98);
        addVaccineIfMissing("VAC009", "Polio (Oral 3rd)", "Oral Polio Vaccine - third dose", 98);
        
        // 16 weeks (112 days)
        addVaccineIfMissing("VAC026", "6-in-1 (3rd dose)", "Diphtheria, hepatitis B, Hib, polio, tetanus, whooping cough", 112);
        addVaccineIfMissing("VAC027", "Pneumococcal (1st dose)", "Pneumococcal vaccine (PCV)", 112);

        // 9 months
        addVaccineIfMissing("VAC010", "Measles", "Measles vaccine", 270);
        
        // 1 year (365 days)
        addVaccineIfMissing("VAC028", "MMR (1st dose)", "Measles, Mumps and Rubella", 365);
        addVaccineIfMissing("VAC029", "Pneumococcal (2nd dose)", "Pneumococcal vaccine (PCV)", 365);
        addVaccineIfMissing("VAC030", "MenB (3rd dose)", "Meningococcal group B bacteria", 365);
        addVaccineIfMissing("VAC031", "Hib/MenC (1st dose)", "Haemophilus influenzae type b (Hib) and meningitis C", 365);

        // 18 months (540/548 days)
        addVaccineIfMissing("VAC011", "DPT Booster", "Diphtheria, Pertussis, Tetanus booster", 540);
        addVaccineIfMissing("VAC012", "Polio Booster", "Oral Polio Vaccine booster", 540);
        addVaccineIfMissing("VAC032", "6-in-1 (4th dose)", "Diphtheria, hepatitis B, Hib, polio, tetanus, whooping cough", 548);
        addVaccineIfMissing("VAC033", "MMR (2nd dose)", "Measles, Mumps and Rubella", 548);
    }
    
    private static void addVaccineIfMissing(String id, String name, String desc, int age) {
        boolean exists = false;
        for (Vaccine v : vaccines) {
            if (v.getVaccineId().equals(id)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addVaccine(id, name, desc, age);
        }
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
    public static Vaccine addVaccine(String vaccineId, String name, String description, int recommendedAgeInDays) {
        Vaccine vaccine = new Vaccine(vaccineId, name, description, recommendedAgeInDays);
        vaccines.add(vaccine);
        StorageService.saveAll();
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

    // Static access for StorageService
    public static List<Vaccine> getAllData() {
        return vaccines;
    }

    public static void setAllData(List<Vaccine> data) {
        vaccines = data;
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

