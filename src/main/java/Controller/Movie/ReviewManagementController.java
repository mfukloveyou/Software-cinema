package Controller.Movie;

import Service.ReviewService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Movie;
import model.Review;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReviewManagementController {

    @FXML
    private ListView<String> reviewsListView; // List of reviews
    @FXML
    private TextArea commentTextArea; // Text area for entering comments
    @FXML
    private ComboBox<Integer> ratingComboBox; // ComboBox for selecting the rating

    private Movie movie; // The current movie
    private ReviewService reviewService; // Review service

    // Initialize the ReviewManagementController with the selected movie
    public void setMovie(Movie movie, Connection connection) {
        this.movie = movie;
        this.reviewService = new ReviewService(connection); // Initialize the ReviewService

        loadReviews(); // Load existing reviews for the movie
    }

    // Load the existing reviews from the database
    private void loadReviews() {
        List<Review> reviews = reviewService.getReviewsByMovieId(movie.getId());
        reviewsListView.getItems().clear();
        for (Review review : reviews) {
            String reviewText = "User: " + review.getUserId() + "\nRating: " + review.getRating() + "\nComment: " + review.getComment();
            reviewsListView.getItems().add(reviewText);
        }
    }

    // Save the user's review
    @FXML
    private void handleSaveReview() {
        String comment = commentTextArea.getText();
        Integer rating = ratingComboBox.getValue();

        if (comment.isEmpty() || rating == null) {
            // Show a warning if the user has not entered a comment and rating
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both a comment and a rating!");
            alert.showAndWait();
            return;
        }

        // Save the review to the database
        try {
            int userId = MovieController.LoggedInUser.getUserId();
            reviewService.addReview(movie.getId(), userId, rating, comment); // Add the review to DB
            loadReviews(); // Reload the list of reviews after adding the new one
            commentTextArea.clear(); // Clear the comment text area after saving
            ratingComboBox.setValue(null); // Reset the ComboBox
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the dialog
    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) reviewsListView.getScene().getWindow();
        stage.close();
    }
}
