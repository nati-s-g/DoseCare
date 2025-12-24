package com.vaccinetracker.services;

import com.vaccinetracker.model.Child;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ChildService class manages child-related operations.
 * 
 * CONCEPT: Service Layer
 * - Handles all operations related to children
 * - Manages a list of children (simulating database)
 * - Provides methods to register, find, and manage children
 */
public class ChildService {
    
    // Store children in a List (simulating database storage)
    private List<Child> children;
    private int nextChildId = 1;  // Auto-increment ID generator
    
    /**
     * Constructor - initializes the children list.
     */
    public ChildService() {
        this.children = new ArrayList<>();
    }
    
    /**
     * Register a new child in the system.
     * 
     * @param name Child's name
     * @param dateOfBirth Date of birth
     * @param parentId Parent's user ID
     * @param hospitalId Hospital identifier
     * @return The created Child object
     */
    public Child registerChild(String name, LocalDate dateOfBirth, String parentId, String hospitalId) {
        // Generate a unique child ID
        String childId = "CHILD" + String.format("%04d", nextChildId++);
        
        // Create new child object
        Child child = new Child(childId, name, dateOfBirth, parentId, hospitalId);
        
        // Add to the list
        children.add(child);
        
        return child;
    }
    
    /**
     * Find a child by their child ID.
     * 
     * @param childId The child ID to search for
     * @return Child object if found, null otherwise
     */
    public Child getChildById(String childId) {
        for (Child child : children) {
            if (child.getChildId().equals(childId)) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Get all children registered by a specific parent.
     * 
     * CONCEPT: Filtering Collections
     * - Loops through list and finds matching items
     * 
     * @param parentId The parent's user ID
     * @return List of children belonging to that parent
     */
    public List<Child> getChildrenByParent(String parentId) {
        List<Child> parentChildren = new ArrayList<>();
        for (Child child : children) {
            if (child.getParentId().equals(parentId)) {
                parentChildren.add(child);
            }
        }
        return parentChildren;
    }
    
    /**
     * Get all children in the system.
     * 
     * @return List of all children
     */
    public List<Child> getAllChildren() {
        return new ArrayList<>(children);  // Return a copy
    }
    
    /**
     * Get children registered at a specific hospital.
     * 
     * @param hospitalId The hospital identifier
     * @return List of children registered at that hospital
     */
    public List<Child> getChildrenByHospital(String hospitalId) {
        List<Child> hospitalChildren = new ArrayList<>();
        for (Child child : children) {
            if (child.getHospitalId().equals(hospitalId)) {
                hospitalChildren.add(child);
            }
        }
        return hospitalChildren;
    }
    
    /**
     * Get the total number of children registered.
     * 
     * @return Number of children
     */
    public int getChildCount() {
        return children.size();
    }
    
    /**
     * Update child information.
     * 
     * @param childId The child ID to update
     * @param newName New name (null to keep current)
     * @param newDateOfBirth New date of birth (null to keep current)
     * @return true if child was found and updated, false otherwise
     */
    public boolean updateChild(String childId, String newName, LocalDate newDateOfBirth) {
        Child child = getChildById(childId);
        if (child != null) {
            if (newName != null) {
                child.setName(newName);
            }
            if (newDateOfBirth != null) {
                child.setDateOfBirth(newDateOfBirth);
            }
            return true;
        }
        return false;
    }
}

