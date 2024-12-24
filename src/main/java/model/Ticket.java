package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Ticket {

    private int ticketId;
    private int userId;
    private int showtimeId;
    private int seatId;
    private Timestamp bookedAt;
    private int movieId;
    private String showtimeDetails;
    private String seatNumber;
    private String title;

    // Constructor
    public Ticket(int ticketId, int userId, int showtimeId, int seatId, LocalDateTime bookedAt, int movieId, String showtimeDetails, String seatNumber, String title) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.bookedAt = Timestamp.valueOf(bookedAt);
        this.movieId = movieId;
        this.showtimeDetails = showtimeDetails;
        this.seatNumber = seatNumber;
        this.title = title;
    }

    // Getters và setters cho các thuộc tính
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public Timestamp getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(Timestamp bookedAt) {
        this.bookedAt = bookedAt;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getShowtimeDetails() {
        return showtimeDetails;
    }

    public void setShowtimeDetails(String showtimeDetails) {
        this.showtimeDetails = showtimeDetails;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Phương thức toString
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", userId=" + userId +
                ", showtimeId=" + showtimeId +
                ", seatId=" + seatId +
                ", bookedAt=" + bookedAt +
                ", movieId=" + movieId +
                ", showtimeDetails='" + showtimeDetails + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
