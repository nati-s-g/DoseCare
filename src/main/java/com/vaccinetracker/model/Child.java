package com.vaccinetracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Child class representing a newborn or child in the vaccination tracking system.
 * 
 * CONCEPT: Domain Model / Entity Class
 * - Represents a real-world entity (a child)
 * - Contains data about the child
 * - Will be associated with vaccination records
 */
public class Child {
    
    private String childId;         // Unique identifier for the child
    private String name;            // Child's full name
    private LocalDate dateOfBirth;  // Date of birth (using Java 8 LocalDate)
    private String parentId;        // ID of the parent/guardian
    private String hospitalId;      // Hospital where registered
    
    /**
     * Constructor to create a new Child.
     * 
     * @param childId Unique identifier
     * @param name Child's name
     * @param dateOfBirth Date of birth
     * @param parentId Parent's user ID
     * @param hospitalId Hospital identifier
     */
    public Child(String childId, String name, LocalDate dateOfBirth, String parentId, String hospitalId) {
        this.childId = childId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.parentId = parentId;
        this.hospitalId = hospitalId;
    }
    
    // Getter methods
    public String getChildId() {
        return childId;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public String getHospitalId() {
        return hospitalId;
    }
    
    // Setter methods
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    /**
     * Calculate the child's age in days.
     * Useful for determining which vaccines are due.
     * 
     * @return Age in days
     */
    public long getAgeInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(dateOfBirth, LocalDate.now());
    }
    
    /**
     * Get formatted date of birth as string.
     * 
     * @return Formatted date string
     */
    public String getDateOfBirthString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateOfBirth.format(formatter);
    }
    
    /**
     * Override toString to display child information.
     * 
     * @return Formatted string with child details
     */
    @Override
    public String toString() {
        return "Child{" +
                "childId='" + childId + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + getDateOfBirthString() +
                ", parentId='" + parentId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", ageInDays=" + getAgeInDays() +
                '}';
    }
}

