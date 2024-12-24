package Service;

import Manager.TicketManager;
import model.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements TicketManager {

    private Connection connection;

    // Constructor để kết nối cơ sở dữ liệu
    public TicketService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean bookTicket(Ticket ticket) {
        String query = "INSERT INTO tickets (user_id, showtime_id, seat_id, movie_id, showtime_details, seat_number, title) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ticket.getUserId());
            statement.setInt(2, ticket.getShowtimeId());
            statement.setInt(3, ticket.getSeatId());
            statement.setInt(4, ticket.getMovieId());
            statement.setString(5, ticket.getShowtimeDetails());
            statement.setString(6, ticket.getSeatNumber());
            statement.setString(7, ticket.getTitle());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi đặt vé: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int ticketId = resultSet.getInt("ticket_id");
                int showtimeId = resultSet.getInt("showtime_id");
                int seatId = resultSet.getInt("seat_id");
                Timestamp bookedAt = resultSet.getTimestamp("booked_at");
                int movieId = resultSet.getInt("movie_id");
                String showtimeDetails = resultSet.getString("showtime_details");
                String seatNumber = resultSet.getString("seat_number");
                String title = resultSet.getString("title");

                Ticket ticket = new Ticket(ticketId, userId, showtimeId, seatId, bookedAt.toLocalDateTime(), movieId, showtimeDetails, seatNumber, title);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách vé: " + e.getMessage());
            e.printStackTrace();
        }
        return tickets;
    }

    @Override
    public boolean cancelTicket(int ticketId) {
        String deleteTicketQuery = "DELETE FROM tickets WHERE ticket_id = ?";
        String updateSeatQuery = "UPDATE seats SET booked = false WHERE seat_id = (SELECT seat_id FROM tickets WHERE ticket_id = ?)";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteTicketQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateSeatQuery)) {

            // Cập nhật trạng thái ghế trước khi xóa vé
            updateStatement.setInt(1, ticketId);
            updateStatement.executeUpdate();

            // Xóa vé
            deleteStatement.setInt(1, ticketId);
            int rowsDeleted = deleteStatement.executeUpdate();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy vé: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
