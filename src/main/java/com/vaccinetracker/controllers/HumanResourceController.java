package com.vaccinetracker.controllers;

import com.vaccinetracker.model.StaffMember;
import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.services.HumanResourceService;
import com.vaccinetracker.services.VaccinationSiteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.Optional;

public class HumanResourceController {

    // Nurses Tab
    @FXML private TableView<StaffMember> nurseTable;
    @FXML private TableColumn<StaffMember, String> nurseNameCol;
    @FXML private TableColumn<StaffMember, String> nurseIdCol;
    @FXML private TableColumn<StaffMember, String> nurseContactCol;
    @FXML private TableColumn<StaffMember, String> nurseSiteCol;
    @FXML private TextField nurseNameField;
    @FXML private TextField nurseContactField;
    @FXML private ComboBox<VaccinationSite> nurseSiteCombo;

    // Doctors Tab
    @FXML private TableView<StaffMember> doctorTable;
    @FXML private TableColumn<StaffMember, String> doctorNameCol;
    @FXML private TableColumn<StaffMember, String> doctorIdCol;
    @FXML private TableColumn<StaffMember, String> doctorContactCol;
    @FXML private TableColumn<StaffMember, String> doctorSiteCol;
    @FXML private TextField doctorNameField;
    @FXML private TextField doctorContactField;
    @FXML private ComboBox<VaccinationSite> doctorSiteCombo;

    // Other Staff Tab
    @FXML private TableView<StaffMember> staffTable;
    @FXML private TableColumn<StaffMember, String> staffNameCol;
    @FXML private TableColumn<StaffMember, String> staffIdCol;
    @FXML private TableColumn<StaffMember, String> staffRoleCol;
    @FXML private TableColumn<StaffMember, String> staffContactCol;
    @FXML private TableColumn<StaffMember, String> staffSiteCol;
    @FXML private TextField staffNameField;
    @FXML private TextField staffContactField;
    @FXML private ComboBox<String> staffRoleCombo;
    @FXML private ComboBox<VaccinationSite> staffSiteCombo;

    private HumanResourceService hrService;
    private VaccinationSiteService siteService;

    private ObservableList<StaffMember> nurseList = FXCollections.observableArrayList();
    private ObservableList<StaffMember> doctorList = FXCollections.observableArrayList();
    private ObservableList<StaffMember> otherStaffList = FXCollections.observableArrayList();

    public void setServices(HumanResourceService hrService, VaccinationSiteService siteService) {
        this.hrService = hrService;
        this.siteService = siteService;
        loadData();
        setupComboBoxes();
    }

    @FXML
    public void initialize() {
        setupTable(nurseTable, nurseNameCol, nurseIdCol, null, nurseContactCol, nurseSiteCol, nurseList, "Nurse");
        setupTable(doctorTable, doctorNameCol, doctorIdCol, null, doctorContactCol, doctorSiteCol, doctorList, "Doctor");
        setupTable(staffTable, staffNameCol, staffIdCol, staffRoleCol, staffContactCol, staffSiteCol, otherStaffList, "Other");
    }

    private void setupTable(TableView<StaffMember> table, 
                          TableColumn<StaffMember, String> nameCol,
                          TableColumn<StaffMember, String> idCol,
                          TableColumn<StaffMember, String> roleCol,
                          TableColumn<StaffMember, String> contactCol,
                          TableColumn<StaffMember, String> siteCol,
                          ObservableList<StaffMember> list,
                          String role) {
        
        nameCol.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getName();
            if ("Doctor".equals(role)) {
                return new javafx.beans.property.SimpleStringProperty("Dr. " + name);
            } else if ("Nurse".equals(role)) {
                return new javafx.beans.property.SimpleStringProperty("Nurse " + name);
            }
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("professionalId"));
        if (roleCol != null) {
            roleCol.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
            roleCol.setStyle("-fx-alignment: CENTER-LEFT;");
        }
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        siteCol.setCellValueFactory(new PropertyValueFactory<>("siteName"));
        
        // Center align columns
        nameCol.setStyle("-fx-alignment: CENTER-LEFT;");
        idCol.setStyle("-fx-alignment: CENTER;");
        contactCol.setStyle("-fx-alignment: CENTER;");
        siteCol.setStyle("-fx-alignment: CENTER-LEFT;");
        
        table.setItems(list);
    }

    private void setupComboBoxes() {
        if (siteService == null) return;
        ObservableList<VaccinationSite> sites = FXCollections.observableArrayList(siteService.getAllSites());
        
        StringConverter<VaccinationSite> converter = new StringConverter<VaccinationSite>() {
            @Override
            public String toString(VaccinationSite object) {
                return object != null ? object.getName() : "";
            }
            @Override
            public VaccinationSite fromString(String string) { return null; }
        };

        nurseSiteCombo.setItems(sites);
        nurseSiteCombo.setConverter(converter);
        
        doctorSiteCombo.setItems(sites);
        doctorSiteCombo.setConverter(converter);
        
        staffSiteCombo.setItems(sites);
        staffSiteCombo.setConverter(converter);

        staffRoleCombo.setItems(FXCollections.observableArrayList(
            "Vaccination Site Manager", "Watchman", "Usher", "Health Worker", "Help Desk", "Queue Manager", "Vaccine Fetcher"
        ));
    }

    private void loadData() {
        if (hrService == null) return;
        nurseList.setAll(hrService.getStaffByRole("Nurse"));
        doctorList.setAll(hrService.getStaffByRole("Doctor"));
        otherStaffList.setAll(hrService.getStaffByRole("Other"));
    }

    @FXML
    private void handleAddNurse() {
        addStaffMember("Nurse", null, nurseNameField, nurseContactField, nurseSiteCombo);
    }

    @FXML
    private void handleRemoveNurse() {
        removeStaffMember(nurseTable);
    }

    @FXML
    private void handleAddDoctor() {
        addStaffMember("Doctor", null, doctorNameField, doctorContactField, doctorSiteCombo);
    }

    @FXML
    private void handleRemoveDoctor() {
        removeStaffMember(doctorTable);
    }

    @FXML
    private void handleAddStaff() {
        String jobTitle = staffRoleCombo.getValue();
        if (jobTitle == null || jobTitle.isEmpty()) {
             showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a role.");
             return;
        }
        addStaffMember("Other", jobTitle, staffNameField, staffContactField, staffSiteCombo);
    }

    @FXML
    private void handleRemoveStaff() {
        removeStaffMember(staffTable);
    }

    private void addStaffMember(String role, String jobTitle, TextField nameField, TextField contactField, ComboBox<VaccinationSite> siteCombo) {
        String name = nameField.getText();
        String contact = contactField.getText();
        VaccinationSite site = siteCombo.getValue();

        if (name.isEmpty() || contact.isEmpty() || site == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }
        
        String profId = hrService.generateProfessionalId(role);

        hrService.addStaff(name, role, jobTitle, profId, contact, site);
        loadData();
        
        // Clear fields
        nameField.clear();
        contactField.clear();
        siteCombo.getSelectionModel().clearSelection();
        if (staffRoleCombo != null) staffRoleCombo.getSelectionModel().clearSelection();
        
        showAlert(Alert.AlertType.INFORMATION, "Success", role + " added successfully.");
    }

    private void removeStaffMember(TableView<StaffMember> table) {
        StaffMember selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to remove.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Staff");
        alert.setContentText("Are you sure you want to remove " + selected.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            hrService.removeStaff(selected.getId());
            loadData();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
