package Controller.Seat;

import Service.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Seat;
import model.Showtime;
import Service.SeatService;
import Service.ShowtimeService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SeatManagementController {
    @FXML
    private ScrollPane scrollPane; // Add ScrollPane in FXML

    @FXML
    private GridPane seatGrid; // Or HBox if you choose
    @FXML
    private ListView<Showtime> showtimeListView; // ListView for showing showtimes
    @FXML
    private VBox seatVBox; // Changed from ListView to VBox

    private SeatService seatService;
    private Showtime selectedShowtime; // Store the selected showtime
    private ShowtimeService showtimeService;
    private Stage dialogStage; // Variable to store the dialog stage
    // A new ListView just for testing

    // Setup selection mode
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Initialization method to set up SeatService
    @FXML
    public void initialize() {
        scrollPane.setContent(seatGrid); // Or seatHBox if you choose HBox
        scrollPane.setFitToWidth(true); // To make the ScrollPane fit the width
        try {
            Connection connection = DatabaseConnection.getConnection();
            seatService = new SeatService(connection);
            showtimeService = new ShowtimeService(connection);

            // Load the list of showtimes
            loadShowtimes();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to connect to the database.");
        }
    }

    // Method to load the list of showtimes
    @FXML
    public void loadShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowTimes();
        showtimeListView.setItems(FXCollections.observableArrayList(showtimes));

        // Tạo cellFactory để tùy chỉnh cách hiển thị mỗi Showtime
        showtimeListView.setCellFactory(param -> new ListCell<Showtime>() {
            @Override
            protected void updateItem(Showtime showtime, boolean empty) {
                super.updateItem(showtime, empty);

                if (empty || showtime == null) {
                    setText(null);
                } else {
                    // Tùy chỉnh cách hiển thị thông tin của Showtime
                    String displayText = String.format("ID: %d\nTitle: %s\nShowtime: %s\nAvailable Seats: %d",
                            showtime.getId(), showtime.getTitle(), showtime.getShowtime().toLocalDate().toString() + " " +
                                    showtime.getShowtime().toLocalTime().toString(), showtime.getAvailableSeats());
                    setText(displayText);
                }
            }
        });
    }

    // Method to handle selecting a showtime
    @FXML
    private void handleSelectShowtime() {
        selectedShowtime = showtimeListView.getSelectionModel().getSelectedItem();
        if (selectedShowtime != null) {
            loadSeats(selectedShowtime.getShowtimeId());
        } else {
            showAlert("Notice", "Please select a showtime.");
        }
    }

    // Method to load seats based on the selected showtime
    private void loadSeats(int showtimeId) {
        List<Seat> seats = seatService.getSeatsByShowtimeId(showtimeId);
        seatVBox.getChildren().clear(); // Clear the previous elements before loading new ones

        if (seats != null && !seats.isEmpty()) {
            for (Seat seat : seats) {
                CheckBox seatCheckBox = new CheckBox(seat.getSeatNumber());
                seatVBox.getChildren().add(seatCheckBox);
            }
        } else {
            showAlert("Notice", "No seats to display.");
        }
    }

    @FXML
    private void handleAddSeat() {
        // Check if a showtime has been selected
        if (selectedShowtime == null) {
            showAlert("Notice", "Please select a showtime before adding a seat.");
            return;
        }

        // Display a dialog to enter seat information
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Seat");
        dialog.setHeaderText("Enter the new seat number:");
        dialog.setContentText("Seat number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(seatNumber -> {
            // Check if the seat number is empty
            if (seatNumber.trim().isEmpty()) {
                showAlert("Notice", "Please enter a seat number.");
                return;
            }

            // Check the format of the seat number (only uppercase letters and numbers allowed)
            if (!seatNumber.matches("[A-Z0-9]+")) {
                showAlert("Notice", "Invalid seat number. Only uppercase letters and numbers are allowed.");
                return;
            }

            // Check if the seat number already exists in the selected showtime's seat list
            List<Seat> seats = seatService.getSeatsByShowtimeId(selectedShowtime.getShowtimeId());
            boolean seatExists = seats.stream().anyMatch(seat -> seat.getSeatNumber().equals(seatNumber));

            if (seatExists) {
                showAlert("Notice", "The seat number already exists for this showtime.");
                return;
            }

            // Call method to add the seat to the database
            addSeatToShowtime(selectedShowtime.getShowtimeId(), seatNumber);
        });
    }

    private void addMultipleSeats(int quantity) {
        // Distribute seats across rows A, B, C...
        char[] rows = {'A', 'B', 'C'};
        int maxSeatsPerRow = 20; // Maximum seats per row (A1, A2, ... A20)
        int totalSeatsAdded = 0;

        for (char row : rows) {
            for (int i = 1; i <= maxSeatsPerRow && totalSeatsAdded < quantity; i++) {
                String seatNumber = String.valueOf(row) + i;
                if (!isSeatExists(seatNumber)) {
                    addSeatToShowtime(selectedShowtime.getShowtimeId(), seatNumber);
                    totalSeatsAdded++;
                }
            }
            if (totalSeatsAdded >= quantity) {
                break;
            }
        }
    }

    private boolean isSeatExists(String seatNumber) {
        List<Seat> seats = seatService.getSeatsByShowtimeId(selectedShowtime.getShowtimeId());
        return seats.stream().anyMatch(seat -> seat.getSeatNumber().equals(seatNumber));
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleAddMultipleSeats() {
        // Dialog to enter the number of seats
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Multiple Seats");
        dialog.setHeaderText("Enter the number of seats (Maximum 60 seats):");
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr);
                addMultipleSeats(quantity);
            } catch (NumberFormatException e) {
                showAlert("Notice", "Please enter a valid quantity.");
            }
        });
    }

    private void addSeatToShowtime(int showtimeId, String seatNumber) {
        // Call the method in seatService to add the seat to the showtime
        boolean isAdded = seatService.addSeat(showtimeId, seatNumber); // Method needs to be implemented in seatService

        if (isAdded) {
            loadSeats(showtimeId); // Reload the seat list
        } else {
            showAlert("Notice", "An error occurred while adding the seat.");
        }
    }

    // Method to get the list of selected seats
    private List<Seat> getSelectedSeats() {
        return seatVBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> {
                    String seatNumber = ((CheckBox) node).getText(); // Get the seat number from the CheckBox
                    // Find seatId by seatNumber (assuming you have a way to do this)
                    int seatId = findSeatIdByNumber(seatNumber);
                    int showtimeId = selectedShowtime.getShowtimeId(); // Get showtimeId from the selected showtime
                    boolean booked = false; // Default to false if the seat is not booked

                    return new Seat(seatId, showtimeId, seatNumber, booked); // Create a Seat object
                })
                .collect(Collectors.toList());
    }

    // Method to find seatId by seatNumber
    private int findSeatIdByNumber(String seatNumber) {
        List<Seat> allSeats = seatService.getSeatsByShowtimeId(selectedShowtime.getShowtimeId());
        for (Seat seat : allSeats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                return seat.getSeatId(); // Return the seat ID if found
            }
        }
        return -1; // Or handle the case where it's not found
    }

    @FXML
    private void handleDeleteSelectedSeats() {
        // Get the list of selected seats
        List<Seat> selectedSeats = getSelectedSeats(); // Use the method defined earlier

        if (!selectedSeats.isEmpty()) {
            List<Integer> seatIds = selectedSeats.stream()
                    .map(Seat::getSeatId) // Get seat ID
                    .collect(Collectors.toList());

            boolean success = seatService.deleteMultipleSeats(seatIds); // Delete seats
            if (success) {
                showAlert("Notice", "Seats deleted successfully.");
                loadSeats(selectedShowtime.getShowtimeId()); // Reload the seats
            } else {
                showAlert("Error", "Unable to delete seats. Please try again.");
            }
        } else {
            showAlert("Notice", "Please select seats to delete.");
        }
    }

    // Method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);

        alert.setContentText(content);
        alert.showAndWait();
    }
}