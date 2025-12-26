package com.vaccinetracker.controllers;

import com.vaccinetracker.model.InventoryItem;
import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.model.Vaccine;
import com.vaccinetracker.services.VaccinationSiteService;
import com.vaccinetracker.services.VaccineService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import javafx.scene.control.TextInputDialog;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class InventoryController {

    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, String> siteColumn;
    @FXML
    private TableColumn<InventoryItem, String> vaccineColumn;
    @FXML
    private TableColumn<InventoryItem, Integer> stockColumn;
    @FXML
    private TableColumn<InventoryItem, String> expiryDateColumn;

    @FXML
    private ComboBox<VaccinationSite> siteComboBox;
    @FXML
    private ComboBox<Vaccine> vaccineComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private DatePicker expiryDatePicker;

    private VaccinationSiteService vaccinationSiteService;
    private VaccineService vaccineService;
    private ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();

    public void setServices(VaccinationSiteService siteService, VaccineService vaccineService) {
        this.vaccinationSiteService = siteService;
        this.vaccineService = vaccineService;
        loadInventory();
        setupComboBoxes();
    }

    @FXML
    public void initialize() {
        siteColumn.setCellValueFactory(new PropertyValueFactory<>("siteName"));
        vaccineColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        inventoryTable.setItems(inventoryList);
    }

    private void setupComboBoxes() {
        if (vaccinationSiteService != null) {
            updateSiteComboBox();
            siteComboBox.setConverter(new StringConverter<VaccinationSite>() {
                @Override
                public String toString(VaccinationSite site) {
                    return site != null ? site.getName() : "";
                }
                @Override
                public VaccinationSite fromString(String string) {
                    return null; // Not needed
                }
            });
        }

        if (vaccineService != null) {
            updateVaccineComboBox();
            
            vaccineComboBox.setConverter(new StringConverter<Vaccine>() {
                @Override
                public String toString(Vaccine vaccine) {
                    return vaccine != null ? vaccine.getName() : "";
                }
                @Override
                public Vaccine fromString(String string) {
                    return null; // Not needed
                }
            });

            // Add listener for "Add New Vaccine" selection
            vaccineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && "ADD_NEW".equals(newVal.getVaccineId())) {
                    handleAddNewVaccine();
                }
            });
        }
    }

    private void updateSiteComboBox() {
        ObservableList<VaccinationSite> sites = FXCollections.observableArrayList(vaccinationSiteService.getAllSites());
        // Add "Select All" option
        VaccinationSite selectAllOption = new VaccinationSite("ALL_SITES", "Select All Sites", "", "");
        sites.add(0, selectAllOption); // Add at the top
        siteComboBox.setItems(sites);
    }

    private void updateVaccineComboBox() {
        ObservableList<Vaccine> vaccines = FXCollections.observableArrayList(vaccineService.getAllVaccines());
        // Add a dummy vaccine for the "Add New" option
        Vaccine addNewOption = new Vaccine("ADD_NEW", "+ Add New Vaccine...", "Create a new vaccine type", 0);
        vaccines.add(addNewOption);
        vaccineComboBox.setItems(vaccines);
    }

    private void handleAddNewVaccine() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Vaccine");
        dialog.setHeaderText("Create a New Vaccine Type");
        dialog.setContentText("Please enter the vaccine name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String name = result.get().trim();
            Vaccine newVaccine = vaccineService.createVaccine(name);
            
            updateVaccineComboBox();
            vaccineComboBox.getSelectionModel().select(newVaccine);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "New vaccine '" + name + "' created successfully.");
        } else {
            // Reset selection if cancelled or empty
            vaccineComboBox.getSelectionModel().clearSelection();
        }
    }

    private void loadInventory() {
        inventoryList.clear();
        if (vaccinationSiteService != null && vaccineService != null) {
            for (VaccinationSite site : vaccinationSiteService.getAllSites()) {
                for (var entry : site.getVaccineStock().entrySet()) {
                    String vaccineId = entry.getKey();
                    int stock = entry.getValue();
                    Vaccine vaccine = vaccineService.getVaccineById(vaccineId);
                    String vaccineName = (vaccine != null) ? vaccine.getName() : vaccineId;
                    
                    LocalDate expiry = site.getExpiryDate(vaccineId);
                    String expiryStr = (expiry != null) ? expiry.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-";
                    
                    inventoryList.add(new InventoryItem(
                        site.getSiteId(),
                        site.getName(),
                        vaccineId,
                        vaccineName,
                        stock,
                        expiryStr
                    ));
                }
            }
        }
    }

    @FXML
    private void handleAddVaccine() {
        VaccinationSite selectedSite = siteComboBox.getValue();
        Vaccine selectedVaccine = vaccineComboBox.getValue();
        String quantityText = quantityField.getText();
        LocalDate expiryDate = expiryDatePicker.getValue();

        if (selectedSite == null || selectedVaccine == null || quantityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select site, vaccine and enter quantity.");
            return;
        }

        // Check if "Add New Vaccine" placeholder is selected
        if ("ADD_NEW".equals(selectedVaccine.getVaccineId())) {
             showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a valid vaccine.");
             return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Quantity must be positive.");
                return;
            }

            if ("ALL_SITES".equals(selectedSite.getSiteId())) {
                // Add to all sites
                for (VaccinationSite site : vaccinationSiteService.getAllSites()) {
                    site.addStock(selectedVaccine.getVaccineId(), quantity, expiryDate);
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccine stock added to ALL sites successfully.");
            } else {
                // Add to specific site
                selectedSite.addStock(selectedVaccine.getVaccineId(), quantity, expiryDate);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccine stock added successfully.");
            }

            loadInventory();
            quantityField.clear();
            expiryDatePicker.setValue(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid quantity format.");
        }
    }

    @FXML
    private void handleRemoveVaccine() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to remove.");
            return;
        }

        // Logic: Remove the vaccine from the site (or set stock to 0?)
        // "Remove Vaccine" usually implies removing the record.
        VaccinationSite site = vaccinationSiteService.getSiteById(selectedItem.getSiteId());
        if (site != null) {
            site.removeVaccine(selectedItem.getVaccineId());
            loadInventory();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccine removed from site inventory.");
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
