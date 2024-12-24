package Controller.Showtime;

import Service.ShowtimeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Movie;
import model.Showtime;

import java.util.List;

public class DeleteShowtimeDialogController {

    @FXML
    private ListView<String> showtimeListView;

    private Movie movie;
    private ShowtimeService showtimeService = new ShowtimeService();
    private Stage dialogStage;
    private List<Showtime> showtimeList;

    public void setMovie(Movie movie) {
        this.movie = movie;
        loadShowtimes();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void loadShowtimes() {
        showtimeList = showtimeService.getShowTimesByMovieId(movie.getId());
        showtimeListView.getItems().clear();
        for (Showtime showtime : showtimeList) {
            showtimeListView.getItems().add("ID: " + showtime.getId() + " \n" + showtime.getShowtimeDetails());
        }
    }

    @FXML
    private void handleDeleteShowtime() {
        int selectedIndex = showtimeListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Showtime selectedShowtime = showtimeList.get(selectedIndex);
            showtimeService.deleteShowtime(selectedShowtime.getId());
            showAlert("Notification", "Showtime deleted successfully.");
            loadShowtimes(); // Update the showtime list
        } else {
            showAlert("Error", "Please select a showtime to delete.");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
