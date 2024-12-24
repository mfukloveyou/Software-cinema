package model;

public class Seat {
    private int seatId;
    private int showtimeId;
    private String seatNumber;
    private boolean booked; // true if the seat has been placed, false if not

    public Seat(int seatId, int showtimeId, String seatNumber, boolean booked) {
        this.seatId = seatId;
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.booked = booked;
    }

    // Getters và Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
    // Override toString() method
    @Override
    public String toString() {
        return seatNumber; // Returns the number of seats to display
    }

}
