package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.User;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.HumanResourceService;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javafx.util.Pair;

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
    private Button dashboardMenuButton;

    @FXML
    private Button childrenMenuButton;

    @FXML
    private Button sitesMenuButton;

    @FXML
    private Button inventoryMenuButton;

    @FXML
    private Button hrMenuButton;

    @FXML
    private Button alertsMenuButton;

    @FXML
    private VBox mainContent;

    @FXML
    private LineChart<String, Number> vaccinationChart;
    
    @FXML private javafx.scene.control.Label doctorCountLabel;
    @FXML private javafx.scene.control.Label nurseCountLabel;
    @FXML private javafx.scene.control.Label staffCountLabel;
    @FXML private javafx.scene.control.Label childCountLabel;
    @FXML private javafx.scene.control.Label siteCountLabel;
    @FXML private javafx.scene.control.Label vaccineCountLabel;
    
    @FXML private ComboBox<Integer> yearComboBox;

    private java.util.List<Node> dashboardContent;
    
    // Data structure for chart: Year -> Month Index (0-11) -> Pair(Boys, Girls)
    private Map<Integer, Map<Integer, Pair<Integer, Integer>>> chartData = new HashMap<>();
    private final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // Current user and services
    private User currentUser;
    private UserService userService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationService vaccinationService;
    private VaccinationSiteService vaccinationSiteService;
    private AlertService alertService;
    private HumanResourceService hrService;
    
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
        hrService = new HumanResourceService(vaccinationSiteService);
        
        updateDashboardStats();

        // Save initial dashboard content
        if (mainContent != null) {
            dashboardContent = new java.util.ArrayList<>(mainContent.getChildren());
        }

        // Initialize Chart Data
        initializeChartData();
        
        // Setup Year ComboBox
        if (yearComboBox != null) {
            yearComboBox.getItems().addAll(2023, 2024, 2025, 2026);
            yearComboBox.setValue(2025);
            yearComboBox.setOnAction(e -> updateChart(yearComboBox.getValue()));
        }

        // Populate Chart
        if (vaccinationChart != null) {
            updateChart(2025);
        }
    }
    
    private void initializeChartData() {
        Random random = new Random();
        int[] years = {2023, 2024, 2025};
        
        for (int year : years) {
            Map<Integer, Pair<Integer, Integer>> yearData = new HashMap<>();
            for (int i = 0; i < 12; i++) {
                // Generate random data between 8 and 13
                int boys = 8 + random.nextInt(6); // 8 to 13
                int girls = 8 + random.nextInt(6); // 8 to 13
                yearData.put(i, new Pair<>(boys, girls));
            }
            chartData.put(year, yearData);
        }
    }
    
    private void updateChart(int year) {
        if (vaccinationChart == null) return;
        
        vaccinationChart.getData().clear();
        vaccinationChart.setTitle("Children Vaccinated (" + year + ")");
        
        XYChart.Series<String, Number> maleSeries = new XYChart.Series<>();
        maleSeries.setName("Boys");
        
        XYChart.Series<String, Number> femaleSeries = new XYChart.Series<>();
        femaleSeries.setName("Girls");
        
        Map<Integer, Pair<Integer, Integer>> yearData = chartData.get(year);
        if (yearData != null) {
            for (int i = 0; i < 12; i++) {
                String month = MONTHS[i];
                Pair<Integer, Integer> data = yearData.get(i);
                int boys = (data != null) ? data.getKey() : 0;
                int girls = (data != null) ? data.getValue() : 0;
                maleSeries.getData().add(new XYChart.Data<>(month, boys));
                femaleSeries.getData().add(new XYChart.Data<>(month, girls));
            }
        }
        
        vaccinationChart.getData().addAll(maleSeries, femaleSeries);
    }
    
    @FXML
    private void handleAddReport() {
        Dialog<Pair<Integer, Pair<String, Pair<Integer, Integer>>>> dialog = new Dialog<>();
        dialog.setTitle("Add Monthly Report");
        dialog.setHeaderText("Enter vaccination data");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Integer> yearComboDialog = new ComboBox<>();
        yearComboDialog.getItems().addAll(yearComboBox.getItems());
        yearComboDialog.setValue(yearComboBox.getValue());

        ComboBox<String> monthCombo = new ComboBox<>();
        monthCombo.getItems().addAll(MONTHS);
        monthCombo.setValue(MONTHS[0]);

        TextField boysField = new TextField();
        boysField.setPromptText("Boys Count");
        TextField girlsField = new TextField();
        girlsField.setPromptText("Girls Count");

        grid.add(new Label("Year:"), 0, 0);
        grid.add(yearComboDialog, 1, 0);
        grid.add(new Label("Month:"), 0, 1);
        grid.add(monthCombo, 1, 1);
        grid.add(new Label("Boys:"), 0, 2);
        grid.add(boysField, 1, 2);
        grid.add(new Label("Girls:"), 0, 3);
        grid.add(girlsField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Disable Add button until valid inputs are provided
        javafx.scene.Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        java.util.function.Predicate<String> isNonNegativeInteger = s -> {
            if (s == null || s.isEmpty()) return false;
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isDigit(s.charAt(i))) return false;
            }
            return true;
        };

        Runnable validateInputs = () -> {
            boolean valid = isNonNegativeInteger.test(boysField.getText()) &&
                            isNonNegativeInteger.test(girlsField.getText()) &&
                            monthCombo.getValue() != null &&
                            yearComboDialog.getValue() != null;
            addButton.setDisable(!valid);
        };

        boysField.textProperty().addListener((obs, o, n) -> validateInputs.run());
        girlsField.textProperty().addListener((obs, o, n) -> validateInputs.run());
        monthCombo.valueProperty().addListener((obs, o, n) -> validateInputs.run());
        yearComboDialog.valueProperty().addListener((obs, o, n) -> validateInputs.run());
        validateInputs.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int year = yearComboDialog.getValue();
                    int boys = Integer.parseInt(boysField.getText());
                    int girls = Integer.parseInt(girlsField.getText());
                    return new Pair<>(year, new Pair<>(monthCombo.getValue(), new Pair<>(boys, girls)));
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        java.util.Optional<Pair<Integer, Pair<String, Pair<Integer, Integer>>>> result = dialog.showAndWait();

        result.ifPresent(report -> {
            int selectedYear = report.getKey();
            String monthName = report.getValue().getKey();
            Pair<Integer, Integer> counts = report.getValue().getValue();
            
            int monthIndex = -1;
            for (int i = 0; i < MONTHS.length; i++) {
                if (MONTHS[i].equals(monthName)) {
                    monthIndex = i;
                    break;
                }
            }
            
            if (monthIndex != -1) {
                Map<Integer, Pair<Integer, Integer>> yearData = chartData.get(selectedYear);
                if (yearData == null) {
                    yearData = new HashMap<>();
                    chartData.put(selectedYear, yearData);
                }
                
                yearData.put(monthIndex, counts);
                
                if (selectedYear == yearComboBox.getValue()) {
                    updateChart(selectedYear);
                } else {
                    yearComboBox.setValue(selectedYear);
                }
            } else {
                // Fallback alert if month resolution failed
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Invalid Month");
                alert.setHeaderText(null);
                alert.setContentText("Could not determine selected month. Please try again.");
                alert.showAndWait();
            }
        });
    }
    
    /**
     * Handle dashboard menu button click.
     */
    @FXML
    private void handleDashboardMenu() {
        System.out.println("Dashboard menu clicked");
        if (mainContent != null && dashboardContent != null) {
            mainContent.getChildren().setAll(dashboardContent);
            updateDashboardStats();
            updateActiveMenuButton(dashboardMenuButton);
        }
    }

    private void updateDashboardStats() {
        if (doctorCountLabel != null && hrService != null) {
            doctorCountLabel.setText(String.valueOf(hrService.getStaffByRole("Doctor").size()));
        }
        if (nurseCountLabel != null && hrService != null) {
            nurseCountLabel.setText(String.valueOf(hrService.getStaffByRole("Nurse").size()));
        }
        if (staffCountLabel != null && hrService != null) {
            staffCountLabel.setText(String.valueOf(hrService.getStaffByRole("Other").size()));
        }
        if (childCountLabel != null && childService != null) {
            childCountLabel.setText(String.valueOf(childService.getAllChildren().size()));
        }
        if (siteCountLabel != null && vaccinationSiteService != null) {
            siteCountLabel.setText(String.valueOf(vaccinationSiteService.getAllSites().size()));
        }
        if (vaccineCountLabel != null && vaccineService != null) {
            vaccineCountLabel.setText(String.valueOf(vaccineService.getVaccineCount()));
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
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Error loading Children View: " + e.getMessage() + "\nSee console for stack trace.").showAndWait();
        }
    }

    private void updateActiveMenuButton(Button activeButton) {
        // Reset all buttons (simplified)
        // In a real app, you'd have a list of buttons or a toggle group
        if (dashboardMenuButton != null) dashboardMenuButton.getStyleClass().remove("active");
        childrenMenuButton.getStyleClass().remove("active");
        sitesMenuButton.getStyleClass().remove("active");
        inventoryMenuButton.getStyleClass().remove("active");
        hrMenuButton.getStyleClass().remove("active");
        alertsMenuButton.getStyleClass().remove("active");
        
        // Set active
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    /**
     * Handle manage vaccines/inventory button click.
     */
    @FXML
    private void handleManageVaccines() {
        System.out.println("Inventory menu clicked");
        try {
            if (mainContent == null) {
                System.err.println("ERROR: mainContent is null!");
                return;
            }
            
            System.out.println("Loading InventoryView.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InventoryView.fxml"));
            Node inventoryView = loader.load();
            System.out.println("InventoryView loaded successfully");
            
            InventoryController controller = loader.getController();
            if (controller == null) {
                System.err.println("ERROR: InventoryController is null!");
            } else {
                System.out.println("Initializing InventoryController...");
                controller.setServices(vaccinationSiteService, vaccineService);
            }
            
            // Replace main content
            mainContent.getChildren().setAll(inventoryView);
            System.out.println("Main content updated");
            
            // Update active state of buttons
            updateActiveMenuButton(inventoryMenuButton);
            
        } catch (Exception e) {
            System.err.println("Error loading inventory view: " + e.getMessage());
            e.printStackTrace();
        }
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
            
            App.setRoot(root, "Vaccination Records - CoreVax");
        } catch (IOException e) {
            System.err.println("Error loading vaccination records view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle human resources button click.
     */
    @FXML
    private void handleHumanResources() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HumanResourceView.fxml"));
            Node view = loader.load();
            
            HumanResourceController controller = loader.getController();
            controller.setServices(hrService, vaccinationSiteService);
            
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
            
            // Update active state of buttons
            updateActiveMenuButton(hrMenuButton);
            
        } catch (Exception e) {
            System.err.println("Error loading HR view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle view health alerts button click.
     */
    @FXML
    private void handleViewHealthAlerts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HealthAlertsView.fxml"));
            Node view = loader.load();
            
            HealthAlertsController controller = loader.getController();
            controller.setAlertService(alertService);
            
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
            
            // Update active state of buttons
            updateActiveMenuButton(alertsMenuButton);
            
        } catch (Exception e) {
            System.err.println("Error loading Health Alerts view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle back button click.
     * Navigates back to the main dashboard view.
     */
    @FXML
    private void handleBack() {
        handleDashboardMenu();
    }

    /**
     * Handle logout button click.
     */
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            App.setRoot(root, "CoreVax - Login");
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

