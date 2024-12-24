package Manager;

import model.Seat;
import java.sql.SQLException;
import java.util.List;

public interface SeatManager {
    boolean addSeat(int showtimeId, String seatNumber);  // Thêm ghế mới
    boolean deleteMultipleSeats(List<Integer> seatIds);  // Xóa nhiều ghế theo ID
    List<Seat> getSeatsByShowtimeId(int showtimeId);  // Lấy ghế theo showtimeId
    boolean resetSeatsForUser(int userId);  // Đặt lại ghế cho người dùng
    boolean updateSeatWithUserId(int seatId, Integer userId);  // Cập nhật ghế với userId
    boolean updateSeatStatus(int seatId, boolean isBooked) throws SQLException;  // Cập nhật trạng thái ghế khi đặt
}
