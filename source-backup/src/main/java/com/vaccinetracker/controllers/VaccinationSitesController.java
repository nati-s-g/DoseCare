package com.vaccinetracker.controllers;

import com.vaccinetracker.model.VaccinationSite;
import com.vaccinetracker.services.VaccinationSiteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class VaccinationSitesController {

    @FXML
    private TableView<VaccinationSite> sitesTable;
    @FXML
    private TableColumn<VaccinationSite, String> siteIdColumn;
    @FXML
    private TableColumn<VaccinationSite, String> nameColumn;
    @FXML
    private TableColumn<VaccinationSite, String> locationColumn;
    @FXML
    private TableColumn<VaccinationSite, String> contactColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField contactField;

    private VaccinationSiteService vaccinationSiteService;
    private ObservableList<VaccinationSite> sitesList = FXCollections.observableArrayList();

    public void setVaccinationSiteService(VaccinationSiteService vaccinationSiteService) {
        this.vaccinationSiteService = vaccinationSiteService;
        loadSites();
    }

    @FXML
    public void initialize() {
        // Initialize Table Columns
        siteIdColumn.setCellValueFactory(new PropertyValueFactory<>("siteId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        sitesTable.setItems(sitesList);
    }

    private void loadSites() {
        if (vaccinationSiteService != null) {
            sitesList.setAll(vaccinationSiteService.getAllSites());
        }
    }

    @FXML
    private void handleAddSite() {
        String name = nameField.getText();
        String location = locationField.getText();
        String contact = contactField.getText();

        if (name.isEmpty() || location.isEmpty() || contact.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (vaccinationSiteService != null) {
            vaccinationSiteService.createSite(name, location, contact);
            loadSites();
            handleClear();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccination site added successfully.");
        }
    }

    @FXML
    private void handleRemoveSite() {
        VaccinationSite selectedSite = sitesTable.getSelectionModel().getSelectedItem();
        if (selectedSite == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a site to remove.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Vaccination Site");
        confirmAlert.setContentText("Are you sure you want to remove " + selectedSite.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (vaccinationSiteService != null) {
                boolean removed = vaccinationSiteService.deleteSite(selectedSite.getSiteId());
                if (removed) {
                    loadSites();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vaccination site removed successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not remove the site.");
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        locationField.clear();
        contactField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
