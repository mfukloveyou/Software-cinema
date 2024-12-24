package Service;

import Manager.ShowtimeManager;
import model.Showtime;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeService implements ShowtimeManager {
    private Connection connection;

    public ShowtimeService() {
        try {
            // Sử dụng lớp DatabaseConnection để kết nối MySQL
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ShowtimeService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Showtime> getAllShowTimes() {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT showtime_id, movie_id, showtime, available_seats, title FROM showtimes";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("showtime_id");
                int movieId = resultSet.getInt("movie_id");
                LocalDateTime showtime = resultSet.getTimestamp("showtime").toLocalDateTime();
                int availableSeats = resultSet.getInt("available_seats");
                String title = resultSet.getString("title");

                Showtime showtimeObj = new Showtime(id, title, showtime, availableSeats, movieId);
                showtimes.add(showtimeObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    @Override
    public Showtime getShowtimeDetailsById(int showtimeId) {
        Showtime showtime = null;
        String query = "SELECT * FROM showtimes WHERE showtime_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, showtimeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                showtime = new Showtime();
                showtime.setShowtimeId(resultSet.getInt("showtime_id"));
                showtime.setMovieId(resultSet.getInt("movie_id"));
                showtime.setShowtime(resultSet.getTimestamp("showtime").toLocalDateTime());
                showtime.setAvailableSeats(resultSet.getInt("available_seats"));
                showtime.setTitle(resultSet.getString("title"));
                showtime.setShowtimeDetails(resultSet.getString("showtime_details"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtime;
    }

    @Override
    public List<Showtime> getShowTimesByMovieId(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT showtime_id, movie_id, showtime, available_seats, title FROM showtimes WHERE movie_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("showtime_id");
                    LocalDateTime showtime = resultSet.getTimestamp("showtime").toLocalDateTime();
                    int availableSeats = resultSet.getInt("available_seats");
                    String title = resultSet.getString("title");

                    Showtime showtimeObj = new Showtime(id, title, showtime, availableSeats, movieId);
                    showtimes.add(showtimeObj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    @Override
    public int getMovieIdByShowtimeId(int showtimeId) {
        int movieId = -1;
        String query = "SELECT movie_id FROM showtimes WHERE showtime_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                movieId = rs.getInt("movie_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movieId;
    }

    @Override
    public boolean deleteShowtime(int showtimeId) {
        String sql = "DELETE FROM showtimes WHERE showtime_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, showtimeId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addShowtime(Showtime showtime) {
        String query = "INSERT INTO showtimes (movie_id, showtime, available_seats, title, showtime_details) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, showtime.getMovieId());
            pstmt.setTimestamp(2, Timestamp.valueOf(showtime.getShowtime()));
            pstmt.setInt(3, showtime.getAvailableSeats());
            pstmt.setString(4, showtime.getTitle());
            pstmt.setString(5, showtime.getShowtimeDetails());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm suất chiếu: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getShowtimeIdByMovieAndTime(String movieTitle, LocalDateTime showtime) {
        int showtimeId = -1;
        String sql = "SELECT showtime_id FROM showtimes WHERE title = ? AND showtime = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, movieTitle);
            statement.setTimestamp(2, Timestamp.valueOf(showtime));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    showtimeId = resultSet.getInt("showtime_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimeId;
    }
}
