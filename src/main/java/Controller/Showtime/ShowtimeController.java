package Controller.Showtime;

import Controller.Seat.SeatController;
import Service.SeatService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Seat;
import model.Showtime;
import Service.ShowtimeService;
import Service.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeController {

    @FXML
    private ListView<Showtime> showtimeListView;

    private ShowtimeService showtimeService;

    public ShowtimeController() {

    }

    @FXML
    private Button closeButton;

    @FXML
    private void handleClose() {
        // Close the dialog
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection(); // Create a connection
            showtimeService = new ShowtimeService(); // Initialize ShowtimeService with the connection
            loadShowtimes(); // Load showtimes
            seatService = new SeatService(connection);

            // Use CellFactory
            showtimeListView.setCellFactory(param -> new ShowtimeListCell());
        } catch (SQLException e) {
            e.printStackTrace(); // Print the error if unable to connect to the database
            showErrorAlert("Connection Error", "Unable to connect to the database.");
        }
    }

    private void loadShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowTimes(); // Get list of showtimes

        if (showtimes.isEmpty()) {
            showErrorAlert("Notification", "No showtimes available to display.");
        } else {
            showtimeListView.getItems().addAll(showtimes); // Add the list of showtimes to ListView
        }
    }

    private SeatService seatService; // Declare seatService variable

    public void openSeatsDialog(int showtimeId) {
        try {
            // Create a new instance of SeatController
            SeatController seatController = new SeatController();
            seatController.showSeatDialog(showtimeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelectShowtime() {
        System.out.println("handleSelectShowtime was called");

        Showtime selectedShowtime = showtimeListView.getSelectionModel().getSelectedItem(); // Use Showtime instead of String
        System.out.println("Selected showtime: " + selectedShowtime);

        if (selectedShowtime != null) {
            // Get movie title and showtime directly from the Showtime object
            String movieTitle = selectedShowtime.getTitle();
            LocalDateTime showtime = selectedShowtime.getShowtime();

            // Call ShowtimeService to get the showtime_id
            int showtimeId = showtimeService.getShowtimeIdByMovieAndTime(movieTitle, showtime);
            System.out.println("Showtime ID: " + showtimeId);
            openSeatsDialog(showtimeId);

            if (showtimeId != -1) {
                // Get list of seats
                List<Seat> seats = null;
                try {
                    seats = seatService.getSeatsByShowtimeId(showtimeId);
                    System.out.println("Seat list retrieved successfully.");
                } catch (Exception e) {
                    System.err.println("Error fetching seat list: " + e.getMessage());
                }

                // Print the list of seats to terminal
                if (seats != null) {
                    System.out.println("Seat list:");
                    for (Seat seat : seats) {
                        System.out.println("Seat ID: " + seat.getSeatId() + ", Status: " + (seat.isBooked() ? "Booked" : "Available"));
                    }
                } else {
                    System.out.println("No seats found for showtime ID: " + showtimeId);
                }
            } else {
                showErrorAlert("Notification", "Showtime not found.");
            }
        } else {
            showErrorAlert("Notification", "Please select a showtime first.");
        }
    }

    @FXML
    public void showShowtimeDialog(List<Showtime> showtimes) {
        try {
            // Load the FXML of the dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Showtime/ShowtimeDialog.fxml"));
            Parent root = loader.load();

            // Get the controller of the loaded FXML
            ShowtimeController controller = loader.getController();
            controller.setShowtimes(showtimes); // Pass the list of showtimes to the dialog

            // Create a new dialog (Stage)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Showtimes");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // Ensure that ListView is assigned a value from FXML
            if (showtimeListView != null && showtimeListView.getScene() != null) {
                dialogStage.initOwner(showtimeListView.getScene().getWindow()); // Set parent window for the dialog
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setShowtimes(List<Showtime> showtimes) {
        showtimeListView.getItems().clear();
        showtimeListView.getItems().addAll(showtimes); // Directly add the list of showtimes to ListView
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
