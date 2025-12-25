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
    private String gender;          // Gender of the child
    private String guardianName;    // Name of the guardian
    private String guardianContact; // Contact number of the guardian
    
    /**
     * Constructor to create a new Child.
     * 
     * @param childId Unique identifier
     * @param name Child's name
     * @param dateOfBirth Date of birth
     * @param parentId Parent's user ID
     * @param hospitalId Hospital identifier
     * @param gender Gender of the child
     * @param guardianName Name of the guardian
     * @param guardianContact Contact number of the guardian
     */
    public Child(String childId, String name, LocalDate dateOfBirth, String parentId, String hospitalId, String gender, String guardianName, String guardianContact) {
        this.childId = childId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.parentId = parentId;
        this.hospitalId = hospitalId;
        this.gender = gender;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
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

    public String getGender() {
        return gender;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianContact() {
        return guardianContact;
    }
    
    // Setter methods
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public void setGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact;
    }
    
    /**
     * Calculate the child's age in years (or months/days logic can be added).
     * For display purposes, we might want a string representation.
     * 
     * @return Age in days
     */
    public long getAgeInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(dateOfBirth, LocalDate.now());
    }

    /**
     * Get a friendly age string (e.g., "2 years", "5 months").
     * @return Age string
     */
    public String getAgeString() {
        long days = getAgeInDays();
        if (days < 30) {
            return days + " days";
        } else if (days < 365) {
            return (days / 30) + " months";
        } else {
            return (days / 365) + " years";
        }
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

