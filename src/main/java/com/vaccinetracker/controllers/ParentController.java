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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller for the Parent Dashboard.
 * Handles parent-specific operations and displays relevant information.
 */
public class ParentController {
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Menu Buttons
    @FXML private Button dashboardMenuButton;
    @FXML private Button childrenMenuButton;
    @FXML private Button scheduleMenuButton;
    @FXML private Button recordsMenuButton;
    @FXML private Button resourcesMenuButton;
    
    // Stats Labels
    @FXML private Label childrenCountLabel;
    @FXML private Label upcomingCountLabel;
    @FXML private Label alertsCountLabel;
    
    // Content Areas
    @FXML private VBox contentArea;
    @FXML private VBox mainContent; // The default dashboard content
    @FXML private VBox upcomingVaccinationsBox;
    @FXML private Label noUpcomingLabel;
    @FXML private VBox sitesBox;
    @FXML private Label noSitesLabel;
    @FXML private VBox alertsBox;
    @FXML private Label noAlertsLabel;
    
    // Current user and services
    private User currentUser;
    private UserService userService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationService vaccinationService;
    private VaccinationSiteService vaccinationSiteService;
    private AlertService alertService;
    
    private List<Node> dashboardViewCache;
    
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
        if (currentUser == null) {
            return;
        }

        if (currentUser != null) {
            welcomeLabel.setText(currentUser.getName());
        }
        
        // Initialize services
        childService = new ChildService();
        vaccineService = new VaccineService();
        vaccinationService = new VaccinationService(childService, vaccineService);
        vaccinationSiteService = new VaccinationSiteService();
        alertService = new AlertService();
        
        // Cache the dashboard view
        if (contentArea != null && !contentArea.getChildren().isEmpty()) {
             dashboardViewCache = new ArrayList<>(contentArea.getChildren());
        }
        
        updateDashboardStats();
        loadUpcomingVaccinations();
        loadVaccinationSites();
        loadHealthAlerts();
    }
    
    private void updateDashboardStats() {
        List<Child> children = childService.getChildrenByParent(currentUser.getUserId());
        if (childrenCountLabel != null) {
            childrenCountLabel.setText(String.valueOf(children.size()));
        }
        
        int upcomingCount = 0;
        for (Child child : children) {
            upcomingCount += vaccinationService.getUpcomingVaccinations(child.getChildId()).size();
        }
        if (upcomingCountLabel != null) {
            upcomingCountLabel.setText(String.valueOf(upcomingCount));
        }
        
        // For alerts, we'll just count all for now as we don't have specific targeting yet
        if (alertsCountLabel != null) {
            alertsCountLabel.setText(String.valueOf(alertService.getAllAlerts().size()));
        }
    }
    
    /**
     * Load and display upcoming vaccinations for the parent's children.
     */
    private void loadUpcomingVaccinations() {
        if (upcomingVaccinationsBox == null) return;
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
                
                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #eee; -fx-border-radius: 5;");
                
                VBox info = new VBox(2);
                Label childNameLabel = new Label(child.getName());
                childNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label vaccineLabel = new Label(vaccineName);
                vaccineLabel.setStyle("-fx-text-fill: -text-muted;");
                info.getChildren().addAll(childNameLabel, vaccineLabel);
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label dateLabel = new Label("Due: " + dueDateStr);
                if (record.isOverdue()) {
                    dateLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                } else {
                    dateLabel.setStyle("-fx-text-fill: -brand-blue; -fx-font-weight: bold;");
                }
                
                row.getChildren().addAll(info, spacer, dateLabel);
                upcomingVaccinationsBox.getChildren().add(row);
            }
        }
        
        if (!hasUpcoming) {
            noUpcomingLabel.setVisible(true);
        }
    }
    
    /**
     * Load and display vaccination sites.
     */
    private void loadVaccinationSites() {
        if (sitesBox == null) return;
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
            siteBox.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-radius: 5px;");
            
            Label nameLabel = new Label(site.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -brand-blue;");
            
            Label locationLabel = new Label("📍 " + site.getLocation());
            Label contactLabel = new Label("📞 " + site.getContactInfo());
            
            siteBox.getChildren().addAll(nameLabel, locationLabel, contactLabel);
            sitesBox.getChildren().add(siteBox);
        }
    }
    
    /**
     * Load and display health alerts.
     */
    private void loadHealthAlerts() {
        if (alertsBox == null) return;
        alertsBox.getChildren().clear();
        
        List<HealthAlert> alerts = alertService.getAllAlerts(); // Simplified for now
        
        if (alerts.isEmpty()) {
            noAlertsLabel.setVisible(true);
            return;
        }
        
        noAlertsLabel.setVisible(false);
        
        for (HealthAlert alert : alerts) {
            VBox alertBox = new VBox(5);
            alertBox.setPadding(new Insets(10));
            alertBox.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffe0b2; -fx-border-radius: 5px;");
            
            Label titleLabel = new Label("⚠️ " + alert.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100;");
            
            Label msgLabel = new Label(alert.getMessage());
            msgLabel.setWrapText(true);
            
            alertBox.getChildren().addAll(titleLabel, msgLabel);
            alertsBox.getChildren().add(alertBox);
        }
    }
    
    // Menu Handlers
    
    @FXML
    private void handleDashboardMenu() {
        updateActiveMenuButton(dashboardMenuButton);
        if (dashboardViewCache != null) {
            contentArea.getChildren().setAll(dashboardViewCache);
            updateDashboardStats();
            loadUpcomingVaccinations(); // Refresh data
        }
    }
    
    @FXML
    private void handleChildrenMenu() {
        updateActiveMenuButton(childrenMenuButton);
        // Create a simple view for children
        VBox childrenView = new VBox(20);
        childrenView.setPadding(new Insets(20));
        
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label title = new Label("My Children");
        title.getStyleClass().add("chart-title");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button addChildBtn = new Button("Add Child");
        addChildBtn.getStyleClass().add("button-primary");
        addChildBtn.setOnAction(e -> handleAddChild());
        
        header.getChildren().addAll(title, headerSpacer, addChildBtn);
        
        VBox list = new VBox(10);
        List<Child> children = childService.getChildrenByParent(currentUser.getUserId());
        
        if (children.isEmpty()) {
            Label noChildren = new Label("No children linked to your account yet.");
            noChildren.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 14px;");
            list.getChildren().add(noChildren);
        } else {
            for (Child child : children) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(15));
                card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 10;");
                card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                
                VBox info = new VBox(5);
                Label name = new Label(child.getName());
                name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                Label details = new Label("ID: " + child.getChildId() + " | Born: " + child.getDateOfBirth() + " | Gender: " + child.getGender());
                details.setStyle("-fx-text-fill: -text-muted;");
                
                info.getChildren().addAll(name, details);
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Button viewRecordBtn = new Button("View Record");
                viewRecordBtn.getStyleClass().add("button-secondary");
                viewRecordBtn.setOnAction(e -> openVaccinationRecord(child));
                
                card.getChildren().addAll(info, spacer, viewRecordBtn);
                list.getChildren().add(card);
            }
        }
        
        childrenView.getChildren().addAll(header, list);
        
        ScrollPane scroll = new ScrollPane(childrenView);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Add top bar back
        if (dashboardViewCache != null && !dashboardViewCache.isEmpty()) {
            contentArea.getChildren().setAll(dashboardViewCache.get(0), scroll);
        } else {
            contentArea.getChildren().setAll(scroll);
        }
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }
    
    private void handleAddChild() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Add Child");
        dialog.setHeaderText("Link Existing Child Record");
        dialog.setContentText("Please enter the Child ID provided by the admin:");

        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(childId -> {
            if (childId.trim().isEmpty()) return;
            
            boolean success = childService.linkChildToParent(childId.trim(), currentUser.getUserId());
            if (success) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Child linked successfully!");
                alert.showAndWait();
                handleChildrenMenu(); // Refresh view
                updateDashboardStats(); // Update stats
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Child ID not found. Please check and try again.");
                alert.showAndWait();
            }
        });
    }
    
    private void openVaccinationRecord(Child child) {
        // TODO: Implement opening specific child record
        System.out.println("Opening record for: " + child.getName());
    }

    @FXML
    private void handleScheduleMenu() {
        updateActiveMenuButton(scheduleMenuButton);
        // Reuse the upcoming vaccinations logic but in a full view
        VBox scheduleView = new VBox(20);
        scheduleView.setPadding(new Insets(20));
        
        Label title = new Label("Vaccination Schedule");
        title.getStyleClass().add("chart-title");
        
        VBox list = new VBox(10);
        // ... (Logic to show full schedule)
        // For now, just show the same upcoming list but maybe more detailed
        
        scheduleView.getChildren().addAll(title, new Label("Full schedule feature coming soon..."));
        
        ScrollPane scroll = new ScrollPane(scheduleView);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        if (dashboardViewCache != null && !dashboardViewCache.isEmpty()) {
            contentArea.getChildren().setAll(dashboardViewCache.get(0), scroll);
        } else {
            contentArea.getChildren().setAll(scroll);
        }
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    @FXML
    private void handleRecordsMenu() {
        updateActiveMenuButton(recordsMenuButton);
        // TODO: Integrate VaccinationRecordsController
        VBox recordsView = new VBox(20);
        recordsView.setPadding(new Insets(20));
        recordsView.getChildren().add(new Label("Records View - Select a child to view records"));
        
        // We could load the VaccinationRecords.fxml here but we need to configure it for parent view
        
        ScrollPane scroll = new ScrollPane(recordsView);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        if (dashboardViewCache != null && !dashboardViewCache.isEmpty()) {
            contentArea.getChildren().setAll(dashboardViewCache.get(0), scroll);
        } else {
            contentArea.getChildren().setAll(scroll);
        }
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    @FXML
    private void handleResourcesMenu() {
        updateActiveMenuButton(resourcesMenuButton);
        VBox resourcesView = new VBox(20);
        resourcesView.setPadding(new Insets(20));
        
        Label title = new Label("Resources & Health Alerts");
        title.getStyleClass().add("chart-title");
        
        VBox alertsContainer = new VBox(10);
        // Reuse loadHealthAlerts logic or similar
        List<HealthAlert> alerts = alertService.getAllAlerts();
        for (HealthAlert alert : alerts) {
             VBox alertBox = new VBox(5);
            alertBox.setPadding(new Insets(15));
            alertBox.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffe0b2; -fx-border-radius: 5px;");
            
            Label t = new Label("⚠️ " + alert.getTitle());
            t.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 14px;");
            
            Label m = new Label(alert.getMessage());
            m.setWrapText(true);
            
            alertBox.getChildren().addAll(t, m);
            alertsContainer.getChildren().add(alertBox);
        }
        
        resourcesView.getChildren().addAll(title, alertsContainer);
        
        ScrollPane scroll = new ScrollPane(resourcesView);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        if (dashboardViewCache != null && !dashboardViewCache.isEmpty()) {
            contentArea.getChildren().setAll(dashboardViewCache.get(0), scroll);
        } else {
            contentArea.getChildren().setAll(scroll);
        }
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }
    
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            App.setRoot(root, "Vaccine Tracker - Login");
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateActiveMenuButton(Button activeButton) {
        dashboardMenuButton.getStyleClass().remove("active");
        childrenMenuButton.getStyleClass().remove("active");
        scheduleMenuButton.getStyleClass().remove("active");
        recordsMenuButton.getStyleClass().remove("active");
        resourcesMenuButton.getStyleClass().remove("active");
        
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
