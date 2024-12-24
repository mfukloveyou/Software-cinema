package Controller.Showtime;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import model.Showtime;

import java.io.IOException;

public class ShowtimeListCell extends ListCell<Showtime> {
    private VBox vBox;
    private Label titleLabel;
    private Label timeLabel;
    private Label availableSeatsLabel;

    public ShowtimeListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Showtime/ShowtimeListCell.fxml"));
            vBox = loader.load();
            titleLabel = (Label) vBox.lookup("#titleLabel");
            timeLabel = (Label) vBox.lookup("#timeLabel");
            availableSeatsLabel = (Label) vBox.lookup("#availableSeatsLabel"); // Initialize the available seats label
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Showtime showtime, boolean empty) {
        super.updateItem(showtime, empty);

        if (empty || showtime == null) {
            setGraphic(null);
        } else {
            titleLabel.setText("Movie: " + showtime.getTitle());
            timeLabel.setText("Showtime: " + showtime.getShowtime().toString());
            availableSeatsLabel.setText("Available Seats: " + showtime.getAvailableSeats()); // Update available seats
            setGraphic(vBox);
        }
    }
}
