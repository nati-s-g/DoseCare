package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.HealthAlert;
import com.vaccinetracker.model.User;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.UserService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccinationSiteService;
import com.vaccinetracker.services.VaccineService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Parent Dashboard.
 * Handles parent-specific operations and displays relevant information.
 */
public class ParentController {
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button viewRecordsButton;
    
    @FXML
    private VBox upcomingVaccinationsBox;
    
    @FXML
    private Label noUpcomingLabel;
    
    @FXML
    private VBox sitesBox;
    
    @FXML
    private Label noSitesLabel;
    
    @FXML
    private VBox alertsBox;
    
    @FXML
    private Label noAlertsLabel;
    
    // Current user and services
    private User currentUser;
    private UserService userService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationService vaccinationService;
    private VaccinationSiteService vaccinationSiteService;
    private AlertService alertService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    /**
     * Set the current user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Set the user service.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Initialize the controller and load data.
     */
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
        }
        
        // Initialize services
        childService = new ChildService();
        vaccineService = new VaccineService();
        vaccinationService = new VaccinationService(childService, vaccineService);
        vaccinationSiteService = new VaccinationSiteService();
        alertService = new AlertService();
        
        // Load dashboard data
        loadUpcomingVaccinations();
        loadVaccinationSites();
        loadHealthAlerts();
    }
    
    /**
     * Load and display upcoming vaccinations for the parent's children.
     */
    private void loadUpcomingVaccinations() {
        upcomingVaccinationsBox.getChildren().clear();
        
        List<Child> children = childService.getChildrenByParent(currentUser.getUserId());
        
        if (children.isEmpty()) {
            noUpcomingLabel.setVisible(true);
            return;
        }
        
        noUpcomingLabel.setVisible(false);
        boolean hasUpcoming = false;
        
        for (Child child : children) {
            List<VaccinationRecord> upcoming = vaccinationService.getUpcomingVaccinations(child.getChildId());
            
            for (VaccinationRecord record : upcoming) {
                hasUpcoming = true;
                var vaccine = vaccineService.getVaccineById(record.getVaccineId());
                String vaccineName = (vaccine != null) ? vaccine.getName() : record.getVaccineId();
                
                String dueDateStr = record.getNextDueDate() != null 
                    ? record.getNextDueDate().format(DATE_FORMATTER) 
                    : "TBD";
                
                Label recordLabel = new Label(String.format("• %s - %s (Due: %s)", 
                    child.getName(), vaccineName, dueDateStr));
                recordLabel.setWrapText(true);
                recordLabel.setPadding(new Insets(5));
                
                if (record.isOverdue()) {
                    recordLabel.getStyleClass().add("status-overdue");
                } else {
                    recordLabel.getStyleClass().add("status-pending");
                }
                
                upcomingVaccinationsBox.getChildren().add(recordLabel);
            }
        }
        
        if (!hasUpcoming) {
            noUpcomingLabel.setText("No upcoming vaccinations scheduled");
            noUpcomingLabel.setVisible(true);
        }
    }
    
    /**
     * Load and display vaccination sites.
     */
    private void loadVaccinationSites() {
        sitesBox.getChildren().clear();
        
        List<VaccinationSite> sites = vaccinationSiteService.getAllSites();
        
        if (sites.isEmpty()) {
            noSitesLabel.setVisible(true);
            return;
        }
        
        noSitesLabel.setVisible(false);
        
        for (VaccinationSite site : sites) {
            VBox siteBox = new VBox(5);
            siteBox.setPadding(new Insets(10));
            siteBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");
            
            Label nameLabel = new Label(site.getName());
            nameLabel.getStyleClass().add("label-header");
            
            Label locationLabel = new Label("📍 " + site.getLocation());
            Label contactLabel = new Label("📞 " + site.getContactInfo());
            Label vaccinesLabel = new Label("💉 Available vaccines: " + site.getVaccineCount());
            
            siteBox.getChildren().addAll(nameLabel, locationLabel, contactLabel, vaccinesLabel);
            sitesBox.getChildren().add(siteBox);
        }
    }
    
    /**
     * Load and display health alerts.
     */
    private void loadHealthAlerts() {
        alertsBox.getChildren().clear();
        
        List<HealthAlert> alerts = alertService.getActiveAlerts();
        
        if (alerts.isEmpty()) {
            noAlertsLabel.setVisible(true);
            return;
        }
        
        noAlertsLabel.setVisible(false);
        
        for (HealthAlert alert : alerts) {
            VBox alertBox = new VBox(5);
            alertBox.setPadding(new Insets(10));
            
            // Apply severity-based styling
            String severityClass = "alert-info";
            String severityStyle = "severity-low";
            
            switch (alert.getSeverityLevel()) {
                case HIGH:
                    severityClass = "alert-error";
                    severityStyle = "severity-high";
                    break;
                case MEDIUM:
                    severityClass = "alert-box";
                    severityStyle = "severity-medium";
                    break;
                default:
                    severityClass = "alert-info";
                    severityStyle = "severity-low";
                    break;
            }
            
            alertBox.getStyleClass().add(severityClass);
            
            Label titleLabel = new Label(alert.getTitle());
            titleLabel.getStyleClass().addAll("label-header", severityStyle);
            
            Label messageLabel = new Label(alert.getMessage());
            messageLabel.setWrapText(true);
            
            alertBox.getChildren().addAll(titleLabel, messageLabel);
            alertsBox.getChildren().add(alertBox);
        }
    }
    
    /**
     * Handle view records button click.
     */
    @FXML
    private void handleViewRecords() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VaccinationRecords.fxml"));
            Parent root = loader.load();
            
            VaccinationRecordsController controller = loader.getController();
            controller.setVaccinationService(vaccinationService);
            controller.setChildService(childService);
            controller.setVaccineService(vaccineService);
            controller.setIsAdminView(false);
            controller.setParentId(currentUser.getUserId());
            controller.initialize();
            
            App.setRoot(root, "Vaccination Records - Vaccine Tracker");
        } catch (IOException e) {
            System.err.println("Error loading vaccination records view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle logout button click.
     */
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            App.setRoot(root, "Vaccine Tracker - Login");
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

