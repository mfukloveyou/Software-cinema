package Manager;

import model.Showtime;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeManager {
    List<Showtime> getAllShowTimes();  // Lấy tất cả suất chiếu
    Showtime getShowtimeDetailsById(int showtimeId);  // Lấy thông tin suất chiếu theo showtimeId
    List<Showtime> getShowTimesByMovieId(int movieId);  // Lấy suất chiếu theo movieId
    int getMovieIdByShowtimeId(int showtimeId);  // Lấy movieId theo showtimeId
    boolean deleteShowtime(int showtimeId);  // Xóa suất chiếu theo showtimeId
    boolean addShowtime(Showtime showtime);  // Thêm suất chiếu mới
    int getShowtimeIdByMovieAndTime(String movieTitle, LocalDateTime showtime);  // Lấy showtimeId theo title và thời gian
}
