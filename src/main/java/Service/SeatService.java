package Service;

import Manager.SeatManager;
import model.Seat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatService implements SeatManager {
    private Connection connection;



    public SeatService(Connection connection) {
        this.connection = connection;
        if (connection != null) {
            System.out.println("Successfully connected to the database.");
        } else {
            System.err.println("Failed to connect to the database.");
        }
    }

    public boolean hasBookedSeats(int userId) {
        String query = "SELECT COUNT(*) FROM seats WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có ít nhất 1 ghế được đặt
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu có lỗi hoặc không có ghế
    }

    @Override
    public boolean deleteMultipleSeats(List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            return false; // No seats to delete
        }

        String sql = "DELETE FROM seats WHERE seat_id IN (" +
                seatIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < seatIds.size(); i++) {
                preparedStatement.setInt(i + 1, seatIds.get(i));
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Seat> getSeatsByShowtimeId(int showtimeId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE showtime_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, showtimeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Seat seat = new Seat(
                        resultSet.getInt("seat_id"),
                        showtimeId,
                        resultSet.getString("seat_number"),
                        resultSet.getBoolean("booked")
                );
                seats.add(seat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seats;
    }

    @Override
    public boolean resetSeatsForUser(int userId) {
        String sql = "UPDATE seats SET user_id = NULL, booked = NULL WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSeatWithUserId(int seatId, Integer userId) {
        String sql = "UPDATE seats SET user_id = ?, booked = ? WHERE seat_id = ?";

        boolean isBooked = (userId != null);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, userId, java.sql.Types.INTEGER);
            statement.setObject(2, isBooked ? 1 : null, java.sql.Types.INTEGER);
            statement.setInt(3, seatId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addSeat(int showtimeId, String seatNumber) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO seats (showtime_id, seat_number) VALUES (?, ?)")) {
            statement.setInt(1, showtimeId);
            statement.setString(2, seatNumber);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSeatStatus(int seatId, boolean isBooked) throws SQLException {
        String query = "UPDATE seats SET booked = ? WHERE seat_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, isBooked);
            statement.setInt(2, seatId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}
