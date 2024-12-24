package Controller.Movie;

import Controller.User.AdminController;
import Service.MovieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSelectMovieDialogController extends AdminController {
    private AdminController adminController;
    private MovieService movieService;
    private Stage dialogStage; // Declare the dialogStage property


    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage; // Set the dialogStage
    }

    @FXML
    public void handleAddMovie() {
        if (dialogStage != null) {
            dialogStage.close(); // Close the current dialog
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/MovieInputDialog.fxml"));
            Parent root = loader.load();

            MovieInputDialogController controller = loader.getController();
            controller.setMovieService(new MovieService()); // Set MovieService for the controller

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Movie");
            dialogStage.setScene(new Scene(root));
            this.setDialogStage(dialogStage); // Set the dialogStage property

            dialogStage.show(); // Show the dialog
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the add movie dialog box.");
        }
    }

    @FXML
    public void handleDeleteMovie() {
        if (dialogStage != null) {
            dialogStage.close(); // Close the current dialog
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/DeleteMovieDialog.fxml"));
            Parent root = loader.load();

            DeleteMovieDialogController deleteMovieDialogController = loader.getController();
            deleteMovieDialogController.setAdminController(adminController);
            deleteMovieDialogController.setMovieService(new MovieService());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Delete Movie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(adminController.getStage());
            dialogStage.setScene(new Scene(root));

            deleteMovieDialogController.setDialogStage(dialogStage);
            this.setDialogStage(dialogStage); // Set the dialogStage property

            dialogStage.showAndWait(); // Show the dialog and wait
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the delete movie dialog box.");
        }
    }

    @FXML
    public void handleViewMovies() {
        if (dialogStage != null) {
            dialogStage.close(); // Close the current dialog
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/MovieListDialog.fxml"));
            Parent root = loader.load();

            MovieListDialogController movieListDialogController = loader.getController();
            movieListDialogController.setMovieService(new MovieService());
            movieListDialogController.setAdminController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Movie List");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.getStage());
            dialogStage.setScene(new Scene(root));

            movieListDialogController.setDialogStage(dialogStage);
            this.setDialogStage(dialogStage); // Set the dialogStage property

            dialogStage.showAndWait(); // Show the dialog and wait
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the movie list dialog box.");
        }
    }

    @FXML
    public void handleExit() {
        if (dialogStage != null) {
            dialogStage.close(); // Close the dialog if dialogStage is not null
        } else {
            showAlert("Notification", "No dialog to close!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
