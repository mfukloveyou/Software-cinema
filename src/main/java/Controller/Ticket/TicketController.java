package Controller.Ticket;

import Service.DatabaseConnection;
import Service.SeatService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Ticket;
import Service.TicketService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    @FXML
    private Label movieTitleLabel;
    @FXML
    private ImageView ticketImageView;
    @FXML
    private Label showtimeDetailsLabel;

    @FXML
    private Label userLabel;
    @FXML
    private Label seatNumberLabel;
    @FXML
    private ListView<Ticket> ticketListView;

    private Stage dialogStage;
    private Ticket ticket;
    private TicketService ticketService; // Service for saving ticket information

    // Default constructor initializes the TicketService with database connection
    public TicketController() {
        try {
            this.ticketService = new TicketService(DatabaseConnection.getConnection());

            if (this.ticketService == null) {
                throw new IllegalStateException("Unable to initialize TicketService. Database connection might have failed.");
            }

            System.out.println("TicketService initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing TicketService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {}

    public void initialize(List<Ticket> tickets) {
        ticketListView.setCellFactory(param -> new TicketListCell()); // Set factory for ListCell
        ticketListView.getItems().addAll(tickets); // Add tickets to ListView
    }

    // Method to set ticket information
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        movieTitleLabel.setText("Movie: " + ticket.getTitle());

        // Extract showtime
        String showtime = extractShowtime(ticket.getShowtimeDetails());
        showtimeDetailsLabel.setText("Showtime: " + showtime);

        seatNumberLabel.setText("Seat: " + ticket.getSeatNumber());
        userLabel.setText("User: " + ticket.getUserId());

        // Load ticket image
        String imagePath = "/images/ticket.png"; // Update path if needed
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ticketImageView.setImage(image); // Display image on ImageView

        try {
            this.ticketService = new TicketService(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            System.err.println("Error initializing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractShowtime(String showtimeDetails) {
        // Assuming the format is "Movie D Showtime 1 - 2024-10-20T14:00 | Seats available: 100"
        String[] parts = showtimeDetails.split(" - ");
        if (parts.length > 1) {
            return parts[1].split(" ")[0]; // Extract the date and time
        }
        return showtimeDetails; // Return original if not found
    }

    // Handle deleting a selected ticket
    @FXML
    private void handleDeleteTicket() {
        Ticket selectedTicket = ticketListView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            int ticketId = selectedTicket.getTicketId(); // Get ticket ID
            boolean canceled = ticketService.cancelTicket(ticketId);
            if (canceled) {
                ticketListView.getItems().remove(selectedTicket); // Remove from ListView
                showAlert("Notification", "Ticket has been successfully canceled.");
            } else {
                showAlert("Error", "Unable to cancel ticket. Please try again.");
            }
        } else {
            showAlert("Notification", "Please select a ticket to cancel.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Set Stage for dialog
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Close the dialog window
    @FXML
    private void handleCloseAction(ActionEvent event) {
        Stage stage = (Stage) ticketListView.getScene().getWindow();
        stage.close();
    }

    // Confirm ticket booking and update seat status
    @FXML
    private void handleConfirm() {
        try {
            boolean isBooked = ticketService.bookTicket(ticket);

            if (isBooked) {
                SeatService seatService = new SeatService(DatabaseConnection.getConnection());

                try {
                    boolean isSeatUpdated = seatService.updateSeatStatus(ticket.getSeatId(), true);

                    if (isSeatUpdated) {
                        // Success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Booking Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Ticket booked successfully, and seat status updated!");
                        alert.showAndWait();
                    } else {
                        // Error message if seat update fails
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Seat Update Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Ticket booked successfully, but unable to update seat status.");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    // Handle SQL errors
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Database Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error occurred while updating seat: " + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            } else {
                // Error message if ticket booking fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Booking Error");
                alert.setHeaderText(null);
                alert.setContentText("Error occurred while booking the ticket. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            // Handle database errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while booking the ticket: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }

        // Close the dialog
        dialogStage.close();
    }

    // Cancel and close the dialog
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic if needed
    }
}
