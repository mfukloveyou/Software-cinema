package Service;

import Manager.UserManager;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;

public class UserService implements UserManager {

    private static final String SELECT_ALL_USERS = "SELECT * FROM users";

    @Override
    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        int userId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    @Override
    public User loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        User user = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean registerUser(User user) {
        // Biểu thức chính quy cho username, email và phone
        String usernamePattern = "^[a-zA-Z0-9]{4,}$"; // Username có ít nhất 4 ký tự (chỉ chữ và số)
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"; // Email có dạng example@domain.com
        String phonePattern = "^\\d{10}$"; // Phone có đúng 10 chữ số

        if (user.getUsername().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username is required.");
            return false;
        }

        if (user.getEmail().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email is required.");
            return false;
        }

        if (user.getPhoneNumber().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Phone number is required.");
            return false;
        }

        if (user.getPassword().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Password is required.");
            return false;
        }

        if (!Pattern.matches(usernamePattern, user.getUsername())) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username must be at least 4 characters and contain only letters and numbers.");
            return false;
        }

        if (!Pattern.matches(emailPattern, user.getEmail())) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email is not valid.");
            return false;
        }

        if (!Pattern.matches(phonePattern, user.getPhoneNumber())) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Phone number must be exactly 10 digits.");
            return false;
        }

        String checkQuery = "SELECT * FROM users WHERE username = ? OR email = ? OR phone = ?";
        String insertUserQuery = "INSERT INTO users (username, password, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {

            checkStmt.setString(1, user.getUsername());
            checkStmt.setString(2, user.getEmail());
            checkStmt.setString(3, user.getPhoneNumber());

            ResultSet resultSet = checkStmt.executeQuery();
            boolean usernameExists = false, emailExists = false, phoneExists = false;

            if (resultSet.next()) {
                if (resultSet.getString("username").equals(user.getUsername())) {
                    usernameExists = true;
                }
                if (resultSet.getString("email").equals(user.getEmail())) {
                    emailExists = true;
                }
                if (resultSet.getString("phone").equals(user.getPhoneNumber())) {
                    phoneExists = true;
                }
            }

            if (usernameExists) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
                return false;
            }
            if (emailExists) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email already exists.");
                return false;
            }
            if (phoneExists) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Phone number already exists.");
                return false;
            }

            insertStmt.setString(1, user.getUsername());
            insertStmt.setString(2, user.getPassword());
            insertStmt.setString(3, user.getEmail());
            insertStmt.setString(4, user.getPhoneNumber());
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(SELECT_ALL_USERS)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, phone = ?, password = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getPassword());
            statement.setInt(5, user.getUserId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
