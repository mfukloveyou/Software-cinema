package Controller.Ticket;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import model.Ticket;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TicketListCell extends ListCell<Ticket> {
    private VBox vBox;
    private Label ticketIdLabel = new Label();
    private Label titleLabel = new Label();
    private Label showtimeLabel = new Label();
    private Label seatLabel = new Label();
    private Label bookedAtLabel = new Label();

    // Constructor
    public TicketListCell() {
        try {
            // Load the FXML layout from the file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Ticket/TicketListCell.fxml"));
            vBox = loader.load();
            // You may not need to set the Labels from FXML anymore since they will be created in updateItem
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the content for each item in the ListCell
    @Override
    protected void updateItem(Ticket ticket, boolean empty) {
        super.updateItem(ticket, empty);

        // If the item is empty or the ticket is null, clear the graphic
        if (empty || ticket == null) {
            setGraphic(null);
        } else {
            // Update the Labels with ticket information
            ticketIdLabel.setText("Ticket ID: " + ticket.getTicketId());
            titleLabel.setText("Movie: " + ticket.getTitle());
            showtimeLabel.setText("Showtime: " + ticket.getShowtimeDetails());
            seatLabel.setText("Seat: " + ticket.getSeatNumber());

            // Convert Timestamp to LocalDateTime and format it
            String bookedAtFormatted = ticket.getBookedAt().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bookedAtLabel.setText("Booked at: " + bookedAtFormatted);

            // Create the layout for the ListCell
            vBox.getChildren().clear(); // Clear old content
            vBox.getChildren().addAll(ticketIdLabel, titleLabel, showtimeLabel, seatLabel, bookedAtLabel);
            setGraphic(vBox); // Set VBox as the graphic for the ListCell
        }
    }
}
