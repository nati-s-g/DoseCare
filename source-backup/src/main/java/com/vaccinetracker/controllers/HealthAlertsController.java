package com.vaccinetracker.controllers;

import com.vaccinetracker.model.HealthAlert;
import com.vaccinetracker.services.AlertService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.time.LocalDate;

public class HealthAlertsController {

    @FXML private TextField subjectField;
    @FXML private ComboBox<HealthAlert.SeverityLevel> severityCombo;
    @FXML private TextArea messageArea;
    
    @FXML private TableView<HealthAlert> alertsTable;
    @FXML private TableColumn<HealthAlert, LocalDate> dateCol;
    @FXML private TableColumn<HealthAlert, HealthAlert.SeverityLevel> severityCol;
    @FXML private TableColumn<HealthAlert, String> titleCol;
    @FXML private TableColumn<HealthAlert, String> messageCol;

    private AlertService alertService;
    private ObservableList<HealthAlert> alertsList = FXCollections.observableArrayList();

    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
        loadAlerts();
    }

    @FXML
    public void initialize() {
        severityCombo.setItems(FXCollections.observableArrayList(HealthAlert.SeverityLevel.values()));
        severityCombo.getSelectionModel().select(HealthAlert.SeverityLevel.LOW);

        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severityLevel"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        // Custom cell factory for message column to show tooltip
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageCol.setCellFactory(column -> new TableCell<HealthAlert, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setPrefWidth(300);
                    tooltip.setWrapText(true);
                    setTooltip(tooltip);
                }
            }
        });
        
        // Double-click listener to view details
        alertsTable.setRowFactory(tv -> {
            TableRow<HealthAlert> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    HealthAlert rowData = row.getItem();
                    showDetailAlert(rowData);
                }
            });
            return row ;
        });
        
        alertsTable.setItems(alertsList);
    }

    private void loadAlerts() {
        if (alertService != null) {
            alertsList.setAll(alertService.getAllAlerts());
        }
    }

    @FXML
    private void handleViewDetails() {
        HealthAlert selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDetailAlert(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an alert to view.");
        }
    }

    private void showDetailAlert(HealthAlert alert) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Health Alert Details");
        dialog.setHeaderText(alert.getTitle());
        
        TextArea area = new TextArea(alert.getMessage());
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefRowCount(10);
        area.setPrefColumnCount(40);
        
        GridPane.setVgrow(area, Priority.ALWAYS);
        GridPane.setHgrow(area, Priority.ALWAYS);
        
        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.setHgap(10);
        content.setVgap(10);
        content.add(new Label("Severity:"), 0, 0);
        content.add(new Label(alert.getSeverityLevel().toString()), 1, 0);
        content.add(new Label("Date:"), 0, 1);
        content.add(new Label(alert.getDateCreated().toString()), 1, 1);
        content.add(new Label("Message:"), 0, 2);
        content.add(area, 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    @FXML
    private void handleSendAlert() {
        String title = subjectField.getText();
        String message = messageArea.getText();
        HealthAlert.SeverityLevel severity = severityCombo.getValue();

        if (title == null || title.isEmpty() || message == null || message.isEmpty() || severity == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (alertService != null) {
            alertService.createAlert(title, message, severity);
            loadAlerts();
            handleClear();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Health alert sent successfully.");
        }
    }

    @FXML
    private void handleClear() {
        subjectField.clear();
        messageArea.clear();
        severityCombo.getSelectionModel().select(HealthAlert.SeverityLevel.LOW);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}