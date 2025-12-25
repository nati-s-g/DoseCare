package com.vaccinetracker.controllers;

import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.HealthAlert;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private TextField vaccineNameField;
    @FXML
    private TextArea explanationArea;

    private ChildService childService;
    private AlertService alertService;
    private ObservableList<Child> childrenList = FXCollections.observableArrayList();
    private ObservableList<ChildSelectionWrapper> notifyList = FXCollections.observableArrayList();

    public void setChildService(ChildService childService) {
        this.childService = childService;
        loadChildren();
    }

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
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
    }

    private void loadChildren() {
        if (childService != null) {
            List<Child> allChildren = childService.getAllChildren();
            childrenList.setAll(allChildren);
            
            // Populate notify list
            notifyList.clear();
            for (Child child : allChildren) {
                notifyList.add(new ChildSelectionWrapper(child));
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

        String vaccineName = vaccineNameField.getText();
        String explanation = explanationArea.getText();

        if (vaccineName.isEmpty() || explanation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter vaccine name and explanation.");
            return;
        }

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
        vaccineNameField.clear();
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
