package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.Vaccinator;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.Vaccine;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.VaccineService;
import com.vaccinetracker.services.StorageService;
import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.services.VaccinationSiteService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import com.vaccinetracker.services.AlertService;

public class VaccinatorController {

    @FXML private Label vaccinatorNameLabel;
    @FXML private Label totalVaccinatedLabel;
    @FXML private Label siteLabel;
    @FXML private TableView<VaccinationRecord> pendingTable;
    @FXML private TableColumn<VaccinationRecord, String> childNameCol;
    @FXML private TableColumn<VaccinationRecord, String> vaccineCol;
    @FXML private TableColumn<VaccinationRecord, String> dateCol;
    @FXML private TableColumn<VaccinationRecord, String> statusCol;
    @FXML private TableColumn<VaccinationRecord, Void> actionCol;
    
    @FXML private Button dashboardMenuButton;
    @FXML private Button recordsMenuButton;
    @FXML private Button profileMenuButton;
    @FXML private VBox mainContent;

    private Vaccinator currentVaccinator;
    private VaccinationService vaccinationService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationSiteService vaccinationSiteService;
    
    private List<Node> dashboardContent;

    @FXML
    public void initialize() {
        try {
            // Initialize services
            childService = new ChildService();
            vaccineService = new VaccineService();
            vaccinationService = new VaccinationService(childService, vaccineService);
            vaccinationSiteService = new VaccinationSiteService();
            
            setupTable();
            
            // Save initial dashboard content
            if (mainContent != null) {
                dashboardContent = new ArrayList<>(mainContent.getChildren());
            }
        } catch (Exception e) {
            System.err.println("Error in VaccinatorController.initialize():");
            e.printStackTrace();
            throw e; // Re-throw to let FXML loader know
        }
    }

    public void setVaccinator(Vaccinator vaccinator) {
        this.currentVaccinator = vaccinator;
        if (vaccinator != null) {
            vaccinatorNameLabel.setText(vaccinator.getName());
            
            // Get site name
            VaccinationSite site = vaccinationSiteService.getSiteById(vaccinator.getSiteId());
            
            // Fallback for legacy data mismatch (SITE-001 vs SITE0001)
            if (site == null && "SITE-001".equals(vaccinator.getSiteId())) {
                site = vaccinationSiteService.getSiteById("SITE0001");
            }
            
            String siteName = (site != null) ? site.getName() : vaccinator.getSiteId();
            siteLabel.setText(siteName);
            
            updateStats();
            loadPendingVaccinations();
        }
    }
    
    private void setupTable() {
        childNameCol.setCellValueFactory(cellData -> {
            Child child = childService.getChildById(cellData.getValue().getChildId());
            return new SimpleStringProperty(child != null ? child.getName() : "Unknown");
        });
        
        vaccineCol.setCellValueFactory(cellData -> {
            Vaccine vaccine = vaccineService.getVaccineById(cellData.getValue().getVaccineId());
            return new SimpleStringProperty(vaccine != null ? vaccine.getName() : cellData.getValue().getVaccineId());
        });
        
        dateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNextDueDate().toString()));
            
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        // Remove default blue selection style
        pendingTable.setSelectionModel(null);
            
        addButtonToTable();
    }

    private void addButtonToTable() {
        javafx.util.Callback<TableColumn<VaccinationRecord, Void>, TableCell<VaccinationRecord, Void>> cellFactory = new javafx.util.Callback<>() {
            @Override
            public TableCell<VaccinationRecord, Void> call(final TableColumn<VaccinationRecord, Void> param) {
                final TableCell<VaccinationRecord, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Vaccinated");

                    {
                        btn.getStyleClass().add("action-button");
                        btn.setOnAction((event) -> {
                            VaccinationRecord record = getTableView().getItems().get(getIndex());
                            handleVaccinate(record);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        actionCol.setCellFactory(cellFactory);
    }
    
    private void handleVaccinate(VaccinationRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Vaccination");
        alert.setHeaderText("Approve Vaccination");
        alert.setContentText("Mark this vaccination as COMPLETED?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            vaccinationService.recordVaccination(record.getRecordId(), LocalDate.now(), currentVaccinator.getSiteId());
            StorageService.saveAll();
            loadPendingVaccinations();
            updateStats();
        }
    }

    private void loadPendingVaccinations() {
        List<VaccinationRecord> allRecords = vaccinationService.getAllRecords();
        List<VaccinationRecord> pending = allRecords.stream()
            .filter(r -> r.getStatus() == VaccinationRecord.VaccinationStatus.PENDING)
            // Filter by site: Only show records assigned to this vaccinator's site
            .filter(r -> r.getVaccinationSiteId() != null && 
                         r.getVaccinationSiteId().equals(currentVaccinator.getSiteId()))
            .collect(Collectors.toList());
            
        pendingTable.setItems(FXCollections.observableArrayList(pending));
    }
    
    private void updateStats() {
        List<VaccinationRecord> allRecords = vaccinationService.getAllRecords();
        long count = allRecords.stream()
            .filter(r -> r.getStatus() == VaccinationRecord.VaccinationStatus.COMPLETED && 
                         currentVaccinator.getSiteId().equals(r.getVaccinationSiteId()))
            .count();
            
        totalVaccinatedLabel.setText(String.valueOf(count));
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            App.setRoot(root, "CoreVax - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDashboardMenu() {
        if (dashboardContent != null) {
            mainContent.getChildren().setAll(dashboardContent);
            updateActiveMenuButton(dashboardMenuButton);
            // Refresh data
            updateStats();
            loadPendingVaccinations();
        }
    }
    
    @FXML
    private void handleRecordsMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChildrenView.fxml"));
            Node childrenView = loader.load();
            
            // We need to customize the ChildrenView for Vaccinator
            // Since ChildrenController is designed for Admin, we might need to adapt it
            // Or better, reuse the VaccinationRecordsController but with search capability
            
            // Let's stick to the user request: "make vaccination records tab of the vaccinaor user to the vaccination records of the admin childrens tab"
            // This implies loading ChildrenView.fxml but maybe hiding non-relevant tabs?
            
            ChildrenController controller = loader.getController();
            controller.setChildService(childService);
            controller.setVaccinationService(vaccinationService);
            controller.setVaccineService(vaccineService);
            controller.setVaccinationSiteService(vaccinationSiteService);
            controller.setAlertService(new AlertService()); // Dummy alert service to prevent NPE
            
            // Customize view for Vaccinator
            TabPane tabPane = (TabPane) childrenView.lookup("#childrenTabPane");
            if (tabPane != null) {
                // Remove Notify Tab
                tabPane.getTabs().removeIf(tab -> "notifyTab".equals(tab.getId()));
                
                // Select Vaccination Records tab
                for (Tab tab : tabPane.getTabs()) {
                    if ("vaccinationRecordsTab".equals(tab.getId())) {
                        tabPane.getSelectionModel().select(tab);
                        break;
                    }
                }
            }
            
            // Hide Add Record Section (Read-only mode)
            controller.setReadOnlyMode(true);
            
            mainContent.getChildren().setAll(childrenView);
            updateActiveMenuButton(recordsMenuButton);
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load records");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void updateActiveMenuButton(Button activeButton) {
        dashboardMenuButton.getStyleClass().remove("active");
        recordsMenuButton.getStyleClass().remove("active");
        profileMenuButton.getStyleClass().remove("active");
        
        activeButton.getStyleClass().add("active");
    }
    
    @FXML
    private void handleProfileMenu() {
        handleEditProfile();
    }
    
    private void handleEditProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(currentVaccinator.getName());
        TextField contactField = new TextField(currentVaccinator.getContactInfo());
        TextField addressField = new TextField(currentVaccinator.getAddress());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(currentVaccinator.getPassword());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact Info:"), 0, 1);
        grid.add(contactField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                currentVaccinator.setName(nameField.getText());
                currentVaccinator.setContactInfo(contactField.getText());
                currentVaccinator.setAddress(addressField.getText());
                if (!passwordField.getText().isEmpty()) {
                    currentVaccinator.setPassword(passwordField.getText());
                }
                
                vaccinatorNameLabel.setText(currentVaccinator.getName());
                StorageService.saveAll();
            }
        });
    }
}
