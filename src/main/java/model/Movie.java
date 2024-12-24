package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
public class Movie {
    private int id;
    private String title;
    private String description;
    private int duration;
    private LocalDate releaseDate;
    private String genre;
    private String imagePath;
    private List<Showtime> showtimes;
// Constructor với tất cả các tham số
    public Movie(int id, String title, String imagePath, String description, String genre, int duration, LocalDate releaseDate) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Movie duration must be greater than 0.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be blank.");
        }
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.showtimes = new ArrayList<>();
    }

    // Constructor đơn giản
    public Movie(String title, String imagePath, String description, String genre, int duration, LocalDate releaseDate) {
        this(0, title, imagePath, description, genre, duration, releaseDate);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id; // Setter cho id
    }
    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
// Setters
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be blank.");
        }
        this.title = title;
    }
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
