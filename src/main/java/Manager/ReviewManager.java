package Manager;

import model.Review;
import java.sql.SQLException;
import java.util.List;

public interface ReviewManager {
    void addReview(int movieId, int userId, int rating, String comment) throws SQLException;  // Thêm review mới
    List<Review> getReviewsByMovieId(int movieId);  // Lấy danh sách review theo movieId
}
