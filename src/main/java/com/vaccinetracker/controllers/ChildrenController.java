package com.vaccinetracker.controllers;

import com.vaccinetracker.model.Child;
import com.vaccinetracker.model.HealthAlert;
import com.vaccinetracker.model.VaccinationRecord;
import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.model.Vaccine;
import com.vaccinetracker.services.AlertService;
import com.vaccinetracker.services.ChildService;
import com.vaccinetracker.services.VaccinationService;
import com.vaccinetracker.services.VaccinationSiteService;
import com.vaccinetracker.services.VaccineService;
import com.vaccinetracker.services.StorageService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    private TableColumn<Child, String> fatherColumn; // Renamed from guardianColumn
    @FXML
    private TableColumn<Child, String> fatherContactColumn; // Renamed from contactColumn
    @FXML
    private TableColumn<Child, String> motherColumn; // New
    @FXML
    private TableColumn<Child, String> motherContactColumn; // New

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField fatherNameField; // Renamed from guardianField
    @FXML
    private TextField fatherContactField; // Renamed from contactField
    @FXML
    private TextField motherNameField; // New
    @FXML
    private TextField motherContactField; // New


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
    private ComboBox<String> notifyParentComboBox;
    @FXML
    private ComboBox<VaccinationSite> notifySiteComboBox;
    @FXML
    private DatePicker notifyDueDatePicker;
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
    private TableColumn<VaccinationRecord, String> vrSiteColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrDueDateColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrStatusColumn;
    @FXML
    private TableColumn<VaccinationRecord, String> vrAdminDateColumn;
    
    @FXML
    private ComboBox<Vaccine> vaccineComboBox;
    @FXML
    private ComboBox<VaccinationSite> siteComboBox;
    @FXML
    private DatePicker vaccineDueDatePicker;
    @FXML
    private javafx.scene.layout.VBox addRecordSection;

    private ChildService childService;
    private AlertService alertService;
    private VaccinationService vaccinationService;
    private VaccineService vaccineService;
    private VaccinationSiteService vaccinationSiteService;
    
    private ObservableList<Child> childrenList = FXCollections.observableArrayList();
    private ObservableList<ChildSelectionWrapper> notifyList = FXCollections.observableArrayList();
    private ObservableList<ChildSelectionWrapper> vaccinationChildList = FXCollections.observableArrayList();
    private ObservableList<VaccinationRecord> vaccinationRecordsList = FXCollections.observableArrayList();

    @FXML
    private TextField searchField;

    public void setChildService(ChildService childService) {
        this.childService = childService;
        loadChildren();
        
        // Add search listener if searchField exists (it might not be in FXML yet, but good to have logic ready)
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterChildren(newValue);
            });
        }
    }
    
    private void filterChildren(String query) {
        if (query == null || query.isEmpty()) {
            vaccinationChildTable.setItems(vaccinationChildList);
            return;
        }
        
        String lowerQuery = query.toLowerCase();
        ObservableList<ChildSelectionWrapper> filteredList = FXCollections.observableArrayList();
        
        for (ChildSelectionWrapper wrapper : vaccinationChildList) {
            Child child = wrapper.getChild();
            if (child.getChildId().toLowerCase().contains(lowerQuery) || 
                child.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(wrapper);
            }
        }
        
        vaccinationChildTable.setItems(filteredList);
    }

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }

    public void setReadOnlyMode(boolean readOnly) {
        if (addRecordSection != null) {
            addRecordSection.setVisible(!readOnly);
            addRecordSection.setManaged(!readOnly);
        }
    }

    public void setVaccinationService(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    public void setVaccineService(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
        loadVaccines();
    }

    public void setVaccinationSiteService(VaccinationSiteService vaccinationSiteService) {
        this.vaccinationSiteService = vaccinationSiteService;
        loadSites();
    }

    @FXML
    public void initialize() {
        // Initialize Table Columns
        childIdColumn.setCellValueFactory(new PropertyValueFactory<>("childId"));
        
        // Make Child ID copyable via Context Menu
        childIdColumn.setCellFactory(col -> {
            TableCell<Child, String> cell = new TableCell<Child, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                    }
                }
            };
            
            ContextMenu menu = new ContextMenu();
            MenuItem copyItem = new MenuItem("Copy ID");
            copyItem.setOnAction(event -> {
                String item = cell.getItem();
                if (item != null) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item);
                    Clipboard.getSystemClipboard().setContent(content);
                }
            });
            menu.getItems().add(copyItem);
            cell.setContextMenu(menu);
            return cell;
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirthString")); 
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("ageString")); 
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        fatherColumn.setCellValueFactory(new PropertyValueFactory<>("fatherName"));
        fatherContactColumn.setCellValueFactory(new PropertyValueFactory<>("fatherContact"));
        motherColumn.setCellValueFactory(new PropertyValueFactory<>("motherName"));
        motherContactColumn.setCellValueFactory(new PropertyValueFactory<>("motherContact"));

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
        vrSiteColumn.setCellValueFactory(cellData -> {
            if (vaccinationSiteService != null && cellData.getValue().getVaccinationSiteId() != null) {
                VaccinationSite s = vaccinationSiteService.getSiteById(cellData.getValue().getVaccinationSiteId());
                return new SimpleStringProperty(s != null ? s.getName() : cellData.getValue().getVaccinationSiteId());
            }
            return new SimpleStringProperty(cellData.getValue().getVaccinationSiteId() != null ? cellData.getValue().getVaccinationSiteId() : "");
        });
        vrDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextDueDate"));
        vrStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        vrAdminDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdministered"));
        
        // Align columns
        vrVaccineColumn.setStyle("-fx-alignment: CENTER;");
        vrSiteColumn.setStyle("-fx-alignment: CENTER;");
        vrDueDateColumn.setStyle("-fx-alignment: CENTER;");
        vrStatusColumn.setStyle("-fx-alignment: CENTER;");
        vrAdminDateColumn.setStyle("-fx-alignment: CENTER;");
        
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
        
        // Parent Selection Listener - Selecting a parent can auto-fill message
        notifyParentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Optional: You could update the message template with "Dear " + newVal
                if (explanationArea.getText().isEmpty()) {
                    explanationArea.setText("Dear " + newVal + ", \n\n");
                }
            }
        });

        // Configure Site ComboBox
        StringConverter<VaccinationSite> siteConverter = new StringConverter<VaccinationSite>() {
            @Override
            public String toString(VaccinationSite object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public VaccinationSite fromString(String string) {
                return null; // Not needed for this use case
            }
        };
        siteComboBox.setConverter(siteConverter);
        notifySiteComboBox.setConverter(siteConverter);
    }

    private void loadVaccines() {
        if (vaccineService != null) {
            ObservableList<Vaccine> vaccines = FXCollections.observableArrayList(vaccineService.getAllVaccines());
            vaccineComboBox.setItems(vaccines);
            notifyVaccineComboBox.setItems(vaccines);
        }
    }

    private void loadSites() {
        if (vaccinationSiteService != null) {
            ObservableList<VaccinationSite> sites = FXCollections.observableArrayList(vaccinationSiteService.getAllSites());
            siteComboBox.setItems(sites);
            notifySiteComboBox.setItems(sites);
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
        VaccinationSite selectedSite = siteComboBox.getValue();
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
        
        String siteId = selectedSite != null ? selectedSite.getSiteId() : null;

        // Check inventory availability
        if (selectedSite != null) {
            int currentStock = selectedSite.getStock(selectedVaccine.getVaccineId());
            if (currentStock < selectedChildren.size()) {
                showAlert(Alert.AlertType.ERROR, "Unavailable Vaccine", 
                    "The selected vaccine is not available at " + selectedSite.getName() + ".\n" +
                    "Available stock: " + currentStock + "\n" +
                    "Required: " + selectedChildren.size());
                return;
            }
        }

        if (vaccinationService != null) {
            int count = 0;
            for (Child child : selectedChildren) {
                vaccinationService.addVaccinationRecord(child.getChildId(), selectedVaccine.getVaccineId(), dueDate, siteId);
                count++;
            }
            
            // Decrement stock
            if (selectedSite != null) {
                selectedSite.removeStock(selectedVaccine.getVaccineId(), count);
            }
            
            // Refresh records if a single child is selected in the table
            if (selectedWrapper != null) {
                loadVaccinationRecords(selectedWrapper.getChild().getChildId());
            }
            
            // Clear form
            vaccineComboBox.getSelectionModel().clearSelection();
            siteComboBox.getSelectionModel().clearSelection();
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
                ChildSelectionWrapper wrapper = new ChildSelectionWrapper(child);
                
                // Add listener to update parent list when selection changes
                wrapper.selectedProperty().addListener((obs, wasSelected, isSelected) -> updateNotifyParentList());
                
                notifyList.add(wrapper);
                vaccinationChildList.add(new ChildSelectionWrapper(child));
            }
            
            // Initial update
            updateNotifyParentList();
        }
    }

    private void updateNotifyParentList() {
        List<String> selectedParents = new ArrayList<>();
        boolean anySelected = false;
        
        for (ChildSelectionWrapper wrapper : notifyList) {
            if (wrapper.isSelected()) {
                anySelected = true;
                Child child = wrapper.getChild();

                String fName = child.getFatherName();
                if (fName != null && !fName.isEmpty() && !selectedParents.contains(fName)) {
                    selectedParents.add(fName);
                }
                
                String mName = child.getMotherName();
                if (mName != null && !mName.isEmpty() && !selectedParents.contains(mName)) {
                    selectedParents.add(mName);
                }
            }
        }
        
        selectedParents.sort(String::compareTo);
        
        // Update the ComboBox
        notifyParentComboBox.setItems(FXCollections.observableArrayList(selectedParents));
        
        if (!selectedParents.isEmpty()) {
            notifyParentComboBox.setPromptText("Select Parent (" + selectedParents.size() + " available)");
        } else if (anySelected) {
            notifyParentComboBox.setPromptText("No parent info found");
        } else {
            notifyParentComboBox.setPromptText("Select children to see parents");
        }
    }
    
    // Removed old logic that filtered table by parent selection since flow is reversed now
/*    private void filterNotifyTableByParent(String parentName) {
         // Filter the table to show only children of this parent
         // And automatically select them
         ObservableList<ChildSelectionWrapper> filtered = FXCollections.observableArrayList();
         for (ChildSelectionWrapper wrapper : notifyList) {
             if (parentName.equals(wrapper.getChild().getGuardianName())) {
                 wrapper.setSelected(true);
                 filtered.add(wrapper);
             } else {
                 wrapper.setSelected(false);
             }
         }
         notifyTable.setItems(filtered);
    } */

    @FXML
    private void handleSave() {
        if (validateInput()) {
            String name = nameField.getText();
            LocalDate dob = dobPicker.getValue();
            String gender = genderComboBox.getValue();
            String fatherName = fatherNameField.getText();
            String fatherContact = fatherContactField.getText();
            String motherName = motherNameField.getText();
            String motherContact = motherContactField.getText();
            
            // Default hospital and parent ID for now
            String hospitalId = "H001"; 
            String parentId = "P_NEW"; 

            ChildService.registerChild(name, dob, parentId, hospitalId, gender, fatherName, fatherContact, motherName, motherContact);
            
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
    private void handleNotifyFromList() {
        Child selectedChild = childrenTable.getSelectionModel().getSelectedItem();
        if (selectedChild == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a child to notify.");
            return;
        }
        
        // Switch to Notify Tab
        childrenTabPane.getSelectionModel().select(notifyTab);
        
        // Select the child in the notify table
        for (ChildSelectionWrapper wrapper : notifyList) {
            if (wrapper.getChild().getChildId().equals(selectedChild.getChildId())) {
                wrapper.setSelected(true);
                notifyTable.getSelectionModel().select(wrapper);
                notifyTable.scrollTo(wrapper);
            } else {
                wrapper.setSelected(false);
            }
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
        fatherNameField.clear();
        fatherContactField.clear();
        motherNameField.clear();
        motherContactField.clear();
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
        VaccinationSite selectedSite = notifySiteComboBox.getValue();
        LocalDate dueDate = notifyDueDatePicker.getValue();
        String explanation = explanationArea.getText();

        if (selectedVaccine == null || explanation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a vaccine and enter an explanation.");
            return;
        }
        
        String vaccineName = selectedVaccine.getName();
        String siteName = selectedSite != null ? selectedSite.getName() : "Unknown Site";
        String dueDateStr = dueDate != null ? dueDate.toString() : "Not specified";

        // Check inventory availability
        if (selectedSite != null) {
            int currentStock = selectedSite.getStock(selectedVaccine.getVaccineId());
            if (currentStock <= 0) {
                showAlert(Alert.AlertType.ERROR, "Unavailable Vaccine", 
                    "The selected vaccine is not available at " + selectedSite.getName() + ".\n" +
                    "Please check inventory or select a different site.");
                return;
            }
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
            String message = explanation + "\n\nDue Date: " + dueDateStr + "\nLocation: " + siteName + "\nChild: " + child.getName() + " (" + child.getChildId() + ")";
            alertService.createTargetedAlert(title, message, HealthAlert.SeverityLevel.MEDIUM, child.getChildId());
            
            // Sync with vaccination records
            if (vaccinationService != null && dueDate != null) {
                 List<VaccinationRecord> records = vaccinationService.getRecordsByChild(child.getChildId());
                 boolean recordExists = false;
                 
                 for (VaccinationRecord record : records) {
                     if (record.getVaccineId().equals(selectedVaccine.getVaccineId()) && 
                         record.getStatus() == VaccinationRecord.VaccinationStatus.PENDING) {
                         
                         // Update existing pending record
                         record.setNextDueDate(dueDate);
                         if (selectedSite != null) {
                             record.setVaccinationSiteId(selectedSite.getSiteId());
                         }
                         recordExists = true;
                         break; 
                     }
                 }
                 
                 if (!recordExists) {
                     // Create new record
                     String siteId = selectedSite != null ? selectedSite.getSiteId() : null;
                     vaccinationService.addVaccinationRecord(child.getChildId(), selectedVaccine.getVaccineId(), dueDate, siteId);
                 }
            }
            count++;
        }
        
        // Ensure all changes (including manual updates to records) are saved
        StorageService.saveAll();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Sent " + count + " notifications successfully.");
        
        // Clear form
        notifyVaccineComboBox.getSelectionModel().clearSelection();
        notifySiteComboBox.getSelectionModel().clearSelection();
        notifyDueDatePicker.setValue(null);
        explanationArea.clear();
        for (ChildSelectionWrapper wrapper : notifyList) {
            wrapper.setSelected(false);
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || 
            dobPicker.getValue() == null || 
            genderComboBox.getValue() == null || 
            fatherNameField.getText().isEmpty() ||
            fatherContactField.getText().isEmpty() ||
            motherNameField.getText().isEmpty() ||
            motherContactField.getText().isEmpty()) {
            
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
