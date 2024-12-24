package Service;

import Manager.ReviewManager;
import model.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService implements ReviewManager {
    private final Connection connection;

    public ReviewService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addReview(int movieId, int userId, int rating, String comment) throws SQLException {
        String sql = """
INSERT INTO reviews (movie_id, user_id, rating, comment) VALUES (?, ?, ?, ?)
""";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, rating);
            preparedStatement.setString(4, comment);

            preparedStatement.executeUpdate(); // Thực thi câu lệnh SQL
        }
    }

    @Override
    public List<Review> getReviewsByMovieId(int movieId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE movie_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getInt("movie_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("rating"),
                        rs.getString("comment")
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }
}
