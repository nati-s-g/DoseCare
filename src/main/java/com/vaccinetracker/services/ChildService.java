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
        initializeDummyData();
    }

    private void initializeDummyData() {
        // Ethiopian names: Child Name = First Name + Father's Name
        // Guardian Name usually Father (Father's Name + Grandfather's Name) or Mother
        
        registerChild("Abebe Kebede", LocalDate.of(2023, 5, 15), "P001", "H001", "Male", "Kebede Tadesse", generateRandomContact());
        registerChild("Sara Tesfaye", LocalDate.of(2024, 1, 10), "P002", "H001", "Female", "Tesfaye Alemu", generateRandomContact());
        registerChild("Tigist Haile", LocalDate.of(2022, 11, 20), "P003", "H002", "Female", "Almaz Bekele", generateRandomContact()); // Mother as guardian
        registerChild("Dawit Solomon", LocalDate.of(2023, 8, 5), "P004", "H001", "Male", "Solomon Girma", generateRandomContact());
        registerChild("Bethlehem Assefa", LocalDate.of(2024, 3, 12), "P005", "H003", "Female", "Assefa Worku", generateRandomContact());
        registerChild("Yared Mulugeta", LocalDate.of(2023, 12, 1), "P006", "H002", "Male", "Mulugeta Abebe", generateRandomContact());
        registerChild("Hana Daniel", LocalDate.of(2022, 9, 25), "P007", "H001", "Female", "Daniel Yosef", generateRandomContact());
        registerChild("Kirubel Getachew", LocalDate.of(2024, 2, 18), "P008", "H003", "Male", "Aster Mengistu", generateRandomContact()); // Mother as guardian
        registerChild("Mahlet Tekle", LocalDate.of(2023, 6, 30), "P009", "H002", "Female", "Tekle Yosef", generateRandomContact());
        registerChild("Eyob Fikru", LocalDate.of(2022, 10, 8), "P010", "H001", "Male", "Fikru Demeke", generateRandomContact());
        registerChild("Kalkidan Zelalem", LocalDate.of(2024, 4, 5), "P011", "H003", "Female", "Zelalem Tadesse", generateRandomContact());
        registerChild("Nahom Berhanu", LocalDate.of(2023, 1, 22), "P012", "H002", "Male", "Berhanu Kebede", generateRandomContact());
    }

    private String generateRandomContact() {
        // Generate 8 random digits
        int randomNum = 10000000 + (int)(Math.random() * 90000000);
        return "+2519" + randomNum;
    }
    
    /**
     * Register a new child in the system.
     * 
     * @param name Child's name
     * @param dateOfBirth Date of birth
     * @param parentId Parent's user ID
     * @param hospitalId Hospital identifier
     * @param gender Gender of the child
     * @param guardianName Name of the guardian
     * @param guardianContact Contact number of the guardian
     * @return The created Child object
     */
    public Child registerChild(String name, LocalDate dateOfBirth, String parentId, String hospitalId, String gender, String guardianName, String guardianContact) {
        // Generate a unique child ID
        String childId = "CHILD" + String.format("%04d", nextChildId++);
        
        // Create new child object
        Child child = new Child(childId, name, dateOfBirth, parentId, hospitalId, gender, guardianName, guardianContact);
        
        // Add to the list
        children.add(child);
        
        return child;
    }
    
    /**
     * Remove a child from the system.
     * 
     * @param childId The ID of the child to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean deleteChild(String childId) {
        return children.removeIf(child -> child.getChildId().equals(childId));
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

