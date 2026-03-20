package com.vaccinetracker.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StaffMember {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty role; // "Nurse", "Doctor", "Other"
    private final StringProperty jobTitle; // Specific role for "Other" staff (e.g., Manager, Watchman)
    private final StringProperty professionalId; // Vaccinator ID, Doctor ID, etc.
    private final StringProperty contactInfo;
    private final StringProperty siteId;
    private final StringProperty siteName; // For display convenience

    public StaffMember(String id, String name, String role, String jobTitle, String professionalId, String contactInfo, String siteId, String siteName) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.jobTitle = new SimpleStringProperty(jobTitle);
        this.professionalId = new SimpleStringProperty(professionalId);
        this.contactInfo = new SimpleStringProperty(contactInfo);
        this.siteId = new SimpleStringProperty(siteId);
        this.siteName = new SimpleStringProperty(siteName);
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getRole() { return role.get(); }
    public StringProperty roleProperty() { return role; }

    public String getJobTitle() { return jobTitle.get(); }
    public StringProperty jobTitleProperty() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle.set(jobTitle); }

    public String getProfessionalId() { return professionalId.get(); }
    public StringProperty professionalIdProperty() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId.set(professionalId); }

    public String getContactInfo() { return contactInfo.get(); }
    public StringProperty contactInfoProperty() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo.set(contactInfo); }

    public String getSiteId() { return siteId.get(); }
    public StringProperty siteIdProperty() { return siteId; }
    public void setSiteId(String siteId) { this.siteId.set(siteId); }

    public String getSiteName() { return siteName.get(); }
    public StringProperty siteNameProperty() { return siteName; }
    public void setSiteName(String siteName) { this.siteName.set(siteName); }
}
