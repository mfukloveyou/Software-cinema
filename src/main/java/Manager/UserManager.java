package Manager;

import model.User;

import java.util.List;

public interface UserManager {
    boolean registerUser(User user);  // Đăng ký người dùng mới
    User loginUser(String username, String password);  // Đăng nhập người dùng
    List<User> getAllUsers();  // Lấy danh sách tất cả người dùng
    boolean updateUser(User user);  // Cập nhật thông tin người dùng
    boolean deleteUser(int userId);  // Xóa người dùng
    int getUserIdByUsername(String username);  // Lấy ID người dùng theo tên đăng nhập
}
