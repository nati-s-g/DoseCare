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
    private String fatherName;      // Name of the father (Guardian 1)
    private String fatherContact;   // Contact number of the father
    private String motherName;      // Name of the mother (Guardian 2)
    private String motherContact;   // Contact number of the mother
    
    /**
     * Constructor to create a new Child.
     * 
     * @param childId Unique identifier
     * @param name Child's name
     * @param dateOfBirth Date of birth
     * @param parentId Parent's user ID
     * @param hospitalId Hospital identifier
     * @param gender Gender of the child
     * @param fatherName Name of the father
     * @param fatherContact Contact number of the father
     * @param motherName Name of the mother
     * @param motherContact Contact number of the mother
     */
    public Child(String childId, String name, LocalDate dateOfBirth, String parentId, String hospitalId, String gender, String fatherName, String fatherContact, String motherName, String motherContact) {
        this.childId = childId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.parentId = parentId;
        this.hospitalId = hospitalId;
        this.gender = gender;
        this.fatherName = fatherName;
        this.fatherContact = fatherContact;
        this.motherName = motherName;
        this.motherContact = motherContact;
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

    public String getFatherName() {
        return fatherName;
    }

    public String getFatherContact() {
        return fatherContact;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getMotherContact() {
        return motherContact;
    }

    // Legacy getter for compatibility - returns Father's name
    public String getGuardianName() {
        return fatherName;
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

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setFatherContact(String fatherContact) {
        this.fatherContact = fatherContact;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public void setMotherContact(String motherContact) {
        this.motherContact = motherContact;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

