package com.vaccinetracker.services;

import com.vaccinetracker.model.VaccinationSite;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * VaccinationSiteService class manages vaccination site operations.
 * 
 * CONCEPT: Service Layer
 * - Manages vaccination sites (locations where vaccines are administered)
 * - Provides methods to create, find, and manage sites
 * - Handles vaccine availability at each site
 */
public class VaccinationSiteService {
    
    // Store vaccination sites in a List
    private static List<VaccinationSite> vaccinationSites = new ArrayList<>();
    private static int nextSiteId = 1;  // Auto-increment ID generator
    
    /**
     * Constructor - initializes the sites list.
     */
    public VaccinationSiteService() {
        // No longer automatically initializing sample sites here
        // This is now handled by StorageService on first run
    }
    
    /**
     * Initialize with some sample vaccination sites for demonstration.
     */
    public static void initializeSampleSites() {
        if (!vaccinationSites.isEmpty()) return;

        // Addis Ababa Hospitals
        VaccinationSite site1 = createSite("Black Lion Hospital (Tikur Anbessa)", 
                                           "Churchill Ave, Addis Ababa", 
                                           "Phone: +251 11 551 1211");
        initializeStock(site1);
        
        VaccinationSite site2 = createSite("St. Paul's Hospital Millennium Medical College", 
                                           "Swaziland St, Addis Ababa", 
                                           "Phone: +251 11 275 0125");
        initializeStock(site2);
        
        VaccinationSite site3 = createSite("Zewditu Memorial Hospital", 
                                           "Churchill Ave, Addis Ababa", 
                                           "Phone: +251 11 551 8085");
        initializeStock(site3);

        VaccinationSite site4 = createSite("Yekatit 12 Hospital", 
                                           "6 Kilo, Addis Ababa", 
                                           "Phone: +251 11 155 3065");
        initializeStock(site4);

        VaccinationSite site5 = createSite("Alert Hospital", 
                                           "Jimma Road, Addis Ababa", 
                                           "Phone: +251 11 321 1344");
        initializeStock(site5);
    }

    private static void initializeStock(VaccinationSite site) {
        // Initialize with random stock for all 12 vaccines
        for (int i = 1; i <= 12; i++) {
            String vaccineId = String.format("VAC%03d", i);
            int stock = 50 + (int)(Math.random() * 100); // Random stock between 50 and 150
            // Random expiry date between 6 months and 2 years from now
            LocalDate expiry = LocalDate.now().plusDays(180 + (int)(Math.random() * 550));
            site.updateStock(vaccineId, stock, expiry);
        }
    }
    
    /**
     * Create a new vaccination site.
     * 
     * @param name Site name
     * @param location Physical address
     * @param contactInfo Contact information
     * @return The created VaccinationSite object
     */
    public static VaccinationSite createSite(String name, String location, String contactInfo) {
        String siteId = "SITE" + String.format("%04d", nextSiteId++);
        VaccinationSite site = new VaccinationSite(siteId, name, location, contactInfo);
        vaccinationSites.add(site);
        return site;
    }
    
    /**
     * Find a vaccination site by its site ID.
     * 
     * @param siteId The site ID to search for
     * @return VaccinationSite object if found, null otherwise
     */
    public VaccinationSite getSiteById(String siteId) {
        for (VaccinationSite site : vaccinationSites) {
            if (site.getSiteId().equals(siteId)) {
                return site;
            }
        }
        return null;
    }
    
    /**
     * Find sites by name (case-insensitive partial match).
     * 
     * @param name The name to search for
     * @return List of matching sites
     */
    public List<VaccinationSite> getSitesByName(String name) {
        List<VaccinationSite> matchingSites = new ArrayList<>();
        String searchName = name.toLowerCase();
        for (VaccinationSite site : vaccinationSites) {
            if (site.getName().toLowerCase().contains(searchName)) {
                matchingSites.add(site);
            }
        }
        return matchingSites;
    }
    
    /**
     * Get all vaccination sites.
     * 
     * @return List of all vaccination sites
     */
    public List<VaccinationSite> getAllSites() {
        return new ArrayList<>(vaccinationSites);  // Return a copy
    }
    
    /**
     * Find sites that have a specific vaccine available.
     * 
     * CONCEPT: Filtering Collections
     * 
     * @param vaccineId The vaccine ID to search for
     * @return List of sites that have this vaccine
     */
    public List<VaccinationSite> getSitesWithVaccine(String vaccineId) {
        List<VaccinationSite> sitesWithVaccine = new ArrayList<>();
        for (VaccinationSite site : vaccinationSites) {
            if (site.hasVaccine(vaccineId)) {
                sitesWithVaccine.add(site);
            }
        }
        return sitesWithVaccine;
    }
    
    /**
     * Update stock for a vaccine at a site.
     * 
     * @param siteId The site ID
     * @param vaccineId The vaccine ID
     * @param quantity The new quantity
     * @return true if site found, false otherwise
     */
    public boolean updateSiteStock(String siteId, String vaccineId, int quantity) {
        VaccinationSite site = getSiteById(siteId);
        if (site != null) {
            site.updateStock(vaccineId, quantity);
            return true;
        }
        return false;
    }

    /**
     * Add a vaccine to a site's available vaccines list.
     * 
     * @param siteId The site ID
     * @param vaccineId The vaccine ID to add
     * @return true if site was found and vaccine was added, false otherwise
     */
    public boolean addVaccineToSite(String siteId, String vaccineId) {
        VaccinationSite site = getSiteById(siteId);
        if (site != null) {
            site.addVaccine(vaccineId);
            return true;
        }
        return false;
    }
    
    /**
     * Remove a vaccine from a site's available vaccines list.
     * 
     * @param siteId The site ID
     * @param vaccineId The vaccine ID to remove
     * @return true if site was found and vaccine was removed, false otherwise
     */
    public boolean removeVaccineFromSite(String siteId, String vaccineId) {
        VaccinationSite site = getSiteById(siteId);
        if (site != null) {
            return site.removeVaccine(vaccineId);
        }
        return false;
    }
    
    /**
     * Remove a vaccination site by its site ID.
     * 
     * @param siteId The site ID to remove
     * @return true if site was found and removed, false otherwise
     */
    public boolean deleteSite(String siteId) {
        return vaccinationSites.removeIf(site -> site.getSiteId().equals(siteId));
    }

    /**
     * Get the total number of vaccination sites.
     * 
     * @return Number of sites
     */
    public int getSiteCount() {
        return vaccinationSites.size();
    }

    // Static access for StorageService
    public static List<VaccinationSite> getAllData() {
        return vaccinationSites;
    }

    public static void setAllData(List<VaccinationSite> data) {
        vaccinationSites = data;
    }
}

