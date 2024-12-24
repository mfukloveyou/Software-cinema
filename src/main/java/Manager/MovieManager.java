package Manager;

import model.Movie;
import java.util.List;

public interface MovieManager {
    boolean addMovie(Movie movie);  // Thêm phim mới
    List<Movie> getAllMovies();     // Lấy danh sách tất cả các phim
    boolean deleteMovie(int movieId);  // Xóa phim theo ID
}
