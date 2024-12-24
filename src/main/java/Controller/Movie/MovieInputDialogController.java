package Controller.Movie;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Movie;
import Service.MovieService;

import java.io.File;
import java.time.LocalDate;

public class MovieInputDialogController {
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField genreTextField;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextField releaseDateTextField;
    @FXML
    private TextField imagePathTextField; // Added field for image path
    @FXML private DatePicker releaseDatePicker;
    @FXML private Button uploadImageButton;
    @FXML private ImageView imagePreview;
    private MovieService movieService;
    private String imagePath = ""; // To store the image file path
    // Default constructor
    public MovieInputDialogController() {
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }
    @FXML
    private void handleImageUpload() {
        // Create a file chooser for image files
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        Stage stage = (Stage) uploadImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();  // Set the image path to the absolute path of the selected file
            System.out.println("Image selected: " + imagePath);  // Log the image path for debugging

            // Show the selected image in the ImageView
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setImage(image);  // Set the image on the ImageView
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleAddMovie() {
        System.out.println("Add button was pressed.");

        try {
            // Get text field values
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String genre = genreTextField.getText();
            int duration = Integer.parseInt(durationTextField.getText());
            LocalDate releaseDate = releaseDatePicker.getValue();  // Use DatePicker value

            // Get image path from the file selected during the upload process
            String imagePath = this.imagePath;  // This should be set from the image upload button

            // Check if all fields are filled out
            if (title.isEmpty() || description.isEmpty() || genre.isEmpty() || imagePath.isEmpty() || durationTextField.getText().isEmpty() || releaseDate == null) {
                showAlert("Error", "Please fill in all fields.");
                return; // Prevent further processing if any field is empty
            }

            // Create new Movie object
            Movie newMovie = new Movie(title, imagePath, description, genre, duration, releaseDate);

            // Check if the movieService is available to add the movie
            if (movieService != null) {
                movieService.addMovie(newMovie); // Add movie to the service
                showAlert("Success", "Movie has been added successfully.");
            } else {
                System.err.println("movieService is null!"); // Notify if movieService is null
                showAlert("Error", "Movie service is not initialized.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid duration.");
        } catch (Exception e) {
            showAlert("Error", "Unable to add movie: " + e.getMessage());
        }
    }

    @FXML
    public void handleExit() {
        Stage stage = (Stage) titleTextField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
