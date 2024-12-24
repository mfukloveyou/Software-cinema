package Service;

import Manager.MovieManager;
import model.Movie;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieService implements MovieManager {

    private Connection connection;

    public MovieService() {
        try {
            // Sử dụng lớp DatabaseConnection để kết nối với MySQL
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addMovie(Movie movie) {
        // Kiểm tra hợp lệ
        System.out.println("Adding movie: " + movie.getTitle());
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty.");
        }
        String query = "INSERT INTO movies (title, description, duration, release_date, genre, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getDescription());
            pstmt.setInt(3, movie.getDuration());
            pstmt.setDate(4, Date.valueOf(movie.getReleaseDate()));
            pstmt.setString(5, movie.getGenre());
            pstmt.setString(6, movie.getImagePath());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0; // Trả về true nếu thành công
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
        }
        return false; // Trả về false nếu có lỗi
    }
    @Override
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("movie_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int duration = rs.getInt("duration");
                LocalDate releaseDate = rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null;
                String genre = rs.getString("genre");
                String imagePath = rs.getString("image_path");

                Movie movie = new Movie(id, title, imagePath, description, genre, duration, releaseDate);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Movies list in MovieService: " + movies);
        return movies;
    }

    @Override
    public boolean deleteMovie(int movieId) {
        String query = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, movieId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
