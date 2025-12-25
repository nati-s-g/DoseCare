package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.User;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.UserService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccinationSiteService;
import com.vaccinetracker.services.VaccineService;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;

/**
 * Controller for the Admin Dashboard.
 * Handles admin-specific operations and navigation.
 */
public class AdminController {
    
    @FXML
    private Button logoutButton;

    @FXML
    private Button childrenMenuButton;

    @FXML
    private Button sitesMenuButton;

    @FXML
    private Button inventoryMenuButton;

    @FXML
    private Button alertsMenuButton;

    @FXML
    private BarChart<String, Number> vaccinationChart;
    
    // Current user and services
    private User currentUser;
    private UserService userService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationService vaccinationService;
    private VaccinationSiteService vaccinationSiteService;
    private AlertService alertService;
    
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
     * Initialize the controller and services.
     */
    public void initialize() {
        // Initialize services
        childService = new ChildService();
        vaccineService = new VaccineService();
        vaccinationService = new VaccinationService(childService, vaccineService);
        vaccinationSiteService = new VaccinationSiteService();
        alertService = new AlertService();
        
        // Populate Chart
        if (vaccinationChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Children Vaccinated");
            series.getData().add(new XYChart.Data<>("Jan", 15));
            series.getData().add(new XYChart.Data<>("Feb", 25));
            series.getData().add(new XYChart.Data<>("Mar", 35));
            series.getData().add(new XYChart.Data<>("Apr", 25));
            series.getData().add(new XYChart.Data<>("May", 45));
            series.getData().add(new XYChart.Data<>("Jun", 55));
            vaccinationChart.getData().add(series);
        }
    }
    
    /**
     * Handle register child button click.
     */
    @FXML
    private void handleRegisterChild() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RegisterChild.fxml"));
            Parent root = loader.load();
            
            RegisterChildController controller = loader.getController();
            controller.setChildService(childService);
            controller.setVaccinationService(vaccinationService);
            controller.setCurrentUser(currentUser);
            
            App.setRoot(root, "Register New Child - Vaccine Tracker");
        } catch (IOException e) {
            System.err.println("Error loading register child view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle manage vaccines button click.
     */
    @FXML
    private void handleManageVaccines() {
        // For now, show an alert with vaccine count
        // In a full implementation, this would open a vaccine management window
        int vaccineCount = vaccineService.getVaccineCount();
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Manage Vaccines");
        alert.setHeaderText("Vaccine Management");
        alert.setContentText("There are " + vaccineCount + " vaccines in the system.\n\nVaccine management interface coming soon.");
        alert.showAndWait();
    }
    
    /**
     * Handle manage sites button click.
     */
    @FXML
    private void handleManageSites() {
        // Show site count
        int siteCount = vaccinationSiteService.getSiteCount();
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Manage Vaccination Sites");
        alert.setHeaderText("Site Management");
        alert.setContentText("There are " + siteCount + " vaccination sites in the system.\n\nSite management interface coming soon.");
        alert.showAndWait();
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
            controller.setIsAdminView(true);
            controller.initialize();
            
            App.setRoot(root, "Vaccination Records - Vaccine Tracker");
        } catch (IOException e) {
            System.err.println("Error loading vaccination records view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle view health alerts button click.
     */
    @FXML
    private void handleViewHealthAlerts() {
        StringBuilder alertsText = new StringBuilder();
        alertsText.append("Active Health Alerts:\n\n");
        
        var activeAlerts = alertService.getActiveAlerts();
        if (activeAlerts.isEmpty()) {
            alertsText.append("No active alerts at this time.");
        } else {
            for (var alert : activeAlerts) {
                alertsText.append(String.format("• [%s] %s\n%s\n\n", 
                    alert.getSeverityLevel(), alert.getTitle(), alert.getMessage()));
            }
        }
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Community Health Alerts");
        alert.setHeaderText("Health Alerts & Recommendations");
        alert.setContentText(alertsText.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
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

