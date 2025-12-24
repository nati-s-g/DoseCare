package com.vaccinetracker.services;

import com.vaccinetracker.model.User;

/**
 * Authentication service for handling user login and role selection.
 * 
 * CONCEPT: Service Layer & Integration
 * - Integrates with UserService to authenticate users
 * - For course project: simple role-based selection (simulation mode)
 * - Can be extended later for real authentication
 */
public class AuthService {
    
    // Reference to UserService (dependency injection pattern)
    private UserService userService;
    
    /**
     * Enum representing different user roles in the system.
     * An enum is a special type that defines a group of constants.
     * This enum is defined inside the AuthService class, so it's a nested enum.
     */
    public enum UserRole {
        /**
         * Administrator role - has full access to the system
         */
        ADMIN,
        
        /**
         * Parent role - standard user access
         */
        PARENT,
        
        /**
         * Invalid role - used when authentication fails
         */
        INVALID
    }
    
    /**
     * Constructor that requires UserService.
     * 
     * CONCEPT: Dependency Injection
     * - AuthService depends on UserService
     * - Service is passed in constructor rather than created inside
     * 
     * @param userService The UserService instance to use
     */
    public AuthService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Simple role-based authentication (for course project).
     * This is a simulation - just returns a user of the specified role.
     * 
     * CONCEPT: Simple Authentication Simulation
     * - No real password checking
     * - Just selects a user by role
     * - Perfect for course projects
     * 
     * @param role The role to authenticate as (ADMIN or PARENT)
     * @return User object of that role, or null if none found
     */
    public User authenticateByRole(String role) {
        return userService.authenticateByRole(role);
    }
    
    /**
     * Attempts to authenticate a user with the provided credentials.
     * 
     * This method checks if the username and password match predefined values:
     * - Admin: username="admin" and password="admin" returns ADMIN role
     * - Parent: username="user1234" and password="12345678" returns PARENT role
     * - Any other combination returns INVALID role
     * 
     * NOTE: This is a simple simulation. In a real application, you would
     * check against a database with encrypted passwords.
     * 
     * @param username The username to check
     * @param password The password to check
     * @return UserRole enum value: ADMIN, PARENT, or INVALID
     */
    public UserRole login(String username, String password) {
        // Check if the credentials match the admin account
        // Using equals() method to compare strings (important for string comparison in Java)
        if (username.equals("admin") && password.equals("admin")) {
            // Admin credentials are correct, return ADMIN role
            return UserRole.ADMIN;
        }
        
        // Check if the credentials match the parent account
        if (username.equals("user1234") && password.equals("12345678")) {
            // Parent credentials are correct, return PARENT role
            return UserRole.PARENT;
        }
        
        // If we reach here, the credentials don't match any valid account
        // Return INVALID role to indicate authentication failed
        return UserRole.INVALID;
    }
    
    /**
     * Get a User object after role-based login.
     * Combines login() and authenticateByRole().
     * 
     * @param username The username
     * @param password The password
     * @return User object if credentials are valid, null otherwise
     */
    public User loginAndGetUser(String username, String password) {
        UserRole role = login(username, password);
        if (role != UserRole.INVALID) {
            return authenticateByRole(role.toString());
        }
        return null;
    }
}
