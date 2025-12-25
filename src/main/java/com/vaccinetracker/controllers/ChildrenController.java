package com.vaccinetracker.controllers;

import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.HealthAlert;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.Vaccine;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccineService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChildrenController {

    @FXML
    private TabPane childrenTabPane;
    @FXML
    private Tab registeredChildrenTab;
    @FXML
    private Tab registerChildTab;
    @FXML
    private Tab notifyTab;
    @FXML
    private Tab vaccinationRecordsTab;

    @FXML
    private TableView<Child> childrenTable;
    @FXML
    private TableColumn<Child, String> childIdColumn;
    @FXML
    private TableColumn<Child, String> nameColumn;
    @FXML
    private TableColumn<Child, String> dobColumn;
    @FXML
    private TableColumn<Child, String> ageColumn;
    @FXML
    private TableColumn<Child, String> genderColumn;
    @FXML
    private TableColumn<Child, String> guardianColumn;
    @FXML
    private TableColumn<Child, String> contactColumn;

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField guardianField;
    @FXML
    private TextField contactField;

    // Notify Tab Controls
    @FXML
    private TableView<ChildSelectionWrapper> notifyTable;
    @FXML
    private TableColumn<ChildSelectionWrapper, Boolean> selectColumn;
    @FXML
    private TableColumn<ChildSelectionWrapper, String> notifyNameColumn;
    @FXML
    private TableColumn<ChildSelectionWrapper, String> notifyIdColumn;
    
    @FXML
    private ComboBox<Vaccine> notifyVaccineComboBox;
    @FXML
    private TextArea explanationArea;

    // Vaccination Records Tab Controls
    @FXML
    private TableView<ChildSelectionWrapper> vaccinationChildTable;
    @FXML
    private TableColumn<ChildSelectionWrapper, Boolean> vcSelectColumn;
    @FXML
    private TableColumn<ChildSelectionWrapper, String> vcChildIdColumn;
    @FXML
    private TableColumn<ChildSelectionWrapper, String> vcChildNameColumn;
    
    @FXML
    private TableView<VaccinationRecord> vaccinationRecordTable;
    @FXML
    private TableColumn<VaccinationRecord, String> vrVaccineColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrDueDateColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrStatusColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrAdminDateColumn;
    
    @FXML
    private ComboBox<Vaccine> vaccineComboBox;
    @FXML
    private DatePicker vaccineDueDatePicker;

    private ChildService childService;
    private AlertService alertService;
    private VaccinationService vaccinationService;
    private VaccineService vaccineService;
    
    private ObservableList<Child> childrenList = FXCollections.observableArrayList();
    private ObservableList<ChildSelectionWrapper> notifyList = FXCollections.observableArrayList();
    private ObservableList<ChildSelectionWrapper> vaccinationChildList = FXCollections.observableArrayList();
    private ObservableList<VaccinationRecord> vaccinationRecordsList = FXCollections.observableArrayList();

    public void setChildService(ChildService childService) {
        this.childService = childService;
        loadChildren();
    }

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }

    public void setVaccinationService(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    public void setVaccineService(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
        loadVaccines();
    }

    @FXML
    public void initialize() {
        // Initialize Table Columns
        childIdColumn.setCellValueFactory(new PropertyValueFactory<>("childId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirthString")); 
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("ageString")); 
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        guardianColumn.setCellValueFactory(new PropertyValueFactory<>("guardianName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("guardianContact"));

        // Initialize Gender ComboBox
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female"));
        
        // Bind list to table
        childrenTable.setItems(childrenList);

        // Initialize Notify Table
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        notifyNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getChild().getName()));
        notifyIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getChild().getChildId()));
        
        notifyTable.setItems(notifyList);
        notifyTable.setEditable(true);

        // Initialize Vaccination Records Tab
        vcSelectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        vcSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(vcSelectColumn));
        vcChildIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChild().getChildId()));
        vcChildNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChild().getName()));
        vaccinationChildTable.setItems(vaccinationChildList);
        vaccinationChildTable.setEditable(true);
        
        // Listener for child selection in Vaccination Tab
        vaccinationChildTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadVaccinationRecords(newSelection.getChild().getChildId());
            } else {
                vaccinationRecordsList.clear();
            }
        });

        vrVaccineColumn.setCellValueFactory(cellData -> {
            if (vaccineService != null) {
                Vaccine v = vaccineService.getVaccineById(cellData.getValue().getVaccineId());
                return new SimpleStringProperty(v != null ? v.getName() : cellData.getValue().getVaccineId());
            }
            return new SimpleStringProperty(cellData.getValue().getVaccineId());
        });
        vrDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextDueDate"));
        vrStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        vrAdminDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdministered"));
        
        vaccinationRecordTable.setItems(vaccinationRecordsList);
        
        // Configure Vaccine ComboBox
        StringConverter<Vaccine> vaccineConverter = new StringConverter<Vaccine>() {
            @Override
            public String toString(Vaccine object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Vaccine fromString(String string) {
                return null; // Not needed for this use case
            }
        };
        vaccineComboBox.setConverter(vaccineConverter);
        notifyVaccineComboBox.setConverter(vaccineConverter);
    }

    private void loadVaccines() {
        if (vaccineService != null) {
            ObservableList<Vaccine> vaccines = FXCollections.observableArrayList(vaccineService.getAllVaccines());
            vaccineComboBox.setItems(vaccines);
            notifyVaccineComboBox.setItems(vaccines);
        }
    }

    private void loadVaccinationRecords(String childId) {
        if (vaccinationService != null) {
            List<VaccinationRecord> records = vaccinationService.getRecordsByChild(childId);
            vaccinationRecordsList.setAll(records);
        }
    }

    @FXML
    private void handleAddVaccinationRecord() {
        ChildSelectionWrapper selectedWrapper = vaccinationChildTable.getSelectionModel().getSelectedItem();
        Vaccine selectedVaccine = vaccineComboBox.getValue();
        LocalDate dueDate = vaccineDueDatePicker.getValue();
        
        // Check if any children are selected via checkboxes
        List<Child> selectedChildren = new ArrayList<>();
        for (ChildSelectionWrapper wrapper : vaccinationChildList) {
            if (wrapper.isSelected()) {
                selectedChildren.add(wrapper.getChild());
            }
        }
        
        // If no checkboxes checked, try to use the currently selected row
        if (selectedChildren.isEmpty() && selectedWrapper != null) {
            selectedChildren.add(selectedWrapper.getChild());
        }
        
        if (selectedChildren.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Child Selected", "Please select at least one child (checkbox or row selection).");
            return;
        }
        
        if (selectedVaccine == null) {
            showAlert(Alert.AlertType.WARNING, "No Vaccine Selected", "Please select a vaccine.");
            return;
        }
        
        if (dueDate == null) {
            showAlert(Alert.AlertType.WARNING, "No Date Selected", "Please select a due date.");
            return;
        }
        
        if (vaccinationService != null) {
            int count = 0;
            for (Child child : selectedChildren) {
                vaccinationService.addVaccinationRecord(child.getChildId(), selectedVaccine.getVaccineId(), dueDate);
                count++;
            }
            
            // Refresh records if a single child is selected in the table
            if (selectedWrapper != null) {
                loadVaccinationRecords(selectedWrapper.getChild().getChildId());
            }
            
            // Clear form
            vaccineComboBox.getSelectionModel().clearSelection();
            vaccineDueDatePicker.setValue(null);
            
            // Uncheck all
            for (ChildSelectionWrapper wrapper : vaccinationChildList) {
                wrapper.setSelected(false);
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccination record added for " + count + " child(ren).");
        }
    }

    @FXML
    private void handleSelectAllVaccinationChildren() {
        boolean allSelected = vaccinationChildList.stream().allMatch(ChildSelectionWrapper::isSelected);
        for (ChildSelectionWrapper wrapper : vaccinationChildList) {
            wrapper.setSelected(!allSelected);
        }
    }

    private void loadChildren() {
        if (childService != null) {
            List<Child> allChildren = childService.getAllChildren();
            childrenList.setAll(allChildren);
            
            // Populate notify list
            notifyList.clear();
            vaccinationChildList.clear();
            for (Child child : allChildren) {
                notifyList.add(new ChildSelectionWrapper(child));
                vaccinationChildList.add(new ChildSelectionWrapper(child));
            }
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            String name = nameField.getText();
            LocalDate dob = dobPicker.getValue();
            String gender = genderComboBox.getValue();
            String guardian = guardianField.getText();
            String contact = contactField.getText();
            
            // Default hospital and parent ID for now
            String hospitalId = "H001"; 
            String parentId = "P_NEW"; 

            childService.registerChild(name, dob, parentId, hospitalId, gender, guardian, contact);
            
            // Refresh list
            loadChildren();
            
            // Clear form
            handleClear();
            
            // Switch to list tab
            childrenTabPane.getSelectionModel().select(registeredChildrenTab);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Child registered successfully!");
        }
    }

    @FXML
    private void handleRemoveChild() {
        Child selectedChild = childrenTable.getSelectionModel().getSelectedItem();
        if (selectedChild == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a child to remove.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Child");
        alert.setContentText("Are you sure you want to remove " + selectedChild.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean removed = childService.deleteChild(selectedChild.getChildId());
            if (removed) {
                loadChildren();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Child removed successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not remove child.");
            }
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        dobPicker.setValue(null);
        genderComboBox.getSelectionModel().clearSelection();
        guardianField.clear();
        contactField.clear();
    }

    @FXML
    private void handleSelectAll() {
        boolean allSelected = notifyList.stream().allMatch(ChildSelectionWrapper::isSelected);
        for (ChildSelectionWrapper wrapper : notifyList) {
            wrapper.setSelected(!allSelected);
        }
    }

    @FXML
    private void handleNotify() {
        if (alertService == null) {
            // Fallback if not injected
            alertService = new AlertService(); 
        }

        Vaccine selectedVaccine = notifyVaccineComboBox.getValue();
        String explanation = explanationArea.getText();

        if (selectedVaccine == null || explanation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a vaccine and enter an explanation.");
            return;
        }
        
        String vaccineName = selectedVaccine.getName();

        List<Child> selectedChildren = new ArrayList<>();
        for (ChildSelectionWrapper wrapper : notifyList) {
            if (wrapper.isSelected()) {
                selectedChildren.add(wrapper.getChild());
            }
        }

        if (selectedChildren.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select at least one child to notify.");
            return;
        }

        // Send alerts
        int count = 0;
        for (Child child : selectedChildren) {
            String title = "Vaccine Alert: " + vaccineName;
            String message = explanation + "\n\nChild: " + child.getName() + " (" + child.getChildId() + ")";
            alertService.createTargetedAlert(title, message, HealthAlert.SeverityLevel.MEDIUM, child.getChildId());
            count++;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Sent " + count + " notifications successfully.");
        
        // Clear form
        notifyVaccineComboBox.getSelectionModel().clearSelection();
        explanationArea.clear();
        for (ChildSelectionWrapper wrapper : notifyList) {
            wrapper.setSelected(false);
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || 
            dobPicker.getValue() == null || 
            genderComboBox.getValue() == null || 
            guardianField.getText().isEmpty() ||
            contactField.getText().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Wrapper class for checkbox selection
    public static class ChildSelectionWrapper {
        private final Child child;
        private final BooleanProperty selected;

        public ChildSelectionWrapper(Child child) {
            this.child = child;
            this.selected = new SimpleBooleanProperty(false);
        }

        public Child getChild() {
            return child;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }
    }
}
