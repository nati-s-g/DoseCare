package com.vaccinetracker.model;

/**
 * Vaccine class representing a type of vaccine available in the system.
 * 
 * CONCEPT: Domain Model
 * - Represents a vaccine type (e.g., BCG, Polio, Measles)
 * - Contains information about when the vaccine should be administered
 * - Will be used in VaccinationRecord to track which vaccine was given
 */
public class Vaccine {
    
    private String vaccineId;           // Unique identifier
    private String name;                // Vaccine name (e.g., "BCG", "Polio", "DPT")
    private String description;         // Description of what the vaccine prevents
    private int recommendedAgeInDays;   // Age (in days) when vaccine should be given
    
    /**
     * Constructor to create a Vaccine.
     * 
     * @param vaccineId Unique identifier
     * @param name Vaccine name
     * @param description What disease it prevents
     * @param recommendedAgeInDays Recommended age in days (e.g., 0 for birth, 42 for 6 weeks)
     */
    public Vaccine(String vaccineId, String name, String description, int recommendedAgeInDays) {
        this.vaccineId = vaccineId;
        this.name = name;
        this.description = description;
        this.recommendedAgeInDays = recommendedAgeInDays;
    }
    
    // Getter methods
    public String getVaccineId() {
        return vaccineId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getRecommendedAgeInDays() {
        return recommendedAgeInDays;
    }
    
    // Setter methods
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setRecommendedAgeInDays(int recommendedAgeInDays) {
        this.recommendedAgeInDays = recommendedAgeInDays;
    }
    
    /**
     * Check if a child is old enough for this vaccine.
     * 
     * @param childAgeInDays Child's age in days
     * @return true if child is old enough, false otherwise
     */
    public boolean isEligible(int childAgeInDays) {
        return childAgeInDays >= recommendedAgeInDays;
    }
    
    /**
     * Override toString to display vaccine information.
     * 
     * @return Formatted string with vaccine details
     */
    @Override
    public String toString() {
        return "Vaccine{" +
                "vaccineId='" + vaccineId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", recommendedAgeInDays=" + recommendedAgeInDays +
                '}';
    }
}

