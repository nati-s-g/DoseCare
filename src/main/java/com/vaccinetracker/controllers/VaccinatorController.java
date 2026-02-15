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
import com.vaccinetracker.services.AppointmentService;
import com.vaccinetracker.model.Appointment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    @FXML private Button scheduleMenuButton;
    @FXML private Button appointmentsMenuButton;
    @FXML private Button profileMenuButton;
    @FXML private VBox mainContent;

    private Vaccinator currentVaccinator;
    private VaccinationService vaccinationService;
    private ChildService childService;
    private VaccineService vaccineService;
    private VaccinationSiteService vaccinationSiteService;
    private AppointmentService appointmentService;
    
    private List<Node> dashboardContent;

    @FXML
    public void initialize() {
        try {
            // Initialize services
            childService = new ChildService();
            vaccineService = new VaccineService();
            vaccinationService = new VaccinationService(childService, vaccineService);
            vaccinationSiteService = new VaccinationSiteService();
            appointmentService = new AppointmentService();
            
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
    private void handleAppointmentsMenu() {
        if (mainContent == null) return;
        
        dashboardMenuButton.getStyleClass().remove("active");
        recordsMenuButton.getStyleClass().remove("active");
        profileMenuButton.getStyleClass().remove("active");
        appointmentsMenuButton.getStyleClass().add("active");
        
        VBox appointmentsView = new VBox(20);
        appointmentsView.setPadding(new Insets(20));
        
        Label title = new Label("Manage Appointments");
        title.getStyleClass().add("chart-title");
        
        Label subtitle = new Label("Review requests and manage existing appointments");
        subtitle.setStyle("-fx-text-fill: -text-muted;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        VBox list = new VBox(10);
        list.setStyle("-fx-padding: 0 15 0 0;");
        
        Runnable refreshList = () -> {
            list.getChildren().clear();
            List<Appointment> allAppointments = appointmentService.getAllAppointments();
            
            // Filter: Show REQUESTED, CONFIRMED (to complete), and COMPLETED (for history maybe? let's stick to actionable)
            // Let's show everything but sort by importance: REQUESTED first, then CONFIRMED
            // FILTER BY SITE ID
            
            allAppointments.removeIf(a -> a.getSiteId() != null && !a.getSiteId().equals(currentVaccinator.getSiteId()));
            
            allAppointments.sort((a1, a2) -> {
                if (a1.getStatus() == Appointment.AppointmentStatus.REQUESTED && a2.getStatus() != Appointment.AppointmentStatus.REQUESTED) return -1;
                if (a1.getStatus() != Appointment.AppointmentStatus.REQUESTED && a2.getStatus() == Appointment.AppointmentStatus.REQUESTED) return 1;
                return a1.getDate().compareTo(a2.getDate());
            });
            
            if (allAppointments.isEmpty()) {
                list.getChildren().add(new Label("No appointments found."));
            } else {
                for (Appointment appt : allAppointments) {
                    HBox card = new HBox(15);
                    card.setPadding(new Insets(15));
                    card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                    card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    
                    VBox info = new VBox(5);
                    Child child = childService.getChildById(appt.getChildId());
                    Label childName = new Label(child != null ? child.getName() : "Unknown Child");
                    childName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    Label detailsLabel = new Label(appt.getVaccineName() + " @ " + appt.getDate() + " " + appt.getTime());
                    Label notesLabel = new Label("Notes: " + appt.getNotes());
                    notesLabel.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px;");
                    
                    info.getChildren().addAll(childName, detailsLabel, notesLabel);
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    HBox actions = new HBox(8);
                    actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                    
                    if (appt.getStatus() == Appointment.AppointmentStatus.REQUESTED) {
                        Button approveBtn = new Button("Approve");
                        approveBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-cursor: hand;");
                        approveBtn.setOnAction(e -> {
                            appointmentService.updateStatus(appt.getAppointmentId(), Appointment.AppointmentStatus.CONFIRMED);
                             // Reload list using a hacky self-reference or just calling the method again?
                             // Since we are inside the runnable, we can call it recursively if we assigned it to a var, 
                             // but simpler is to just run the handleAppointmentsMenu() again or clear/repop.
                             handleAppointmentsMenu();
                        });
                        
                        Button declineBtn = new Button("Decline");
                        declineBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-cursor: hand;");
                        declineBtn.setOnAction(e -> {
                             appointmentService.updateStatus(appt.getAppointmentId(), Appointment.AppointmentStatus.CANCELLED);
                             handleAppointmentsMenu();
                        });
                        actions.getChildren().addAll(approveBtn, declineBtn);
                        
                    } else if (appt.getStatus() == Appointment.AppointmentStatus.CONFIRMED) {
                        Label statusLabel = new Label("CONFIRMED");
                        statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-padding: 0 10 0 0;");
                        
                        Button completeBtn = new Button("Complete");
                        completeBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-cursor: hand;");
                        completeBtn.setOnAction(e -> {
                             appointmentService.updateStatus(appt.getAppointmentId(), Appointment.AppointmentStatus.COMPLETED);
                             handleAppointmentsMenu();
                        });
                        actions.getChildren().addAll(statusLabel, completeBtn);
                        
                    } else {
                        Label statusLabel = new Label(appt.getStatus().toString());
                        String color = appt.getStatus() == Appointment.AppointmentStatus.COMPLETED ? "#1976d2" : "#757575";
                        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                        actions.getChildren().add(statusLabel);
                    }
                    
                    card.getChildren().addAll(info, spacer, actions);
                    list.getChildren().add(card);
                }
            }
        };
        
        refreshList.run();
        
        scrollPane.setContent(list);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        appointmentsView.getChildren().addAll(title, subtitle, scrollPane);
        mainContent.getChildren().setAll(appointmentsView);
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
