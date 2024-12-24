package Controller.Movie;

import Controller.Ticket.TicketController;
import Controller.User.UserController;
import Service.DatabaseConnection;
import Service.MovieService;
import Service.ShowtimeService;
import Service.TicketService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Movie;
import model.Ticket;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieController {
    // Variable to store the userId of the logged-in user

    // Method to set userId after successful login
    public class LoggedInUser {
        private static int userId;
        public static int movieId; // Added movieId variable
        public static int getUserId() {
            return userId;
        }

        public static void setUserId(int userId) {
            MovieController.LoggedInUser.userId = userId;
        }

        public static int getMovieId() {
            return movieId;
        }

        public static void setMovieId(int movieId) {
            MovieController.LoggedInUser.movieId = movieId;
        }
    }

    private UserController userController; // Declare UserController
    private ShowtimeService showtimeService; // Declare ShowtimeService
    private MovieService movieService; // Declare MovieService
    private TicketService ticketService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    public MovieController() {
        try {
            // Initialize the TicketService object with the database connection
            this.ticketService = new TicketService(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            // Catch and handle SQL errors if there is an issue with database connection
            System.err.println("Error initializing TicketService: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Catch and handle other errors
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private ListView<Movie> movieListView; // ListView to display the list of movies
    @FXML
    private Button viewBookedTicketsButton;
    @FXML
    private Button exitButton;
    @FXML
    private GridPane movieGrid; // Declare movieGrid variable

    private Stage dialogStage; // Dialog to display
    private Movie movie; // Current Movie object
    private boolean saveClicked = false; // Check save status

    @FXML
    public void initialize() {
        // Initialize movieService
        movieService = new MovieService();
        // Initialize showtimeService
        showtimeService = new ShowtimeService(); // Added this line

        // Set event for "View Booked Tickets" button
        viewBookedTicketsButton.setOnAction(event -> showBookedTickets());
        // Set event for "Exit" button
        exitButton.setOnAction(event -> closeMovieDialog());
        loadMovies();
    }

    public void loadMovies() {
        movieListView.getItems().clear();
        List<Movie> movies = movieService.getAllMovies(); // Get the list of movies

        movieListView.setCellFactory(param -> new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                if (empty || movie == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Load FXML file for each movie item
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/MovieItem.fxml"));
                        Parent movieItemNode = loader.load();

                        // Get controller and set movie data
                        MovieItemController controller = loader.getController();
                        controller.setMovie(movie);

                        setGraphic(movieItemNode); // Display movie UI
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        movieListView.getItems().addAll(movies); // Add Movie objects to the ListView
    }

    public void showBookedTickets() {
        if (LoggedInUser.getUserId() != 0) {
            // Get the list of booked tickets for the logged-in user
            List<Ticket> tickets = getTicketsByUserId(LoggedInUser.getUserId());

            if (!tickets.isEmpty()) {
                // Hiển thị thông tin về từng vé
                List<String> ticketInfoList = new ArrayList<>();
                for (Ticket ticket : tickets) {
                    String ticketInfo = "Ticket ID: " + ticket.getTicketId() +
                            ", Movie: " + ticket.getTitle() +
                            ", Showtime: " + ticket.getShowtimeDetails() +
                            ", Seat: " + ticket.getSeatNumber() +
                            ", Booked at: " + ticket.getBookedAt();
                    ticketInfoList.add(ticketInfo);
                }
                showTicketDialog(ticketInfoList);
            } else {
                // Hiển thị thông báo nếu không có vé
                showAlert("No Tickets Found", "You have not booked any tickets.");
            }
        } else {
            showAlert("Not Logged In", "You must log in to view booked tickets.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Có thể thay bằng ERROR nếu cần
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showTicketDialog(List<String> ticketStrings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Ticket/BookedTicketsDialog.fxml"));
            Parent root = loader.load();

            TicketController ticketController = loader.getController();

            // Convert List<String> to List<Ticket>
            List<Ticket> tickets = new ArrayList<>();
            for (String ticketString : ticketStrings) {
                Ticket ticket = convertStringToTicket(ticketString);
                tickets.add(ticket);
            }

            ticketController.initialize(tickets); // Pass the ticket list to the dialog controller

            Stage stage = new Stage();
            stage.setTitle("List of Booked Tickets");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            stage.showAndWait(); // Show dialog and wait for close
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Ticket convertStringToTicket(String ticketString) {
        try {
            String[] parts = ticketString.split(", | \\| ");
            int ticketId = 0;
            String title = "";
            String showtimeDetails = "";
            String seatNumber = "";
            LocalDateTime bookedAt = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

            for (String part : parts) {
                if (part.startsWith("Ticket ID: ")) {
                    ticketId = Integer.parseInt(part.split(": ")[1].trim());
                } else if (part.startsWith("Movie: ")) {
                    title = part.split(": ")[1].trim();
                } else if (part.startsWith("Showtime: ")) {
                    showtimeDetails = part.split(": ")[1].trim();
                } else if (part.startsWith("Seat: ")) {
                    seatNumber = part.split(": ")[1].trim();
                } else if (part.startsWith("Booked at: ")) {
                    String bookedAtString = part.split(": ")[1].trim();
                    bookedAt = LocalDateTime.parse(bookedAtString, formatter);
                }
            }

            return new Ticket(ticketId, 0, 0, 0, bookedAt, 0, showtimeDetails, seatNumber, title);
        } catch (Exception e) {
            System.err.println("Error parsing ticket: " + e.getMessage());
        }
        return null;
    }

    private List<Ticket> getTicketsByUserId(int userId) {
        // Call method from TicketService
        return ticketService.getTicketsByUserId(userId);
    }

    private void closeMovieDialog() {
        Stage stage = (Stage) movieListView.getScene().getWindow();
        stage.close();
    }

    // Set the Dialog Stage
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
