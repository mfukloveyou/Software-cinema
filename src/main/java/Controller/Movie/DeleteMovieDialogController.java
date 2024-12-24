package Controller.Movie;

import Controller.User.AdminController;
import Service.MovieService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Movie;

import java.util.List;

public class DeleteMovieDialogController {

    @FXML
    private ListView<String> movieListView; // Display the list of movie titles
    private MovieService movieService;

    private Stage dialogStage;
    private AdminController adminController;

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    // Method to get MovieService
    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
        updateMovieList(); // Update the movie list when AdminController is set
    }

    // Update the movie list
    public void updateMovieList() {
        if (adminController != null && movieListView != null) {
            movieListView.getItems().clear(); // Clear the current list
            adminController.loadAllMovies(); // Load the movie list via AdminController
            List<Movie> movies = adminController.getMovieList(); // Get the movie list from AdminController
            for (Movie movie : movies) {
                movieListView.getItems().add(movie.getTitle() + " (ID: " + movie.getId() + ")");
            }
        }
    }

    // Set the Stage for the dialog
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCloseButton() {
        dialogStage.close(); // Close the dialog
    }

    @FXML
    public void handleDeleteButton() {
        String selectedMovie = movieListView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            // Extract movie_id from the selected movie title
            int movieId = Integer.parseInt(selectedMovie.split("ID: ")[1].replace(")", ""));
            if (movieService.deleteMovie(movieId)) {
                // Movie deleted successfully
                movieListView.getItems().remove(selectedMovie);
                showAlert("Successfully deleted movie: " + selectedMovie.split(" ID")[0]);
            } else {
                showAlert("Could not delete movie: " + selectedMovie.split(" ID")[0]);
            }
        } else {
            showAlert("Please select a movie to delete."); // Alert if no movie is selected
        }
    }

    private void showAlert(String message) {
        // Show alert message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
