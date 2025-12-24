package com.vaccinetracker.model;

/**
 * Parent class representing a parent/guardian in the system.
 * 
 * CONCEPT: Inheritance
 * - Parent extends (inherits from) User
 * - Gets all fields and methods from User class
 * - Must implement abstract methods from User
 * - Has different capabilities than Admin
 */
public class Parent extends User {
    
    private String address;         // Home address
    private int numberOfChildren;   // How many children they have registered
    
    /**
     * Constructor for Parent.
     * Calls parent class constructor using super()
     * 
     * @param userId Unique identifier
     * @param name Full name
     * @param contactInfo Contact information (email/phone)
     * @param address Home address
     */
    public Parent(String userId, String name, String contactInfo, String address) {
        // Call parent class constructor - sets userId, name, contactInfo, role
        super(userId, name, contactInfo, "PARENT");
        this.address = address;
        this.numberOfChildren = 0;  // Initially no children registered
    }
    
    // Getters and setters
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public int getNumberOfChildren() {
        return numberOfChildren;
    }
    
    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }
    
    /**
     * Implementation of abstract method from User class.
     * 
     * CONCEPT: Method Overriding & Polymorphism
     * - Same method name as in Admin, but different behavior
     * - Java will call the correct version based on object type
     * 
     * @return Description of parent capabilities
     */
    @Override
    public String getDashboardInfo() {
        return "Parent Dashboard - You can: View child vaccination schedules, " +
               "See upcoming vaccines, Find vaccination sites, View health alerts";
    }
    
    /**
     * Parent-specific method: View upcoming vaccinations.
     * Only parents need this functionality.
     * 
     * @return Message about upcoming vaccinations
     */
    public String viewUpcomingVaccinations() {
        return "You have " + numberOfChildren + " child(ren) with upcoming vaccinations. " +
               "Check the vaccination schedule for details.";
    }
    
    /**
     * Parent-specific method: View health alerts.
     * 
     * @return Message about health alerts
     */
    public String viewHealthAlerts() {
        return "Viewing community health alerts and recommendations...";
    }
    
    /**
     * Override toString to include Parent-specific information.
     * 
     * @return Formatted string with parent details
     */
    @Override
    public String toString() {
        return "Parent{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", contactInfo='" + getContactInfo() + '\'' +
                ", address='" + address + '\'' +
                ", numberOfChildren=" + numberOfChildren +
                '}';
    }
}

