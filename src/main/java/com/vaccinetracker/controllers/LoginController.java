package com.vaccinetracker.controllers;

import com.vaccinetracker.App;
import com.vaccinetracker.services.AuthService;
import com.vaccinetracker.services.UserService;
import com.vaccinetracker.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

/**
 * Controller for the Login View.
 * Handles role selection and navigation to appropriate dashboards.
 */
public class LoginController {
    
    @FXML
    private ToggleButton adminToggle;
    
    @FXML
    private ToggleButton vaccinatorToggle;

    @FXML
    private ToggleButton parentToggle;

    @FXML
    private ToggleGroup roleToggle;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Hyperlink forgotLink;
    
    // Services
    private UserService userService;
    private AuthService authService;
    
    /**
     * Initialize the controller.
     * Called automatically when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialize services
        userService = new UserService();
        authService = new AuthService(userService);
        
        // Add listener to toggle group
        roleToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ToggleButton selected = (ToggleButton) newValue;
                if ("Vaccinator".equals(selected.getText())) {
                    usernameField.setPromptText("Vaccinator ID");
                } else {
                    usernameField.setPromptText("Username");
                }
            }
        });
    }
    
    /**
     * Handle login button click.
     * Authenticates user and navigates to dashboard.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Check if the selected role matches the user's role
        ToggleButton selectedToggle = (ToggleButton) roleToggle.getSelectedToggle();
        String selectedRole = selectedToggle.getText().toUpperCase(); // "Admin" -> "ADMIN", "Parent" -> "PARENT"
        
        // Special validation for Vaccinator
        if ("VACCINATOR".equals(selectedRole)) {
            if (!username.startsWith("NUR") && !username.startsWith("DOC")) {
                showError("Login Failed", "Invalid Vaccinator ID.\nOnly Nurse (NUR...) and Doctor (DOC...) IDs are allowed.");
                return;
            }
        }
        
        // Authenticate using the service
        User user = authService.authenticate(username, password);
        
        if (user == null) {
            showError("Login Failed", "Invalid credentials.");
            return;
        }
        
        if (!user.getRole().equals(selectedRole)) {
            showError("Login Failed", "Invalid credentials for selected role.\nPlease check if you selected the correct role (Admin/Vaccinator/Parent).");
            return;
        }
        
        if (user.getRole().equals("ADMIN")) {
            handleAdminLogin(user);
        } else if (user.getRole().equals("PARENT")) {
            handleParentLogin(user);
        } else if (user.getRole().equals("VACCINATOR")) {
            handleVaccinatorLogin(user);
        }
    }

    private void handleVaccinatorLogin(User user) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/view/VaccinatorDashboard.fxml");
            if (fxmlUrl == null) {
                showError("Error", "Vaccinator Dashboard FXML not found!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            VaccinatorController controller = loader.getController();
            if (controller != null) {
                controller.setVaccinator((com.vaccinetracker.model.Vaccinator) user);
            }
            
            App.setRoot(root, "Vaccinator Dashboard - DoseCare");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Could not load Vaccinator Dashboard: " + e.getMessage());
        }
    }

    private void handleAdminLogin(User admin) {
        // Disable button to prevent multiple clicks
        loginButton.setDisable(true);
        
        try {
            System.out.println("Starting admin login...");
            System.out.println("Admin authenticated: " + admin.getName());
            
            if (admin != null) {
                // Load admin dashboard
                System.out.println("Loading AdminDashboard.fxml...");
                java.net.URL fxmlUrl = getClass().getResource("/view/AdminDashboard.fxml");
                if (fxmlUrl == null) {
                    System.err.println("ERROR: Cannot find AdminDashboard.fxml");
                    showError("Error", "Cannot find AdminDashboard.fxml file.\nPlease check if the file exists in src/main/resources/view/");
                    loginButton.setDisable(false);
                    return;
                }
                
                System.out.println("FXML URL found: " + fxmlUrl);
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                System.out.println("Loading FXML...");
                Parent root = loader.load();
                System.out.println("FXML loaded successfully");
                
                // Pass services to admin controller
                AdminController controller = loader.getController();
                if (controller == null) {
                    System.err.println("ERROR: Controller is null");
                    showError("Error", "Failed to load AdminController.\nCheck if fx:controller is set correctly in AdminDashboard.fxml");
                    loginButton.setDisable(false);
                    return;
                }
                
                System.out.println("Initializing controller...");
                controller.setUserService(userService);
                controller.setCurrentUser(admin);
                // controller.initialize(); // Removed manual call as FXMLLoader calls it automatically
                
                // Show admin dashboard
                System.out.println("Setting root...");
                App.setRoot(root, "Admin Dashboard - Vaccine Tracker");
                System.out.println("Admin dashboard loaded successfully!");
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION in handleAdminLogin:");
            e.printStackTrace();
            showError("Error Loading Admin Dashboard", 
                     "An error occurred:\n" + e.getClass().getSimpleName() + ": " + e.getMessage() + 
                     "\n\nCheck the NetBeans Output window for details.");
            loginButton.setDisable(false);
        }
    }
    
    /**
     * Handle parent login button click.
     * Authenticates as parent and navigates to parent dashboard.
     */
    private void handleParentLogin(User parent) {
        // Disable button to prevent multiple clicks
        loginButton.setDisable(true);
        
        try {
            System.out.println("Starting parent login...");
            System.out.println("Parent authenticated: " + parent.getName());
            
            if (parent != null) {
                // Load parent dashboard
                System.out.println("Loading ParentDashboard.fxml...");
                java.net.URL fxmlUrl = getClass().getResource("/view/ParentDashboard.fxml");
                if (fxmlUrl == null) {
                    System.err.println("ERROR: Cannot find ParentDashboard.fxml");
                    showError("Error", "Cannot find ParentDashboard.fxml file.\nPlease check if the file exists in src/main/resources/view/");
                    loginButton.setDisable(false);
                    return;
                }
                
                System.out.println("FXML URL found: " + fxmlUrl);
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                System.out.println("Loading FXML...");
                Parent root = loader.load();
                System.out.println("FXML loaded successfully");
                
                // Pass services to parent controller
                ParentController controller = loader.getController();
                if (controller == null) {
                    System.err.println("ERROR: Controller is null");
                    showError("Error", "Failed to load ParentController.\nCheck if fx:controller is set correctly in ParentDashboard.fxml");
                    loginButton.setDisable(false);
                    return;
                }
                
                System.out.println("Initializing controller...");
                controller.setUserService(userService);
                controller.setCurrentUser(parent);
                controller.initialize();
                
                // Show parent dashboard
                System.out.println("Setting root...");
                App.setRoot(root, "Parent Dashboard - Vaccine Tracker");
                System.out.println("Parent dashboard loaded successfully!");
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION in handleParentLogin:");
            e.printStackTrace();
            showError("Error Loading Parent Dashboard", 
                     "An error occurred:\n" + e.getClass().getSimpleName() + ": " + e.getMessage() + 
                     "\n\nCheck the NetBeans Output window for details.");
            loginButton.setDisable(false);
        }
    }
    
    @FXML
    private void handleSignUp() {
        // Create a custom dialog for sign up
        Dialog<com.vaccinetracker.model.Parent> dialog = new Dialog<>();
        dialog.setTitle("Parent Sign Up");
        dialog.setHeaderText("Create a new Parent Account");

        // Set the button types
        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        // Create the username, password, and other labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField contactField = new TextField();
        contactField.setPromptText("Email or Phone");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Contact Info:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a parent object when the sign up button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                // Basic validation
                if (nameField.getText().isEmpty() || usernameField.getText().isEmpty() || 
                    passwordField.getText().isEmpty()) {
                    return null;
                }
                
                // Generate a unique ID
                String userId = "PAR" + String.format("%03d", userService.getUserCount() + 1);
                
                // Create the parent using the service (which adds it to the list)
                com.vaccinetracker.model.Parent newParent = userService.createParent(
                    userId, 
                    nameField.getText(), 
                    contactField.getText(), 
                    addressField.getText()
                );
                
                // Set the username and password manually since createParent uses defaults
                newParent.setUsername(usernameField.getText());
                newParent.setPassword(passwordField.getText());
                
                return newParent;
            }
            return null;
        });

        Optional<com.vaccinetracker.model.Parent> result = dialog.showAndWait();

        result.ifPresent(parent -> {
            // Save data immediately to ensure persistence
            com.vaccinetracker.services.StorageService.saveAll();
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Sign Up Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! You can now login with your credentials.");
            alert.showAndWait();
            
            // Pre-fill the login fields
            this.usernameField.setText(parent.getUsername());
            this.passwordField.setText("");
            parentToggle.setSelected(true);
        });
    }
    
    /**
     * Show an error alert dialog.
     * Ensures it runs on JavaFX Application Thread.
     */
    private void showError(String title, String message) {
        // Ensure we're on JavaFX thread
        if (javafx.application.Platform.isFxApplicationThread()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } else {
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }
    }
}

