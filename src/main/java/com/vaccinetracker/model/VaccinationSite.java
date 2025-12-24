package com.vaccinetracker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * VaccinationSite class representing a location where vaccinations are administered.
 * 
 * CONCEPT: Composition & Collections
 * - Contains a List of vaccines (composition)
 * - Demonstrates ArrayList usage
 * - Represents a real-world location with multiple vaccines available
 */
public class VaccinationSite {
    
    private String siteId;                      // Unique identifier
    private String name;                        // Site name (e.g., "City Hospital", "Community Center")
    private String location;                    // Physical address
    private String contactInfo;                 // Phone number or email
    private List<String> availableVaccineIds;   // List of vaccine IDs available at this site
    
    /**
     * Constructor to create a vaccination site.
     * 
     * @param siteId Unique identifier
     * @param name Site name
     * @param location Physical address
     * @param contactInfo Contact information
     */
    public VaccinationSite(String siteId, String name, String location, String contactInfo) {
        this.siteId = siteId;
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.availableVaccineIds = new ArrayList<>();  // Initialize empty list
    }
    
    // Getter methods
    public String getSiteId() {
        return siteId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public List<String> getAvailableVaccineIds() {
        return availableVaccineIds;  // Returns reference to the list
    }
    
    // Setter methods
    public void setName(String name) {
        this.name = name;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    /**
     * Add a vaccine to the list of available vaccines at this site.
     * 
     * CONCEPT: List Operations
     * - ArrayList.add() adds an element to the list
     * 
     * @param vaccineId The vaccine ID to add
     */
    public void addVaccine(String vaccineId) {
        if (!availableVaccineIds.contains(vaccineId)) {
            availableVaccineIds.add(vaccineId);
        }
    }
    
    /**
     * Remove a vaccine from the list of available vaccines.
     * 
     * @param vaccineId The vaccine ID to remove
     * @return true if vaccine was removed, false if it wasn't in the list
     */
    public boolean removeVaccine(String vaccineId) {
        return availableVaccineIds.remove(vaccineId);
    }
    
    /**
     * Check if a specific vaccine is available at this site.
     * 
     * @param vaccineId The vaccine ID to check
     * @return true if available, false otherwise
     */
    public boolean hasVaccine(String vaccineId) {
        return availableVaccineIds.contains(vaccineId);
    }
    
    /**
     * Get the number of vaccines available at this site.
     * 
     * @return Number of available vaccines
     */
    public int getVaccineCount() {
        return availableVaccineIds.size();
    }
    
    /**
     * Override toString to display site information.
     * 
     * @return Formatted string with site details
     */
    @Override
    public String toString() {
        return "VaccinationSite{" +
                "siteId='" + siteId + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", availableVaccines=" + availableVaccineIds.size() +
                '}';
    }
}

