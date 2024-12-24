package model;

public class Review {
    private int reviewId;
    private int movieId;
    private int userId;
    private double rating;
    private String comment;

    public Review(int reviewId, int movieId, int userId, double rating, String comment) {
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }


    public String getComment() {
        return comment;
    }


}
