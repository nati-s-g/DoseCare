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
    public User authenticate(String username, String password) {
        return userService.authenticate(username, password);
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
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            if (user.getRole().equals("ADMIN")) {
                return UserRole.ADMIN;
            } else if (user.getRole().equals("PARENT")) {
                return UserRole.PARENT;
            }
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
        return authenticate(username, password);
    }
}
