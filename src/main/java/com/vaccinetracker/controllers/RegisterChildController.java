package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.User;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.VaccinationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller for the Register Child form.
 * Handles child registration and vaccination schedule creation.
 */
public class RegisterChildController {
    
    @FXML
    private TextField childNameField;
    
    @FXML
    private DatePicker dateOfBirthPicker;
    
    @FXML
    private TextField fatherNameField;
    
    @FXML
    private TextField fatherContactField;
    
    @FXML
    private TextField motherNameField;
    
    @FXML
    private TextField motherContactField;
    
    @FXML
    private TextField hospitalIdField;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label statusLabel;
    
    // Services and current user
    private ChildService childService;
    private VaccinationService vaccinationService;
    private User currentUser;
    
    /**
     * Set the child service.
     */
    public void setChildService(ChildService childService) {
        this.childService = childService;
    }
    
    /**
     * Set the vaccination service.
     */
    public void setVaccinationService(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }
    
    /**
     * Set the current user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Set default date to today
        dateOfBirthPicker.setValue(LocalDate.now());
        
        // Pre-fill parent info if available
        if (currentUser != null) {
            // Logic to determine if current user is father or mother could go here
            // fatherNameField.setText(currentUser.getName()); // Example
        }
    }
    
    /**
     * Handle register button click.
     */
    @FXML
    private void handleRegister() {
        // Validate input fields
        if (!validateInput()) {
            return;
        }
        
        try {
            // Get input values
            String childName = childNameField.getText().trim();
            LocalDate dateOfBirth = dateOfBirthPicker.getValue();
            String fatherName = fatherNameField.getText().trim();
            String fatherContact = fatherContactField.getText().trim();
            String motherName = motherNameField.getText().trim();
            String motherContact = motherContactField.getText().trim();

            String hospitalId = hospitalIdField.getText().trim();
            
            if (hospitalId.isEmpty()) {
                hospitalId = "HOSP001"; // Default hospital ID
            }
            
            // For now, use current user's ID as parent ID
            // In a real system, you'd create or find the parent user first
            String parentId = (currentUser != null) ? currentUser.getUserId() : "PAR001";
            
            // Register the child
            Child child = ChildService.registerChild(childName, dateOfBirth, parentId, hospitalId, "Unknown", fatherName, fatherContact, motherName, motherContact);
            
            // Create vaccination schedule for the child
            var schedule = vaccinationService.createScheduleForChild(child.getChildId());
            
            // Show success message
            showStatus("Child registered successfully! Vaccination schedule created with " + 
                      schedule.size() + " vaccination(s).", true);
            
            // Clear form after a delay (optional - you might want to keep it for another registration)
            // handleClear();
            
        } catch (Exception e) {
            showStatus("Error registering child: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }
    
    /**
     * Validate input fields.
     * 
     * @return true if all required fields are valid, false otherwise
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (childNameField.getText().trim().isEmpty()) {
            errors.append("Child name is required.\n");
        }
        
        if (dateOfBirthPicker.getValue() == null) {
            errors.append("Date of birth is required.\n");
        } else if (dateOfBirthPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("Date of birth cannot be in the future.\n");
        }
        
        if (fatherNameField.getText().trim().isEmpty()) {
            errors.append("Father's name is required.\n");
        }
        
        if (fatherContactField.getText().trim().isEmpty()) {
            errors.append("Father's contact is required.\n");
        }

        if (motherNameField.getText().trim().isEmpty()) {
            errors.append("Mother's name is required.\n");
        }
        
        if (motherContactField.getText().trim().isEmpty()) {
            errors.append("Mother's contact is required.\n");
        }
        
        if (errors.length() > 0) {
            showStatus(errors.toString(), false);
            return false;
        }
        
        return true;
    }
    
    /**
     * Show status message.
     * 
     * @param message The message to display
     * @param isSuccess true for success message, false for error
     */
    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        
        if (isSuccess) {
            statusLabel.getStyleClass().removeAll("label-error");
            statusLabel.getStyleClass().add("label-success");
        } else {
            statusLabel.getStyleClass().removeAll("label-success");
            statusLabel.getStyleClass().add("label-error");
        }
    }
    
    /**
     * Handle clear button click.
     */
    @FXML
    private void handleClear() {
        childNameField.clear();
        dateOfBirthPicker.setValue(LocalDate.now());
        fatherNameField.clear();
        fatherContactField.clear();
        motherNameField.clear();
        motherContactField.clear();
        hospitalIdField.setText("HOSP001");
        statusLabel.setVisible(false);
    }
    
    /**
     * Handle back button click.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
            Parent root = loader.load();
            
            AdminController controller = loader.getController();
            controller.setUserService(null); // Will be reinitialized
            controller.setCurrentUser(currentUser);
            controller.initialize();
            
            App.setRoot(root, "Admin Dashboard - CoreVax");
        } catch (IOException e) {
            System.err.println("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

