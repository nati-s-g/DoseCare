package com.vaccinetracker.services;

import com.vaccinetracker.model.Child;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private static List<Child> children = new ArrayList<>();
    
    /**
     * Constructor - initializes the children list.
     */
    public ChildService() {
        if (children.isEmpty()) {
            initializeDummyData();
        }
    }

    private void initializeDummyData() {
        // Ethiopian names: Child Name = First Name + Father's Name
        // Guardian Name usually Father (Father's Name + Grandfather's Name) or Mother
        
        String[] maleFirstNames = {
            "Abebe", "Kebede", "Dawit", "Yared", "Kirubel", "Mohammed", "Ahmed", "Eyob", "Nahom", "Solomon", 
            "Girma", "Tesfaye", "Worku", "Daniel", "Yosef", "Bilal", "Kedir", "Jemal", "Hassen", "Abel",
            "Elias", "Fikru", "Zelalem", "Mulugeta", "Getachew", "Abraham", "Samuel", "Robel", "Natnael"
        };

        String[] femaleFirstNames = {
            "Almaz", "Tigist", "Sara", "Hana", "Mahlet", "Fatima", "Aisha", "Kalkidan", "Aster", "Zeyneba", 
            "Medina", "Bethlehem", "Rahel", "Marta", "Helen", "Genet", "Meskerem", "Zewditu", "Lydia", "Hiwot",
            "Frehiwot", "Selam", "Meron", "Saron", "Yordanos", "Birtukan", "Tsedey", "Lemlem"
        };
        
        String[] lastNames = {
            "Tadesse", "Alemu", "Bekele", "Girma", "Assefa", "Worku", "Yosef", "Mengistu", "Demeke", "Berhanu",
            "Ali", "Hussein", "Mohammed", "Ibrahim", "Ousman", "Abdullah", "Yasin", "Omar", "Said", "Hassan",
            "Haile", "Solomon", "Tekle", "Fikru", "Zelalem", "Mulugeta", "Getachew", "Abraham"
        };

        // Generate 20 random children
        for (int i = 0; i < 20; i++) {
            // Random gender
            String gender = Math.random() < 0.5 ? "Male" : "Female";
            
            String firstName;
            if (gender.equals("Male")) {
                firstName = maleFirstNames[(int)(Math.random() * maleFirstNames.length)];
            } else {
                firstName = femaleFirstNames[(int)(Math.random() * femaleFirstNames.length)];
            }

            String fatherName = lastNames[(int)(Math.random() * lastNames.length)];
            String grandFatherName = lastNames[(int)(Math.random() * lastNames.length)];
            
            String childName = firstName + " " + fatherName;
            
            String guardianName;
            // 30% chance guardian is mother
            if (Math.random() < 0.3) {
                String motherFirstName = femaleFirstNames[(int)(Math.random() * femaleFirstNames.length)];
                String motherFatherName = lastNames[(int)(Math.random() * lastNames.length)];
                guardianName = motherFirstName + " " + motherFatherName;
            } else {
                // Guardian is father
                guardianName = fatherName + " " + grandFatherName;
            }
            
            // Random age between 0 days and 5 years (approx 1825 days)
            int ageInDays = (int)(Math.random() * 1825);
            LocalDate dob = LocalDate.now().minusDays(ageInDays);
            
            // Random parent ID and Hospital ID
            String parentId = "P" + String.format("%03d", (int)(Math.random() * 100));
            String hospitalId = "H" + String.format("%03d", (int)(Math.random() * 5) + 1);
            
            registerChild(childName, dob, parentId, hospitalId, gender, guardianName, generateRandomContact());
        }
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
        // Generate a unique and complex child ID
        String childId = "CHD-" + UUID.randomUUID().toString().substring(0, 13).toUpperCase();
        
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

    /**
     * Link a child to a parent account.
     * 
     * @param childId The child ID
     * @param parentId The parent ID
     * @return true if successful, false if child not found
     */
    public boolean linkChildToParent(String childId, String parentId) {
        Child child = getChildById(childId);
        if (child != null) {
            child.setParentId(parentId);
            return true;
        }
        return false;
    }
}

