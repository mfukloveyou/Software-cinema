package Controller.Showtime;

import Controller.User.AdminController;
import Service.MovieService;
import Service.ShowtimeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Movie;
import model.Showtime;

import java.io.IOException;
import java.util.List;

public class ShowtimeManagementController {
    private AdminController adminController;

    @FXML
    private ListView<Showtime> showtimeListView; // Ensure you have declared the ListView for showtimes

    private Stage dialogStage; // Added this variable

    // Method to set the dialogStage
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Method to set the AdminController
    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    @FXML
    private ListView<String> movieListView;

    private MovieService movieService = new MovieService();
    private ShowtimeService showtimeService = new ShowtimeService();
    private List<Movie> movieList;

    public void initialize() {
        loadMovies();
    }

    private void loadMovies() {
        movieList = movieService.getAllMovies();
        movieListView.getItems().clear();
        for (Movie movie : movieList) {
            movieListView.getItems().add(movie.getTitle() + " (ID: " + movie.getId() + ")");
        }
    }

    @FXML
    private void handleAddShowtime() {
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Movie selectedMovie = movieList.get(selectedIndex);
            showAddShowtimeDialog(selectedMovie);
        } else {
            showAlert("Error", "Please select a movie to add a showtime.");
        }
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleDeleteShowtime() {
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Movie selectedMovie = movieList.get(selectedIndex);
            showDeleteShowtimeDialog(selectedMovie);
        } else {
            showAlert("Error", "Please select a movie to delete its showtime.");
        }
    }

    private void loadShowtimes() {
        // Get the list of showtimes from the database and update the interface
        List<Showtime> showtimes = showtimeService.getAllShowTimes();

        // Update ListView or TableView with the list of showtimes
        showtimeListView.getItems().clear();
        showtimeListView.getItems().addAll(showtimes);
    }

    private void showAddShowtimeDialog(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Showtime/AddShowtimeDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Showtime");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(loader.load()));

            AddShowtimeDialogController controller = loader.getController();
            controller.setMovie(movie);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            loadMovies(); // Refresh the movie list after adding a showtime
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteShowtimeDialog(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Showtime/DeleteShowtimeDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Delete Showtime");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(loader.load()));

            DeleteShowtimeDialogController controller = loader.getController();
            controller.setMovie(movie); // Call method to set movie
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            loadMovies(); // Refresh the movie list after deleting a showtime
        } catch (IOException e) {
            e.printStackTrace();
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
