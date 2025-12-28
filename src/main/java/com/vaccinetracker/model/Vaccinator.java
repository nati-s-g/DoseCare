package com.vaccinetracker.model;

/**
 * Vaccinator class representing a medical professional who administers vaccines.
 */
public class Vaccinator extends User {
    
    private String vaccinatorId;    // Professional ID (cannot be changed)
    private String siteId;          // Assigned vaccination site
    private String address;         // Home address
    
    /**
     * Constructor for Vaccinator.
     * 
     * @param userId Unique user identifier
     * @param username Username
     * @param password Password
     * @param name Full name
     * @param contactInfo Contact information
     * @param vaccinatorId Professional Vaccinator ID
     * @param siteId Assigned Site ID
     * @param address Address
     */
    public Vaccinator(String userId, String username, String password, String name, String contactInfo, String vaccinatorId, String siteId, String address) {
        super(userId, username, password, name, contactInfo, "VACCINATOR");
        this.vaccinatorId = vaccinatorId;
        this.siteId = siteId;
        this.address = address;
    }
    
    public String getVaccinatorId() {
        return vaccinatorId;
    }
    
    // No setter for vaccinatorId as it shouldn't be changed
    
    public String getSiteId() {
        return siteId;
    }
    
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String getDashboardInfo() {
        return "Vaccinator Dashboard - " + siteId;
    }
}
