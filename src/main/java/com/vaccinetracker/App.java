package com.vaccinetracker;

import com.vaccinetracker.services.StorageService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX Application - Vaccine Tracker
 * Main entry point for the application.
 */
public class App extends Application {
    
    private static Stage primaryStage;
    private static Scene mainScene;

    @Override
    public void start(Stage stage) {
        try {
            // Load data from disk
            StorageService.loadAll();
            
            // Add shutdown hook to ensure data is saved even if terminated via terminal
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown hook triggered. Saving data...");
                StorageService.saveAll();
            }));
            
            primaryStage = stage;
            
            System.out.println("App.start() called");
            
            // Load the login view
            java.net.URL fxmlUrl = getClass().getResource("/view/Login.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERROR: Cannot find Login.fxml");
                showErrorAndExit(stage, "Cannot find Login.fxml file.\nPlease check if the file exists in src/main/resources/view/");
                return;
            }
            
            System.out.println("Loading Login.fxml from: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);
            System.out.println("Login.fxml loaded successfully");
            
            // Create scene
            mainScene = new Scene(root, 900, 600);
            
            // Load Global Theme CSS
            java.net.URL themeUrl = getClass().getResource("/styles/theme.css");
            if (themeUrl != null) {
                mainScene.getStylesheets().add(themeUrl.toExternalForm());
                System.out.println("Theme CSS loaded successfully");
            } else {
                System.err.println("WARNING: Cannot find styles/theme.css");
            }

            // Load Legacy/Global CSS
            java.net.URL cssUrl = getClass().getResource("/styles.css");
            if (cssUrl != null) {
                mainScene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Set stage properties
            stage.setTitle("Vaccine Tracker - Login");
            stage.setScene(mainScene);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.centerOnScreen();
            stage.show();
            
            System.out.println("Application started successfully!");
        } catch (Exception e) {
            System.err.println("FATAL ERROR in App.start():");
            e.printStackTrace();
            showErrorAndExit(stage, "Failed to start application:\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Show error and exit application.
     */
    private void showErrorAndExit(Stage stage, String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to Start Application");
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Could not show error dialog: " + e.getMessage());
        }
        javafx.application.Platform.exit();
    }
    
    /**
     * Static method to change the root node of the scene.
     * This allows navigation between different FXML views.
     * 
     * @param root The new root node (loaded from FXML)
     * @param title The window title
     */
    public static void setRoot(Parent root, String title) {
        if (primaryStage != null && mainScene != null) {
            mainScene.setRoot(root);
            primaryStage.setTitle(title);
            
            // Apply CSS if not already applied
            if (mainScene.getStylesheets().isEmpty()) {
                mainScene.getStylesheets().add(App.class.getResource("/styles/theme.css").toExternalForm());
            }
        }
    }
    
    /**
     * Get the primary stage.
     * 
     * @return The primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    @Override
    public void stop() {
        StorageService.saveAll();
    }

    public static void main(String[] args) {
        launch();
    }
}