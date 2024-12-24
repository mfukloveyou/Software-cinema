package Controller.User;

import Controller.Movie.AdminSelectMovieDialogController;
import Controller.Seat.SeatManagementController;
import Controller.Showtime.ShowtimeManagementController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AdminFunctionsDialogController extends AdminController {
    public VBox root;
    private  AdminController adminController;



    public void setAdminController(AdminController adminController) {

        this.adminController = adminController;
    }


    private Stage dialogStage; // Added this property
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleManageTickets() {
        if (dialogStage != null) {
            dialogStage.close();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Ticket/TicketManagement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ticket Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the ticket management dialog.");
        }
    }

    @FXML
    public void handleManageMovies() {
        if (adminController != null) {
            // Call the loadAllMovies method to ensure the movie list is updated from AdminService
            adminController.loadAllMovies();

            // Close the current dialog if it's open
            if (dialogStage != null) {
                dialogStage.close();
            }

            // Create the movie management dialog
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/AdminSelectMovieDialog.fxml"));
                Parent root = loader.load(); // Load the FXML

                // Get the controller set from the FXML
                AdminSelectMovieDialogController movieDialogController = loader.getController();
                movieDialogController.setAdminController(adminController);

                // Create a new Stage for the movie management dialog
                Stage movieStage = new Stage();
                movieStage.setTitle("Manage Movies");
                movieStage.setScene(new Scene(root));

                // Set the dialogStage for the controller
                movieDialogController.setDialogStage(movieStage);

                // Show the movie management dialog
                movieStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to open the movie management dialog.");
            }
        }
    }


    @FXML
    public void handleManageShowtimes() {
        if (adminController != null) {
            // Call to get the list of showtimes from AdminController if needed

            if (dialogStage != null) {
                dialogStage.close();
            }
            // Open the showtime management dialog
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Showtime/ShowtimeManagementDialog.fxml"));
                Parent root = loader.load(); // Load FXML

                // Get the ShowtimeManagementController from FXML
                ShowtimeManagementController showtimeController = loader.getController();
                showtimeController.setAdminController(adminController); // Set the AdminController if needed

                // Create and show a new window
                Stage stage = new Stage();
                stage.setTitle("Manage Showtimes");
                stage.setScene(new Scene(root));
                showtimeController.setDialogStage(stage);
                stage.show(); // Show the dialog
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to open the showtime management dialog.");
            }
        }
    }

    @FXML
    private void handleManageSeats() {
        if (dialogStage != null) {
            dialogStage.close();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Seat/SeatManagementView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Manage Seats");
            stage.setScene(new Scene(root));

            SeatManagementController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.loadShowtimes(); // Load the list of showtimes when opening the interface
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleManageUsers() {
        if (dialogStage != null) {
            dialogStage.close();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Admin/ManageUsersDialog.fxml"));
            DialogPane dialogPane = loader.load();

            ManageUsersController manageUsersController = loader.getController();
            manageUsersController.loadAllUsers(); // Load the list of users

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Manage Users");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the user management dialog.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
