package Controller.Showtime;

import Service.ShowtimeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Movie;
import model.Showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.List;

public class AddShowtimeDialogController {

    @FXML
    private TextField movieIdField;

    @FXML
    private TextField showtimeField;

    @FXML
    private TextField availableSeatsField;
    @FXML
    private DatePicker showtimeDatePicker; // DatePicker cho ngày

    @FXML
    private TextField showtimeTimeField; // TextField cho giờ (HH:mm)

    @FXML
    private TextField titleField;

    @FXML
    private TextField showtimeDetailsField;

    private Movie movie;
    private ShowtimeService showtimeService = new ShowtimeService();
    private Stage dialogStage;

    public void setMovie(Movie movie) {
        this.movie = movie;
        movieIdField.setText(String.valueOf(movie.getId())); // Set the movie ID
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isShowtimeExist(LocalDateTime newShowtime) {
        // Sửa lại thành gọi từ showtimeService
        List<Showtime> allShowtimes = showtimeService.getAllShowTimes();

        for (Showtime showtime : allShowtimes) {
            if (showtime.getShowtime().equals(newShowtime)) {
                return true; // Nếu trùng, trả về true
            }
        }
        return false; // Nếu không trùng, trả về false
    }

    @FXML
    public void handleAddShowtime() {
        try {
            // Lấy dữ liệu từ các trường nhập liệu
            int movieId = Integer.parseInt(movieIdField.getText());
            LocalDate showtimeDate = showtimeDatePicker.getValue(); // Lấy ngày từ DatePicker
            String timeString = showtimeTimeField.getText(); // Lấy giờ từ TextField (HH:mm)
            LocalTime showtimeTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            int availableSeats = Integer.parseInt(availableSeatsField.getText());
            String title = titleField.getText();

            // Kết hợp ngày và giờ thành một LocalDateTime
            LocalDateTime showtime = LocalDateTime.of(showtimeDate, showtimeTime);

            // Kiểm tra nếu thời gian chiếu đã tồn tại
            if (isShowtimeExist(showtime)) {
                showAlert("Error", "Showtime is available!");
                return;
            }

            // Tạo đối tượng Showtime mới
            Showtime newShowtime = new Showtime(0, title, showtime, availableSeats, movieId);

            // Thêm Showtime mới vào cơ sở dữ liệu thông qua ShowtimeService
            showtimeService.addShowtime(newShowtime);

            // Hiển thị thông báo thành công
            showAlert("Alert", "Showtime is added successfully!.");
            dialogStage.close();
        } catch (Exception e) {
            showAlert("Alert", "Showtime is not available!");
            e.printStackTrace(); // In chi tiết lỗi để có thể theo dõi
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
