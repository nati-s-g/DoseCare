package com.vaccinetracker.model;

/**
 * Abstract base class representing a user in the system.
 * 
 * CONCEPT: Abstract Class & Inheritance
 * - Abstract classes cannot be instantiated directly
 * - They serve as templates for child classes
 * - Common fields and methods are defined here
 * - Child classes (Admin, Parent) must implement abstract methods
 */
public abstract class User {
    
    // Fields common to all users
    private String userId;          // Unique identifier for the user
    private String name;            // Full name of the user
    private String contactInfo;     // Email or phone number
    private String role;            // Role type (ADMIN or PARENT)
    
    /**
     * Constructor to initialize user fields.
     * 
     * @param userId Unique identifier
     * @param name Full name
     * @param contactInfo Contact information
     * @param role User role (ADMIN or PARENT)
     */
    public User(String userId, String name, String contactInfo, String role) {
        this.userId = userId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.role = role;
    }
    
    // Getter methods (allows reading private fields)
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public String getRole() {
        return role;
    }
    
    // Setter methods (allows modifying private fields)
    public void setName(String name) {
        this.name = name;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    /**
     * Abstract method that must be implemented by child classes.
     * Each user type has different dashboard capabilities.
     * 
     * CONCEPT: Abstract Method
     * - Forces child classes to provide their own implementation
     * - Demonstrates polymorphism (same method, different behavior)
     * 
     * @return String description of what the user can do
     */
    public abstract String getDashboardInfo();
    
    /**
     * Method to display user information.
     * This is a concrete method (has implementation) that child classes inherit.
     * 
     * @return Formatted string with user details
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

