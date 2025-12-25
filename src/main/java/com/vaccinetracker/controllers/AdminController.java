package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.User;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.UserService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccinationSiteService;
import com.vaccinetracker.services.VaccineService;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;

import javafx.scene.layout.VBox;
import javafx.scene.Node;

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
    private VBox mainContent;

    @FXML
    private LineChart<String, Number> vaccinationChart;
    
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
            vaccinationChart.getData().clear(); // Clear existing data to prevent duplicates
            
            XYChart.Series<String, Number> maleSeries = new XYChart.Series<>();
            maleSeries.setName("Boys");
            maleSeries.getData().add(new XYChart.Data<>("Jan", 12));
            maleSeries.getData().add(new XYChart.Data<>("Feb", 18));
            maleSeries.getData().add(new XYChart.Data<>("Mar", 22));
            maleSeries.getData().add(new XYChart.Data<>("Apr", 15));
            maleSeries.getData().add(new XYChart.Data<>("May", 28));
            maleSeries.getData().add(new XYChart.Data<>("Jun", 32));
            maleSeries.getData().add(new XYChart.Data<>("Jul", 35));
            maleSeries.getData().add(new XYChart.Data<>("Aug", 30));
            maleSeries.getData().add(new XYChart.Data<>("Sep", 25));
            maleSeries.getData().add(new XYChart.Data<>("Oct", 28));
            maleSeries.getData().add(new XYChart.Data<>("Nov", 20));
            maleSeries.getData().add(new XYChart.Data<>("Dec", 15));

            XYChart.Series<String, Number> femaleSeries = new XYChart.Series<>();
            femaleSeries.setName("Girls");
            femaleSeries.getData().add(new XYChart.Data<>("Jan", 10));
            femaleSeries.getData().add(new XYChart.Data<>("Feb", 20));
            femaleSeries.getData().add(new XYChart.Data<>("Mar", 18));
            femaleSeries.getData().add(new XYChart.Data<>("Apr", 18));
            femaleSeries.getData().add(new XYChart.Data<>("May", 25));
            femaleSeries.getData().add(new XYChart.Data<>("Jun", 30));
            femaleSeries.getData().add(new XYChart.Data<>("Jul", 32));
            femaleSeries.getData().add(new XYChart.Data<>("Aug", 28));
            femaleSeries.getData().add(new XYChart.Data<>("Sep", 22));
            femaleSeries.getData().add(new XYChart.Data<>("Oct", 26));
            femaleSeries.getData().add(new XYChart.Data<>("Nov", 18));
            femaleSeries.getData().add(new XYChart.Data<>("Dec", 12));

            vaccinationChart.getData().addAll(maleSeries, femaleSeries);
        }
    }
    
    /**
     * Handle children menu button click.
     */
    @FXML
    private void handleChildrenMenu() {
        System.out.println("Children menu clicked");
        try {
            if (mainContent == null) {
                System.err.println("ERROR: mainContent is null!");
                return;
            }
            
            System.out.println("Loading ChildrenView.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChildrenView.fxml"));
            Node childrenView = loader.load();
            System.out.println("ChildrenView loaded successfully");
            
            ChildrenController controller = loader.getController();
            if (controller == null) {
                System.err.println("ERROR: ChildrenController is null!");
            } else {
                System.out.println("Initializing ChildrenController...");
                controller.setChildService(childService);
                controller.setAlertService(alertService);
                controller.setVaccinationService(vaccinationService);
                controller.setVaccineService(vaccineService);
                controller.setVaccinationSiteService(vaccinationSiteService);
            }
            
            // Replace main content
            mainContent.getChildren().setAll(childrenView);
            System.out.println("Main content updated");
            
            // Update active state of buttons (optional, but good for UX)
            updateActiveMenuButton(childrenMenuButton);
            
        } catch (Exception e) {
            System.err.println("Error loading children view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateActiveMenuButton(Button activeButton) {
        // Reset all buttons (simplified)
        // In a real app, you'd have a list of buttons or a toggle group
        childrenMenuButton.getStyleClass().remove("active");
        sitesMenuButton.getStyleClass().remove("active");
        inventoryMenuButton.getStyleClass().remove("active");
        alertsMenuButton.getStyleClass().remove("active");
        
        // Set active
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
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
        System.out.println("Sites menu clicked");
        try {
            if (mainContent == null) {
                System.err.println("ERROR: mainContent is null!");
                return;
            }
            
            System.out.println("Loading VaccinationSitesView.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VaccinationSitesView.fxml"));
            Node sitesView = loader.load();
            System.out.println("VaccinationSitesView loaded successfully");
            
            VaccinationSitesController controller = loader.getController();
            if (controller == null) {
                System.err.println("ERROR: VaccinationSitesController is null!");
            } else {
                System.out.println("Initializing VaccinationSitesController...");
                controller.setVaccinationSiteService(vaccinationSiteService);
            }
            
            // Replace main content
            mainContent.getChildren().setAll(sitesView);
            System.out.println("Main content updated");
            
            // Update active state of buttons
            updateActiveMenuButton(sitesMenuButton);
            
        } catch (Exception e) {
            System.err.println("Error loading sites view: " + e.getMessage());
            e.printStackTrace();
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

