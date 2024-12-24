package Controller.Movie;

import Controller.User.AdminController;
import Service.MovieService;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Movie;

import java.util.List;

public class MovieListDialogController {

    @FXML
    private ListView<String> movieListView;

    @FXML
    private ImageView movieImageView;

    @FXML
    private Text titleText;

    @FXML
    private Text genreText;

    @FXML
    private Text durationText;

    @FXML
    private Text releaseDateText;

    private MovieService movieService;
    private Stage dialogStage;
    private AdminController adminController;
    private List<Movie> movieList;

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
        updateMovieList();
    }

    public void updateMovieList() {
        if (adminController != null && movieListView != null) {
            movieListView.getItems().clear();
            adminController.loadAllMovies();
            movieList = adminController.getMovieList();
            for (Movie movie : movieList) {
                movieListView.getItems().add(movie.getTitle() + " (ID: " + movie.getId() + ")");
            }

            // Listen for selection change events in ListView
            movieListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.intValue() >= 0) {
                    displayMovieDetails(newVal.intValue());
                }
            });
        }
    }

    // Display movie details
    private void displayMovieDetails(int index) {
        Movie selectedMovie = movieList.get(index);
        titleText.setText("Title: " + selectedMovie.getTitle());
        genreText.setText("Genre: " + selectedMovie.getGenre());
        durationText.setText("Duration: " + selectedMovie.getDuration() + " mins");
        releaseDateText.setText("Release Date: " + (selectedMovie.getReleaseDate() != null ? selectedMovie.getReleaseDate().toString() : "N/A"));

        // Display the movie image if available
        if (selectedMovie.getImagePath() != null) {
            Image image = new Image(selectedMovie.getImagePath());
            movieImageView.setImage(image);
        } else {
            movieImageView.setImage(null);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCloseButton() {
        dialogStage.close();
    }
}
