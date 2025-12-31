package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.Vaccine;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccineService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Vaccination Records view.
 * Displays vaccination records in a table format.
 */
public class VaccinationRecordsController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private TableView<RecordDisplay> recordsTable;
    
    @FXML
    private TableColumn<RecordDisplay, String> childNameColumn;
    
    @FXML
    private TableColumn<RecordDisplay, String> vaccineNameColumn;
    
    @FXML
    private TableColumn<RecordDisplay, String> statusColumn;
    
    @FXML
    private TableColumn<RecordDisplay, String> dateAdministeredColumn;
    
    @FXML
    private TableColumn<RecordDisplay, String> nextDueDateColumn;
    
    @FXML
    private TableColumn<RecordDisplay, String> ageColumn;
    
    @FXML
    private Label noRecordsLabel;
    
    @FXML
    private Label summaryLabel;
    
    @FXML
    private Button backButton;
    
    // Services
    private VaccinationService vaccinationService;
    private ChildService childService;
    private VaccineService vaccineService;
    
    // View configuration
    private boolean isAdminView = false;
    private String parentId = null;
    private String childId = null;
    private Runnable onBackAction = null;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    // Observable list for table data
    private ObservableList<RecordDisplay> recordsData = FXCollections.observableArrayList();
    
    /**
     * Inner class to represent a record for display in the table.
     */
    public static class RecordDisplay {
        private String childName;
        private String vaccineName;
        private String status;
        private String dateAdministered;
        private String nextDueDate;
        private String age;
        
        public RecordDisplay(String childName, String vaccineName, String status,
                           String dateAdministered, String nextDueDate, String age) {
            this.childName = childName;
            this.vaccineName = vaccineName;
            this.status = status;
            this.dateAdministered = dateAdministered;
            this.nextDueDate = nextDueDate;
            this.age = age;
        }
        
        // Getters for table columns
        public String getChildName() { return childName; }
        public String getVaccineName() { return vaccineName; }
        public String getStatus() { return status; }
        public String getDateAdministered() { return dateAdministered; }
        public String getNextDueDate() { return nextDueDate; }
        public String getAge() { return age; }
    }
    
    /**
     * Set the vaccination service.
     */
    public void setVaccinationService(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }
    
    /**
     * Set the child service.
     */
    public void setChildService(ChildService childService) {
        this.childService = childService;
    }
    
    /**
     * Set the vaccine service.
     */
    public void setVaccineService(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }
    
    /**
     * Set whether this is an admin view.
     */
    public void setIsAdminView(boolean isAdminView) {
        this.isAdminView = isAdminView;
    }
    
    public void setVaccinatorMode() {
        this.isAdminView = true; // Reuse admin logic for data loading
        if (backButton != null) {
            backButton.setVisible(false);
            backButton.setManaged(false);
        }
        if (titleLabel != null) {
            titleLabel.setText("All Vaccination Records");
        }
    }
    
    /**
     * Set the parent ID for filtering (parent view only).
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Set the child ID for filtering (specific child view).
     */
    public void setChildId(String childId) {
        this.childId = childId;
    }

    /**
     * Set a custom action for the back button.
     */
    public void setOnBackAction(Runnable onBackAction) {
        this.onBackAction = onBackAction;
    }
    
    /**
     * Initialize the controller and load data.
     */
    @FXML
    public void initialize() {
        // Set up table columns
        childNameColumn.setCellValueFactory(new PropertyValueFactory<>("childName"));
        vaccineNameColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateAdministeredColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdministered"));
        nextDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextDueDate"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        // Align columns
        childNameColumn.setStyle("-fx-alignment: CENTER;");
        vaccineNameColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");
        dateAdministeredColumn.setStyle("-fx-alignment: CENTER;");
        nextDueDateColumn.setStyle("-fx-alignment: CENTER;");
        ageColumn.setStyle("-fx-alignment: CENTER;");
        
        // Set table data
        recordsTable.setItems(recordsData);
        
        // Load records
        loadRecords();
        
        // Update title
        if (isAdminView) {
            titleLabel.setText("All Vaccination Records (Admin View)");
        } else if (childId != null) {
            Child child = childService.getChildById(childId);
            if (child != null) {
                titleLabel.setText(child.getName() + "'s Vaccination Records");
            } else {
                titleLabel.setText("Vaccination Records");
            }
        } else {
            titleLabel.setText("My Children's Vaccination Records");
        }
    }
    
    /**
     * Load vaccination records into the table.
     */
    private void loadRecords() {
        recordsData.clear();
        
        List<VaccinationRecord> records;
        
        if (isAdminView) {
            // Admin view: show all records
            records = vaccinationService.getAllRecords();
        } else {
            // Parent view: show only records for their children
            if (childId != null) {
                records = vaccinationService.getRecordsByChild(childId);
            } else if (parentId != null) {
                List<Child> children = childService.getChildrenByParent(parentId);
                records = new java.util.ArrayList<>();
                
                for (Child child : children) {
                    records.addAll(vaccinationService.getRecordsByChild(child.getChildId()));
                }
            } else {
                noRecordsLabel.setVisible(true);
                return;
            }
        }
        
        if (records.isEmpty()) {
            noRecordsLabel.setVisible(true);
            summaryLabel.setText("No vaccination records found.");
            return;
        }
        
        noRecordsLabel.setVisible(false);
        
        // Convert records to display objects
        int pendingCount = 0;
        int completedCount = 0;
        int overdueCount = 0;
        
        for (VaccinationRecord record : records) {
            Child child = childService.getChildById(record.getChildId());
            Vaccine vaccine = vaccineService.getVaccineById(record.getVaccineId());
            
            String childName = (child != null) ? child.getName() : record.getChildId();
            String vaccineName = (vaccine != null) ? vaccine.getName() : record.getVaccineId();
            
            String status = record.getStatus().toString();
            if (record.isOverdue()) {
                status = "OVERDUE";
                overdueCount++;
            } else if (record.getStatus() == VaccinationRecord.VaccinationStatus.PENDING) {
                pendingCount++;
            } else {
                completedCount++;
            }
            
            String dateAdministeredStr = (record.getDateAdministered() != null)
                ? record.getDateAdministered().format(DATE_FORMATTER)
                : "-";
            
            String nextDueDateStr = (record.getNextDueDate() != null)
                ? record.getNextDueDate().format(DATE_FORMATTER)
                : "-";
            
            String ageStr = (child != null) ? String.valueOf(child.getAgeInDays()) : "-";
            
            RecordDisplay display = new RecordDisplay(childName, vaccineName, status,
                                                     dateAdministeredStr, nextDueDateStr, ageStr);
            recordsData.add(display);
        }
        
        // Update summary
        summaryLabel.setText(String.format(
            "Total: %d records | Completed: %d | Pending: %d | Overdue: %d",
            records.size(), completedCount, pendingCount, overdueCount
        ));
    }
    
    /**
     * Handle back button click.
     */
    @FXML
    private void handleBack() {
        if (onBackAction != null) {
            onBackAction.run();
            return;
        }

        try {
            if (isAdminView) {
                // Go back to admin dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
                Parent root = loader.load();
                
                AdminController controller = loader.getController();
                controller.setCurrentUser(null);
                controller.initialize();
                
                App.setRoot(root, "Admin Dashboard - Vaccine Tracker");
            } else {
                // Go back to parent dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ParentDashboard.fxml"));
                Parent root = loader.load();
                
                ParentController controller = loader.getController();
                controller.setUserService(null);
                controller.setCurrentUser(null);
                controller.initialize();
                
                App.setRoot(root, "Parent Dashboard - Vaccine Tracker");
            }
        } catch (IOException e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

