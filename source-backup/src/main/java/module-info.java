module com.vaccinetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    
    exports com.vaccinetracker;
    exports com.vaccinetracker.controllers;
    exports com.vaccinetracker.model;
    exports com.vaccinetracker.services;
    
    opens com.vaccinetracker.controllers to javafx.fxml;
    opens com.vaccinetracker.model to javafx.base, javafx.fxml, com.google.gson;
    opens com.vaccinetracker.services to com.google.gson;
}
