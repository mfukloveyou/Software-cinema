package Controller.Ticket;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import Service.UserService;
import Service.TicketService;
import model.Ticket;
import model.User;
import Service.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketManagementController {

    @FXML
    private ListView<String> userListView; // List of users
    @FXML
    private ListView<String> ticketListView; // List of tickets

    private final UserService userService; // UserService object
    private TicketService ticketService; // TicketService object

    // Constructor
    public TicketManagementController() {
        userService = new UserService(); // Initialize UserService
        initializeTicketService(); // Initialize TicketService
    }

    // Method to initialize TicketService
    private void initializeTicketService() {
        try {
            Connection connection = DatabaseConnection.getConnection(); // Get database connection
            ticketService = new TicketService(connection); // Initialize TicketService with connection
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Connection Error", "Unable to connect to the database."); // Show error message
        }
    }

    @FXML
    public void initialize() {
        loadUsers(); // Load user list on initialization
    }

    // Load the list of users into the ListView
    private void loadUsers() {
        List<User> users = userService.getAllUsers(); // Get all users from UserService
        List<String> usernames = new ArrayList<>(); // List to hold usernames

        for (User user : users) {
            usernames.add(user.getUsername()); // Add usernames to the list
        }

        userListView.getItems().addAll(usernames); // Add the list of usernames to ListView
    }

    @FXML
    private void handleUserSelection() {
        String selectedUsername = userListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            try {
                int userId = userService.getUserIdByUsername(selectedUsername); // Get user ID by username
                loadTicketsForUser(userId); // Call method with integer parameter
            } catch (NumberFormatException e) {
                showAlert("Error", "User ID is invalid."); // Show error message if ID cannot be converted
            }
        }
    }

    // Load tickets for the selected user by ID
    private void loadTicketsForUser(int userId) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(userId); // Get tickets by user ID
        List<String> ticketDescriptions = new ArrayList<>(); // List to hold ticket descriptions

        for (Ticket ticket : tickets) {
            // Manually format the ticket description with line breaks
            String ticketDescription = "Ticket ID: " + ticket.getTicketId() + "\n" +
                    "Showtime ID: " + ticket.getShowtimeId() + "\n" +
                    "Seat ID: " + ticket.getSeatId() + "\n" +
                    "Seat Number: " + ticket.getSeatNumber() + "\n" +
                    "Movie ID: " + ticket.getMovieId() + "\n" +
                    "Title: " + ticket.getTitle() + "\n" +
                    "Showtime Details: " + ticket.getShowtimeDetails() + "\n" +
                    "Booked At: " + ticket.getBookedAt() + "\n";

            ticketDescriptions.add(ticketDescription); // Add formatted ticket description to the list
        }

        ticketListView.getItems().clear();
        ticketListView.getItems().addAll(ticketDescriptions); // Add formatted descriptions to ListView
    }

    @FXML
    public void handleDeleteTicket() {
        String selectedTicket = ticketListView.getSelectionModel().getSelectedItem();
        if (selectedTicket != null) {
            // Confirm ticket deletion
            boolean confirmation = showConfirmationDialog("Confirm", "Are you sure you want to delete this ticket?");
            if (confirmation) {
                try {
                    // Log the selected ticket info
                    System.out.println("Attempting to delete ticket: " + selectedTicket);

                    int ticketId = extractTicketId(selectedTicket); // Extract ticket ID
                    System.out.println("Extracted ticket ID: " + ticketId);

                    // Delete the ticket
                    ticketService.cancelTicket(ticketId); // Cancel the ticket
                    System.out.println("Ticket successfully deleted with ID: " + ticketId);

                    loadTicketsForUser(getSelectedUserId()); // Reload tickets for the user
                    System.out.println("Reloaded tickets for the user.");

                } catch (NumberFormatException e) {
                    // Log error if there is an issue extracting the ticket ID
                    System.out.println("Error extracting ticket ID: " + e.getMessage());
                    showAlert("Error", "Ticket ID is invalid."); // Show error message
                } catch (Exception e) {
                    // Log any other errors that occur during ticket deletion
                    System.out.println("An error occurred while deleting the ticket: " + e.getMessage());
                    showAlert("Error", "Unable to delete ticket. Please try again.");
                }
            } else {
                System.out.println("User canceled the ticket deletion.");
            }
        } else {
            // Log if no ticket is selected for deletion
            System.out.println("No ticket selected for deletion.");
            showAlert("Notification", "Please select a ticket to delete.");
        }
    }

    // Method to extract ticket ID from the description string
    private int extractTicketId(String selectedTicket) {
        // Log the selectedTicket string for debugging purposes
        System.out.println("Selected ticket string: " + selectedTicket);

        // Try to find the ticketId pattern in the string. Since the ID is numeric and follows "D: ", we can look for numbers after it.
        String regex = "D: (\\d+)";  // This will capture the numbers after "D: "
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(selectedTicket);

        if (matcher.find()) {
            // If a match is found, extract the ticket ID
            String ticketIdString = matcher.group(1); // The number after "D: "
            try {
                return Integer.parseInt(ticketIdString); // Convert to integer
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ticket ID format: " + ticketIdString, e);
            }
        } else {
            throw new IllegalArgumentException("Ticket ID not found in the string: " + selectedTicket);
        }
    }



    // Get the ID of the currently selected user
    private int getSelectedUserId() {
        String selectedUsername = userListView.getSelectionModel().getSelectedItem();
        return userService.getUserIdByUsername(selectedUsername); // Get user ID from the username
    }

    @FXML
    public void handleCloseAction() {
        Stage stage = (Stage) userListView.getScene().getWindow();
        stage.close(); // Close the window
    }

    // Show confirmation dialog
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Show informational alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
