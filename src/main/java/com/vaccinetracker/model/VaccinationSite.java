package com.vaccinetracker.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * VaccinationSite class representing a location where vaccinations are administered.
 * 
 * CONCEPT: Composition & Collections
 * - Contains a Map of vaccines and their stock (composition)
 * - Demonstrates HashMap usage
 * - Represents a real-world location with multiple vaccines available
 */
public class VaccinationSite {
    
    private String siteId;                      // Unique identifier
    private String name;                        // Site name (e.g., "City Hospital", "Community Center")
    private String location;                    // Physical address
    private String contactInfo;                 // Phone number or email
    private Map<String, Integer> vaccineStock;  // Map of vaccine IDs to stock quantity
    private Map<String, LocalDate> vaccineExpiry; // Map of vaccine IDs to expiry date
    
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
        this.vaccineStock = new HashMap<>();  // Initialize empty map
        this.vaccineExpiry = new HashMap<>();
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
    
    public Map<String, Integer> getVaccineStock() {
        return vaccineStock;
    }

    /**
     * Get the stock level for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @return The stock quantity, or 0 if not found
     */
    public int getStock(String vaccineId) {
        return vaccineStock.getOrDefault(vaccineId, 0);
    }

    /**
     * Get the expiry date for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @return The expiry date, or null if not found
     */
    public LocalDate getExpiryDate(String vaccineId) {
        return vaccineExpiry.get(vaccineId);
    }

    /**
     * Update the stock level for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @param quantity The new quantity
     */
    public void updateStock(String vaccineId, int quantity) {
        vaccineStock.put(vaccineId, quantity);
    }

    /**
     * Update the stock level and expiry date for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @param quantity The new quantity
     * @param expiryDate The expiry date
     */
    public void updateStock(String vaccineId, int quantity, LocalDate expiryDate) {
        vaccineStock.put(vaccineId, quantity);
        if (expiryDate != null) {
            vaccineExpiry.put(vaccineId, expiryDate);
        }
    }

    /**
     * Add stock for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @param quantity The quantity to add
     */
    public void addStock(String vaccineId, int quantity) {
        int currentStock = getStock(vaccineId);
        vaccineStock.put(vaccineId, currentStock + quantity);
    }

    /**
     * Add stock for a specific vaccine with expiry date.
     * 
     * @param vaccineId The vaccine ID
     * @param quantity The quantity to add
     * @param expiryDate The expiry date
     */
    public void addStock(String vaccineId, int quantity, LocalDate expiryDate) {
        int currentStock = getStock(vaccineId);
        vaccineStock.put(vaccineId, currentStock + quantity);
        if (expiryDate != null) {
            vaccineExpiry.put(vaccineId, expiryDate);
        }
    }

    /**
     * Remove stock for a specific vaccine.
     * 
     * @param vaccineId The vaccine ID
     * @param quantity The quantity to remove
     * @return true if successful, false if insufficient stock
     */
    public boolean removeStock(String vaccineId, int quantity) {
        int currentStock = getStock(vaccineId);
        if (currentStock >= quantity) {
            vaccineStock.put(vaccineId, currentStock - quantity);
            return true;
        }
        return false;
    }

    /**
     * Check if the site has a specific vaccine available (stock > 0).
     * 
     * @param vaccineId The vaccine ID to check
     * @return true if available, false otherwise
     */
    public boolean hasVaccine(String vaccineId) {
        return getStock(vaccineId) > 0;
    }

    /**
     * Add a vaccine to the site (initialize with 0 stock if not present).
     * Kept for backward compatibility, but now uses stock map.
     * 
     * @param vaccineId The vaccine ID to add
     */
    public void addVaccine(String vaccineId) {
        if (!vaccineStock.containsKey(vaccineId)) {
            vaccineStock.put(vaccineId, 0);
        }
    }

    /**
     * Remove a vaccine from the site completely.
     * 
     * @param vaccineId The vaccine ID to remove
     * @return true if removed, false otherwise
     */
    public boolean removeVaccine(String vaccineId) {
        vaccineExpiry.remove(vaccineId);
        return vaccineStock.remove(vaccineId) != null;
    }

    /**
     * Get the number of vaccines tracked at this site.
     * 
     * @return Number of vaccines in inventory
     */
    public int getVaccineCount() {
        return vaccineStock.size();
    }
    
    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}

