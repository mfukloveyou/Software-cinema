package Controller.User;

import Service.AdminService;
import Service.MovieService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import model.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AdminController {
    private final AdminService adminService;
    private final MovieService movieService;
    private Stage stage; // The main application stage

    // Method to set the stage, should be called when the controller is initialized
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    private List<Movie> movieList = new ArrayList<>(); // Variable to store the list of movies

    public List<Movie> loadAllMovies() {
        movieList.clear();
        movieList = movieService.getAllMovies();
        System.out.println("Number of movies in the list: " + movieList.size());

        if (movieList.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Notification", "There are no movies in the list.");
        } else {
            showAlert(AlertType.INFORMATION, "Success", "The movie list has been loaded.");
        }
        return null;
    }

    public void showAdminFunctionsDialog() {
        try {
            // Load the interface from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Admin/AdminFunctionsDialog.fxml"));
            Parent root = loader.load();

            // Get the AdminFunctionsDialogController from FXMLLoader
            AdminFunctionsDialogController adminFunctionsDialogController = loader.getController();

            // If needed, pass the reference of AdminController to AdminFunctionsDialogController
            AdminController adminController = new AdminController(); // You should pass an existing instance or singleton of AdminController
            adminFunctionsDialogController.setAdminController(adminController);

            // Create a dialog box and set its content
            Dialog<ButtonType> functionDialog = new Dialog<>();
            functionDialog.setTitle("Admin Functions");
            functionDialog.setHeaderText("Select the function you want to perform");
            functionDialog.getDialogPane().setContent(root);
            functionDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Show the dialog box
            functionDialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public AdminController() {
        this.adminService = new AdminService();
        this.movieService = new MovieService();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter both username and password.");
            return;
        }

        if (adminService.loginAdmin(username, password)) {

            loadAllMovies();
            closeWindow();
            showAdminFunctionsDialog();
        } else {
            showAlert(AlertType.ERROR, "Failure", "Login failed. Please check your credentials.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    public void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to add a new user

    // Method to get the list of all users

    // Method to get the list of all movies
    public List<Movie> getMovieList() {
        return movieList;
    }

    // Method to get the list of all showtimes
}
