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

/**
 * Controller for the Login View.
 * Handles role selection and navigation to appropriate dashboards.
 */
public class LoginController {
    
    @FXML
    private ToggleButton adminToggle;
    
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
    }
    
    /**
     * Handle login button click.
     * Authenticates user and navigates to dashboard.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminToggle.isSelected();

        // TODO: Implement actual authentication logic here
        // For now, just simulate navigation based on role
        
        if (isAdmin) {
            handleAdminLogin();
        } else {
            handleParentLogin();
        }
    }

    private void handleAdminLogin() {
        // Disable button to prevent multiple clicks
        loginButton.setDisable(true);
        
        try {
            System.out.println("Starting admin login...");
            
            // Authenticate as admin
            User admin = authService.authenticateByRole("ADMIN");
            System.out.println("Admin authenticated: " + (admin != null));
            
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
            } else {
                showError("Authentication Error", "Failed to authenticate as Admin.\nPlease check UserService.");
                loginButton.setDisable(false);
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
    private void handleParentLogin() {
        // Disable button to prevent multiple clicks
        loginButton.setDisable(true);
        
        try {
            System.out.println("Starting parent login...");
            
            // Authenticate as parent
            User parent = authService.authenticateByRole("PARENT");
            System.out.println("Parent authenticated: " + (parent != null));
            
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
            } else {
                showError("Authentication Error", "Failed to authenticate as Parent.\nPlease check UserService.");
                loginButton.setDisable(false);
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

