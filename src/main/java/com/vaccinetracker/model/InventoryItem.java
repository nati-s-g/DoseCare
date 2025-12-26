package com.vaccinetracker.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InventoryItem {
    private final StringProperty siteId;
    private final StringProperty siteName;
    private final StringProperty vaccineId;
    private final StringProperty vaccineName;
    private final IntegerProperty stock;
    private final StringProperty expiryDate;

    public InventoryItem(String siteId, String siteName, String vaccineId, String vaccineName, int stock, String expiryDate) {
        this.siteId = new SimpleStringProperty(siteId);
        this.siteName = new SimpleStringProperty(siteName);
        this.vaccineId = new SimpleStringProperty(vaccineId);
        this.vaccineName = new SimpleStringProperty(vaccineName);
        this.stock = new SimpleIntegerProperty(stock);
        this.expiryDate = new SimpleStringProperty(expiryDate);
    }

    public InventoryItem(String siteId, String siteName, String vaccineId, String vaccineName, int stock) {
        this(siteId, siteName, vaccineId, vaccineName, stock, "-");
    }

    public String getSiteId() { return siteId.get(); }
    public StringProperty siteIdProperty() { return siteId; }

    public String getSiteName() { return siteName.get(); }
    public StringProperty siteNameProperty() { return siteName; }

    public String getVaccineId() { return vaccineId.get(); }
    public StringProperty vaccineIdProperty() { return vaccineId; }

    public String getVaccineName() { return vaccineName.get(); }
    public StringProperty vaccineNameProperty() { return vaccineName; }

    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
    public IntegerProperty stockProperty() { return stock; }

    public String getExpiryDate() { return expiryDate.get(); }
    public void setExpiryDate(String expiryDate) { this.expiryDate.set(expiryDate); }
    public StringProperty expiryDateProperty() { return expiryDate; }
}
