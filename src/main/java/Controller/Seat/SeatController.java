package Controller.Seat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import Controller.Ticket.TicketController;
import Service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import model.Seat;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Showtime;
import model.Ticket;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SeatController {
    private ShowtimeService showtimeService;
    private TicketService ticketService;

    public SeatController() {
        try {
            Connection connection = DatabaseConnection.getConnection(); // Connect to database
            showtimeService = new ShowtimeService(connection); // Initialize ShowtimeService
            ticketService = new TicketService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button confirmButton; // Button to confirm seat reservation

    private SeatService seatService;

    private int movieId; // Store movie ID
    private String movieTitle; // Store movie title
    private String showtimeDetails; // Store showtime details
    private int userId;

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection(); // Connect to database
            seatService = new SeatService(connection); // Initialize SeatService
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Connection Error", "Could not connect to the database.");
        }
    }

    @FXML
    private GridPane seatGridPane;

    // Show seat selection dialog
    public void showSeatDialog(int showtimeId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Seat/SeatDialog.fxml"));
            Parent root = loader.load();

            // Get SeatController object from loader
            SeatController seatController = loader.getController();
            seatController.loadSeats(showtimeId);

            // Create a new stage for the dialog
            Stage seatDialog = new Stage();
            seatDialog.setTitle("Select Seat");
            seatDialog.setScene(new Scene(root));
            seatDialog.initModality(Modality.APPLICATION_MODAL); // Modal dialog
            seatDialog.showAndWait(); // Wait for user interaction
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show ticket dialog
    private void showTicketDialog(Ticket ticket) {
        try {
            // Load FXML for TicketDialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Ticket/TicketDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ticket Information");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // Create scene and set it in the stage
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            // Get controller and pass ticket information
            TicketController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTicket(ticket);

            // Show the dialog
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LoggedInUser class to store user and movie ID
    public class LoggedInUser {
        private static int userId;
        private static int movieId; // Add movieId variable

        public static int getUserId() {
            return userId;
        }

        public static void setUserId(int userId) {
            LoggedInUser.userId = userId;
        }

        public static void setMovieId(int movieId) {
            LoggedInUser.movieId = movieId;
        }
    }

    @FXML
    public void loadSeats(int showtimeId) {
        List<Seat> seats = seatService.getSeatsByShowtimeId(showtimeId);
        seatGridPane.getChildren().clear();
        int movieId = showtimeService.getMovieIdByShowtimeId(showtimeId);

        if (movieId != -1) {
            Showtime selectedShowtime = showtimeService.getShowtimeDetailsById(showtimeId);

            if (selectedShowtime != null) {
                this.movieId = movieId;
                this.movieTitle = selectedShowtime.getTitle();
                this.showtimeDetails = selectedShowtime.getShowtimeDetails();
                this.userId = LoggedInUser.getUserId();

                int col = 0;
                int row = 0;
                for (Seat seat : seats) {
                    Button seatButton = new Button(seat.getSeatNumber());
                    seatButton.setPrefSize(50, 50);
                    seatButton.getStyleClass().add(seat.isBooked() ? "booked-seat" : "available-seat");

                    seatButton.setOnAction(event -> handleSeatSelection(seat, seatButton, showtimeId));
                    seatGridPane.add(seatButton, col, row);
                    col++;
                    if (col == 5) {
                        col = 0;
                        row++;
                    }
                }
            } else {
                System.out.println("Showtime not found for ID: " + showtimeId);
            }
        } else {
            System.out.println("Movie not found for Showtime ID: " + showtimeId);
        }
    }

    // Handle seat selection
    // Handle seat selection
    private void handleSeatSelection(Seat seat, Button seatButton, int showtimeId) {
        if (seat.isBooked()) {
            // If the seat is already booked, show an error message
            showErrorAlert("Error", "This seat is already booked.");
        } else {
            System.out.println("Seat " + seat.getSeatNumber() + " is selected.");

            // Change the seat's color to indicate it is selected
            seatButton.setStyle("-fx-background-color: yellow;");

            // Create a Ticket object with the selected seat information
            Ticket ticket = new Ticket(0, LoggedInUser.getUserId(), showtimeId, seat.getSeatId(),
                    Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime(),
                    movieId, showtimeDetails, seat.getSeatNumber(), movieTitle);

            // Show the ticket dialog for confirmation
            showTicketDialog(ticket);
        }
    }


    // Confirm booking action
    @FXML
    public void confirmBooking() {
        // Logic for confirming the booking, e.g., saving the booked seat to the database
        closeDialog(); // Close the dialog after confirmation
    }

    // Close the dialog
    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene().getWindow(); // Get current Stage
        stage.close(); // Close the dialog
    }

    // Show error alert
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
