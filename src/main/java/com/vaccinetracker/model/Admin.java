package com.vaccinetracker.model;

/**
 * Admin class representing a hospital administrator or health worker.
 * 
 * CONCEPT: Inheritance
 * - Admin extends (inherits from) User
 * - Gets all fields and methods from User class
 * - Must implement abstract methods from User
 * - Can have its own unique methods
 */
public class Admin extends User {
    
    private String hospitalId;      // Which hospital they work at
    private String department;      // Department name (e.g., "Pediatrics")
    
    /**
     * Constructor for Admin.
     * Calls parent class constructor using super()
     * 
     * @param userId Unique identifier
     * @param name Full name
     * @param contactInfo Contact information
     * @param hospitalId Hospital identifier
     * @param department Department name
     */
    public Admin(String userId, String name, String contactInfo, String hospitalId, String department) {
        // Call parent class constructor - this sets userId, name, contactInfo, role
        super(userId, name, contactInfo, "ADMIN");
        this.hospitalId = hospitalId;
        this.department = department;
    }
    
    // Getters and setters for Admin-specific fields
    public String getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Implementation of abstract method from User class.
     * 
     * CONCEPT: Method Overriding
     * - This method was declared in User but not implemented
     * - Admin provides its own implementation
     * - Each child class can have different behavior (polymorphism)
     * 
     * @return Description of admin capabilities
     */
    @Override
    public String getDashboardInfo() {
        return "Admin Dashboard - You can: Register children, Manage vaccines, " +
               "Manage vaccination sites, View all records, Monitor community health";
    }
    
    /**
     * Admin-specific method: Register a new child.
     * Only admins can perform this action.
     * 
     * @param childName Name of the child
     * @param dateOfBirth Date of birth
     * @param parentName Parent's name
     * @return Confirmation message
     */
    public String registerChild(String childName, String dateOfBirth, String parentName) {
        return "Child " + childName + " registered successfully for parent " + parentName;
    }
    
    /**
     * Admin-specific method: Add a new vaccine to the system.
     * 
     * @param vaccineName Name of the vaccine
     * @return Confirmation message
     */
    public String addVaccine(String vaccineName) {
        return "Vaccine " + vaccineName + " added to system successfully";
    }
    
    /**
     * Admin-specific method: Add a vaccination site.
     * 
     * @param siteName Name of the vaccination site
     * @param location Location address
     * @return Confirmation message
     */
    public String addVaccinationSite(String siteName, String location) {
        return "Vaccination site " + siteName + " at " + location + " added successfully";
    }
    
    /**
     * Override toString to include Admin-specific information.
     * 
     * @return Formatted string with admin details
     */
    @Override
    public String toString() {
        return "Admin{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", contactInfo='" + getContactInfo() + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

