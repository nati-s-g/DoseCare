package com.vaccinetracker.services;

import com.vaccinetracker.model.Admin;
import com.vaccinetracker.model.Parent;
import com.vaccinetracker.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService class manages user-related operations.
 * 
 * CONCEPT: Service Layer / Business Logic
 * - Separates business logic from UI (controllers)
 * - Uses ArrayList to store users (simulating a database)
 * - Provides methods to create, find, and manage users
 */
public class UserService {
    
    // Store users in a List (simulating database storage)
    private static List<User> users = new ArrayList<>();
    
    /**
     * Constructor - initializes the user list and creates some sample users.
     */
    public UserService() {
        if (users.isEmpty()) {
            initializeSampleUsers();  // Add some sample data for testing
        }
    }
    
    /**
     * Initialize with some sample users for demonstration.
     * In a real application, this would load from a database.
     */
    private void initializeSampleUsers() {
        // Create a sample admin
        Admin admin = new Admin("ADM001", "Dr. Sarah Johnson", "sarah.johnson@hospital.com", 
                               "HOSP001", "Pediatrics");
        users.add(admin);
        
        // Create a sample parent
        Parent parent = new Parent("PAR001", "John Smith", "john.smith@email.com", 
                                  "123 Main St, City");
        parent.setNumberOfChildren(1);
        users.add(parent);
    }
    
    /**
     * Create a new admin user.
     * 
     * @param userId Unique identifier
     * @param name Full name
     * @param contactInfo Contact information
     * @param hospitalId Hospital identifier
     * @param department Department name
     * @return The created Admin object
     */
    public Admin createAdmin(String userId, String name, String contactInfo, 
                            String hospitalId, String department) {
        Admin admin = new Admin(userId, name, contactInfo, hospitalId, department);
        users.add(admin);
        return admin;
    }
    
    /**
     * Create a new parent user.
     * 
     * @param userId Unique identifier
     * @param name Full name
     * @param contactInfo Contact information
     * @param address Home address
     * @return The created Parent object
     */
    public Parent createParent(String userId, String name, String contactInfo, String address) {
        Parent parent = new Parent(userId, name, contactInfo, address);
        users.add(parent);
        return parent;
    }
    
    /**
     * Find a user by their user ID.
     * 
     * CONCEPT: List Iteration
     * - Loops through list to find matching user
     * 
     * @param userId The user ID to search for
     * @return User object if found, null otherwise
     */
    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;  // User not found
    }
    
    /**
     * Authenticate a user by role (simple role-based selection).
     * For course project: just returns a user of the specified role.
     * 
     * @param role The role to authenticate as (ADMIN or PARENT)
     * @return User object of that role, or null if none found
     */
    public User authenticateByRole(String role) {
        for (User user : users) {
            if (user.getRole().equals(role)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Get all users in the system.
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);  // Return a copy to prevent external modification
    }
    
    /**
     * Get all admin users.
     * 
     * CONCEPT: Type Checking & Casting
     * - Checks if user is instance of Admin
     * - Casts User to Admin type
     * 
     * @return List of all Admin users
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Admin) {
                admins.add((Admin) user);  // Cast User to Admin
            }
        }
        return admins;
    }
    
    /**
     * Get all parent users.
     * 
     * @return List of all Parent users
     */
    public List<Parent> getAllParents() {
        List<Parent> parents = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Parent) {
                parents.add((Parent) user);  // Cast User to Parent
            }
        }
        return parents;
    }
    
    /**
     * Get the total number of users.
     * 
     * @return Number of users
     */
    public int getUserCount() {
        return users.size();
    }

    // Static access for StorageService
    public static List<User> getAllData() {
        return users;
    }

    public static void setAllData(List<User> data) {
        users = data;
    }
}

