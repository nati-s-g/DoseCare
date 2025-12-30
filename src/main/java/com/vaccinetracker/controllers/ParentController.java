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
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.DateCell;
import javafx.scene.control.Alert;
import java.util.Optional;
import java.time.LocalDate;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VaccinationRecords.fxml"));
            Parent view = loader.load();
            
            VaccinationRecordsController controller = loader.getController();
            controller.setVaccinationService(vaccinationService);
            controller.setChildService(childService);
            controller.setVaccineService(vaccineService);
            controller.setChildId(child.getChildId());
            controller.setOnBackAction(() -> handleChildrenMenu());
            controller.initialize(); // Reload data with new settings
            
            // Replace content area content
            contentArea.getChildren().setAll(view);
            VBox.setVgrow(view, Priority.ALWAYS);
            
        } catch (IOException e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load records view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleScheduleMenu() {
        updateActiveMenuButton(scheduleMenuButton);
        
        VBox scheduleView = new VBox(20);
        scheduleView.setPadding(new Insets(20));
        
        Label title = new Label("Vaccination Schedule");
        title.getStyleClass().add("chart-title");
        
        VBox contentBox = new VBox(15);
        
        List<Child> children = childService.getChildrenByParent(currentUser.getUserId());
        
        if (children.isEmpty()) {
            Label noChildren = new Label("No children linked to your account.");
            noChildren.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 14px;");
            contentBox.getChildren().add(noChildren);
        } else {
            boolean hasAnyUpcoming = false;
            
            for (Child child : children) {
                List<VaccinationRecord> upcoming = vaccinationService.getUpcomingVaccinations(child.getChildId());
                
                if (!upcoming.isEmpty()) {
                    hasAnyUpcoming = true;
                    
                    VBox childSection = new VBox(10);
                    childSection.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                    
                    Label childName = new Label(child.getName());
                    childName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -brand-blue;");
                    
                    VBox recordsList = new VBox(10);
                    
                    for (VaccinationRecord record : upcoming) {
                        var vaccine = vaccineService.getVaccineById(record.getVaccineId());
                        String vaccineName = (vaccine != null) ? vaccine.getName() : record.getVaccineId();
                        String vaccineDesc = (vaccine != null) ? vaccine.getDescription() : "No description available.";
                        String dueDateStr = record.getNextDueDate() != null ? record.getNextDueDate().format(DATE_FORMATTER) : "TBD";
                        
                        // Determine site to display
                        String siteDisplayName = "Any authorized center";
                        String siteDisplayLocation = "Check local listings";
                        String siteLabelPrefix = "Recommended: ";
                        boolean isBooked = false;
                        
                        if (record.getVaccinationSiteId() != null) {
                            VaccinationSite bookedSite = vaccinationSiteService.getSiteById(record.getVaccinationSiteId());
                            if (bookedSite != null) {
                                siteDisplayName = bookedSite.getName();
                                siteDisplayLocation = bookedSite.getLocation();
                                siteLabelPrefix = "Booked at: ";
                                isBooked = true;
                            }
                        } 
                        
                        if (!isBooked) {
                             // Fallback to recommendation
                            List<VaccinationSite> sites = vaccinationSiteService.getAllSites();
                            if (!sites.isEmpty()) {
                                VaccinationSite s = sites.get(0);
                                siteDisplayName = s.getName();
                                siteDisplayLocation = s.getLocation();
                            }
                        }

                        VBox card = new VBox(8);
                        card.setStyle("-fx-border-color: #eee; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");
                        
                        // Header: Vaccine Name and Date
                        HBox header = new HBox(10);
                        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        Label vName = new Label(vaccineName);
                        vName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        Label dDate = new Label("Due: " + dueDateStr);
                        if (record.isOverdue()) {
                            dDate.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        } else {
                            dDate.setStyle("-fx-text-fill: -brand-blue; -fx-font-weight: bold;");
                        }
                        header.getChildren().addAll(vName, spacer, dDate);
                        
                        // Description
                        Label descLabel = new Label(vaccineDesc);
                        descLabel.setWrapText(true);
                        descLabel.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 12px;");
                        
                        // Site Info
                        HBox siteInfo = new HBox(10);
                        siteInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        Label siteIcon = new Label("🏥"); // Emoji as icon
                        VBox siteDetails = new VBox(2);
                        Label siteNameLabel = new Label(siteLabelPrefix + siteDisplayName);
                        siteNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                        if (isBooked) {
                            siteNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: -brand-green;");
                        }
                        Label siteLocLabel = new Label(siteDisplayLocation);
                        siteLocLabel.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px;");
                        siteDetails.getChildren().addAll(siteNameLabel, siteLocLabel);
                        siteInfo.getChildren().addAll(siteIcon, siteDetails);
                        
                        // Action Button
                        HBox actions = new HBox();
                        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                        Button bookBtn = new Button(isBooked ? "Update Booking" : "Book Appointment");
                        bookBtn.setStyle("-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-color: -brand-blue; -fx-text-fill: white; -fx-background-radius: 3; -fx-cursor: hand;");
                        bookBtn.setOnAction(e -> showBookingDialog(record, vaccineName));
                        actions.getChildren().add(bookBtn);
                        
                        card.getChildren().addAll(header, descLabel, new Separator(), siteInfo, actions);
                        recordsList.getChildren().add(card);
                    }
                    
                    childSection.getChildren().addAll(childName, recordsList);
                    contentBox.getChildren().add(childSection);
                }
            }
            
            if (!hasAnyUpcoming) {
                Label noUpcoming = new Label("No upcoming vaccinations scheduled for your children.");
                noUpcoming.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 14px;");
                contentBox.getChildren().add(noUpcoming);
            }
        }
        
        scheduleView.getChildren().addAll(title, contentBox);
        
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
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VaccinationRecords.fxml"));
            Parent view = loader.load();
            
            VaccinationRecordsController controller = loader.getController();
            controller.setVaccinationService(vaccinationService);
            controller.setChildService(childService);
            controller.setVaccineService(vaccineService);
            controller.setParentId(currentUser.getUserId());
            controller.setOnBackAction(() -> handleDashboardMenu());
            controller.initialize();
            
            contentArea.getChildren().setAll(view);
            VBox.setVgrow(view, Priority.ALWAYS);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showBookingDialog(VaccinationRecord record, String vaccineName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Book Appointment");
        dialog.setHeaderText("Schedule Vaccination: " + vaccineName);
        
        ButtonType bookButtonType = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Determine Assigned Site
        VaccinationSite assignedSite = null;
        if (record.getVaccinationSiteId() != null) {
            assignedSite = vaccinationSiteService.getSiteById(record.getVaccinationSiteId());
        }
        // Fallback if not assigned yet (e.g. pick first available as default)
        if (assignedSite == null) {
             List<VaccinationSite> sites = vaccinationSiteService.getAllSites();
             if (!sites.isEmpty()) {
                 assignedSite = sites.get(0);
             }
        }
        
        final VaccinationSite finalSite = assignedSite; // For lambda

        // Site Display (Read Only)
        TextField siteField = new TextField();
        siteField.setEditable(false);
        siteField.setStyle("-fx-background-color: #f4f4f4;"); // Grey out
        if (finalSite != null) {
            siteField.setText(finalSite.getName());
        } else {
            siteField.setText("No site assigned");
        }

        // Date Selection
        DatePicker datePicker = new DatePicker();
        if (record.getNextDueDate() != null) {
            datePicker.setValue(record.getNextDueDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        // Disable past dates and dates after due date
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean isPast = date.isBefore(LocalDate.now());
                boolean isAfterDue = record.getNextDueDate() != null && date.isAfter(record.getNextDueDate());
                setDisable(empty || isPast || isAfterDue);
            }
        });

        grid.add(new Label("Assigned Site:"), 0, 0);
        grid.add(siteField, 1, 0);
        grid.add(new Label("Select Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/Disable book button based on validation
        Node bookButton = dialog.getDialogPane().lookupButton(bookButtonType);
        bookButton.setDisable(finalSite == null); 
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
                return bookButtonType;
            }
            return null;
        });
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == bookButtonType) {
            LocalDate selectedDate = datePicker.getValue();
            
            if (finalSite != null && selectedDate != null) {
                // Update record
                record.setVaccinationSiteId(finalSite.getSiteId());
                // Note: In a real app, we would save the specific appointment date separately
                // For now, we update the site ID to indicate it's booked there.
                
                // Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Confirmed");
                alert.setHeaderText("Appointment Scheduled Successfully");
                alert.setContentText(String.format("Appointment for %s confirmed at %s on %s.", 
                    vaccineName, finalSite.getName(), selectedDate.format(DATE_FORMATTER)));
                alert.showAndWait();
                
                // Refresh the view
                handleScheduleMenu();
            }
        }
    }
    
    @FXML
    private void handleEditProfile() {
        if (!(currentUser instanceof com.vaccinetracker.model.Parent)) return;
        
        com.vaccinetracker.model.Parent parentUser = (com.vaccinetracker.model.Parent) currentUser;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setText(parentUser.getName());
        
        TextField contactField = new TextField();
        contactField.setText(parentUser.getContactInfo());
        
        TextField addressField = new TextField();
        addressField.setText(parentUser.getAddress());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact Info:"), 0, 1);
        grid.add(contactField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                String newName = nameField.getText();
                String newContact = contactField.getText();
                String newAddress = addressField.getText();
                
                if (!newName.isEmpty()) {
                    parentUser.setName(newName);
                    welcomeLabel.setText(newName);
                }
                
                if (!newContact.isEmpty()) {
                    parentUser.setContactInfo(newContact);
                }
                
                if (!newAddress.isEmpty()) {
                    parentUser.setAddress(newAddress);
                }
                
                // Update guardian info for all children
                childService.updateGuardianInfo(parentUser.getUserId(), parentUser.getName(), parentUser.getContactInfo());
                
                // Refresh view if needed
                if (childrenMenuButton.getStyleClass().contains("active")) {
                    handleChildrenMenu();
                }
            }
        });
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
        
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
