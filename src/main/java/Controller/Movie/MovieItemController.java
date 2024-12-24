package Controller.Movie;
import Controller.Showtime.ShowtimeController;
import Service.DatabaseConnection;
import Service.ReviewService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Movie;
import model.Review;
import model.Showtime;
import Service.ShowtimeService;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MovieItemController {
    @FXML
    private HBox movieItemBox; // HBox needs to be declared correctly
    @FXML
    private ImageView movieImageView;
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label movieGenreLabel;
    @FXML
    private Label movieDescriptionLabel;
    @FXML
    private Label movieRatingLabel;
    @FXML
    private Label movieDurationLabel;
    @FXML
    private Label movieReleaseDateLabel;
    @FXML
    private Button movieButton;
    @FXML
    private Button reviewButton;
    @FXML
    private ListView<String> reviewsListView;

    private Movie movie;
    private ShowtimeService showtimeService = new ShowtimeService(); // Initialize ShowtimeService
    private ReviewService reviewService; // Review service

    // This method initializes data for each movie item
    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection(); // Get database connection
            reviewService = new ReviewService(connection); // Initialize ReviewService with connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        movieTitleLabel.setText(movie.getTitle());
        movieGenreLabel.setText(movie.getGenre());
        movieDescriptionLabel.setText(movie.getDescription());
        movieDurationLabel.setText("Duration: " + movie.getDuration() + " minutes");
        movieReleaseDateLabel.setText("Release Date: " + movie.getReleaseDate());

        // Set image into ImageView
        Image image = new Image(movie.getImagePath(), 334, 250, true, true);  // Tải ảnh với độ phân giải tối đa 400x300
        movieImageView.setImage(image);
        // Đặt kích thước cho ImageView để phù hợp với tỷ lệ 3:4
        updateImageViewSize();

        // Áp dụng bo góc cho ảnh
        applyRoundCorners(movieImageView);
        // Calculate and display average rating
        loadAverageRating();

        // Register event for the movie selection button
        movieButton.setOnAction(event -> {
            // Call handleMovieSelection method when button is pressed
            handleMovieSelection(movie);
        });
        reviewButton.setOnAction(event -> {
            showReviewsDialog();
            // Add hover effect for movieItemBox
        });
    }
    private void updateImageViewSize() {
        // Đặt ImageView có kích thước là 400x300 px (tỷ lệ 3:4)
        double width = 334;
        double height = 250;

        movieImageView.setFitWidth(width);
        movieImageView.setFitHeight(height);
        movieImageView.setPreserveRatio(true);  // Giữ tỷ lệ 3:4 của ảnh
    }

    private void applyRoundCorners(ImageView imageView) {
        // Áp dụng bo góc cho ảnh với bán kính 20px
        Rectangle clip = new Rectangle();
        clip.setWidth(imageView.getFitWidth());
        clip.setHeight(imageView.getFitHeight());

        // Bo góc với bán kính 20px
        clip.setArcWidth(20);  // Bán kính bo góc theo chiều ngang
        clip.setArcHeight(20); // Bán kính bo góc theo chiều dọc

        imageView.setClip(clip);  // Áp dụng clip vào ImageView để bo góc ảnh
    }
    // Method to load the average rating
    private void loadAverageRating() {
        List<Review> reviews = reviewService.getReviewsByMovieId(movie.getId());
        if (reviews != null && !reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            movieRatingLabel.setText(String.format("Average Rating: %.1f", averageRating));
            displayStarRating(averageRating); // Display star rating
        } else {
            movieRatingLabel.setText("Average Rating: 0.0");
            displayStarRating(0.0); // Display no stars
        }
    }

    @FXML
    private HBox starRatingBox; // Added HBox in FXML to contain the stars
    private void displayStarRating(double rating) {
        starRatingBox.getChildren().clear(); // Clear previous stars

        // Full stars
        int fullStars = (int) rating;
        // Empty stars
        int emptyStars = 5 - Math.min(fullStars, 5);

        // Load full and empty star images once
        Image fullStarImage = loadStarImage("/images/star.png");
        Image emptyStarImage = loadStarImage("/images/staroff.png");

        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            ImageView star = new ImageView(fullStarImage);
            star.setFitHeight(20);
            star.setFitWidth(20);
            starRatingBox.getChildren().add(star);
        }

        // Add empty stars
        for (int i = 0; i < emptyStars; i++) {
            ImageView starOff = new ImageView(emptyStarImage);
            starOff.setFitHeight(20);
            starOff.setFitWidth(20);
            starRatingBox.getChildren().add(starOff);
        }
    }

    private Image loadStarImage(String resourcePath) {
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalArgumentException("Image " + resourcePath + " not found.");
            }
            return new Image(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Or you could throw an exception
        }
    }

    @FXML
    private void showReviewsDialog() {
        try {
            // Load the FXML interface for the review dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReviewManagementDialog.fxml"));
            Parent root = loader.load();

            // Get the controller object and pass movie information and database connection
            ReviewManagementController controller = loader.getController();
            Connection connection = DatabaseConnection.getConnection();
            controller.setMovie(movie, connection); // Pass movie and connection to the controller

            // Create and display the dialog
            Stage stage = new Stage();
            stage.setTitle("Review Management for Movie: " + movie.getTitle());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to handle when the user selects a movie
    private void handleMovieSelection(Movie movie) {
        // Get the list of showtimes from ShowtimeService
        List<Showtime> showtimes = showtimeService.getShowTimesByMovieId(movie.getId());

        if (showtimes.isEmpty()) {
            System.out.println("No showtimes available for movie: " + movie.getTitle());
            // Create and display an alert dialog
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Notification");
            alert.setHeaderText(null); // No header
            alert.setContentText("No showtimes available for movie: " + movie.getTitle());
            alert.showAndWait();
        } else {
            System.out.println("Showtimes for movie " + movie.getTitle() + ":");

            for (Showtime showtime : showtimes) {
                System.out.println("Showtime ID: " + showtime.getId() + ", Time: " + showtime.getShowtime());
            }

            // Create ShowtimeController object and call showShowtimeDialog method
            ShowtimeController showtimeController = new ShowtimeController();
            showtimeController.showShowtimeDialog(showtimes);
        }
    }
}
