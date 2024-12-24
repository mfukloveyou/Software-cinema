package model;

import java.time.LocalDateTime;
public class Showtime {
    private int id;
    private String title; // Showtime title
    private LocalDateTime showtime; // Showtime date and time
    private int availableSeats; // Number of available seats
    private int movieId; // ID of the associated movie

    // Default constructor
    public Showtime(int showtimeId, int movieId, LocalDateTime showtimeTime, int availableSeats, String title, String showtimeDetails) {
        // Default constructor does not need to initialize showtime
    }

    // Constructor with all parameters
    public Showtime(int id, String title, LocalDateTime showtime, int availableSeats, int movieId) {
        this.id = id;
        this.title = title;
        this.showtime = showtime;
        this.availableSeats = availableSeats;
        this.movieId = movieId;
    }

    public Showtime() {

    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public int getShowtimeId() {
        return this.id;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getMovieId() {
        return movieId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShowtime(LocalDateTime showtime) {
        this.showtime = showtime;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    // Method to get showtime details
    public String getShowtimeDetails() {
        String showtimeStr = (showtime != null) ? showtime.toString() : "No showtimes in this film yet";
        return title + " - " + showtimeStr +
                " | Available seats: " + availableSeats;
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", showtime=" + showtime +
                ", availableSeats=" + availableSeats +
                ", movieId=" + movieId +
                '}';
    }

    public void setShowtimeId(int showtimeId) {
    }

    public void setShowtimeDetails(String showtimeDetails) {
    }
}
