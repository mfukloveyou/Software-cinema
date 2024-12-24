package Controller.User;

import Service.DatabaseConnection;
import Service.SeatService;
import Service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.scene.control.ListCell;

public class ManageUsersController {
    @FXML
    private ListView<User> userListView;

    private UserService userService = new UserService();
    private SeatService seatService;

    public class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setText(null);
            } else {
                setText(user.getUsername()); // Display the username
            }
        }
    }

    @FXML
    public void initialize() {
        try {
            // Khởi tạo seatService
            seatService = new SeatService(DatabaseConnection.getConnection()); // Cung cấp kết nối cơ sở dữ liệu

            // Sử dụng UserListCell để hiển thị tên người dùng
            userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
                @Override
                public ListCell<User> call(ListView<User> param) {
                    return new UserListCell();
                }
            });

            loadAllUsers(); // Nạp tất cả người dùng (nếu cần)

        } catch (SQLException e) {
            // Xử lý ngoại lệ nếu có lỗi kết nối cơ sở dữ liệu
            e.printStackTrace();
            showAlert("Error", "Failed to connect to the database: " + e.getMessage());
        }
    }




    public void loadAllUsers() {
        List<User> users = userService.getAllUsers();
        userListView.getItems().clear();
        userListView.getItems().addAll(users);
    }

    @FXML
    private void handleUpdateUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Admin/UpdateUserDialog.fxml"));
                Parent root = loader.load();

                UpdateUserDialogController controller = loader.getController();
                controller.setUser(selectedUser);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Update User Information");
                dialogStage.setScene(new Scene(root));
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(userListView.getScene().getWindow()); // No need for mainApp
                dialogStage.showAndWait();

                if (controller.isUpdated()) {
                    boolean success = userService.updateUser(selectedUser);
                    if (success) {
                        showAlert("Notification", "User information updated successfully.");
                        loadAllUsers(); // Reload the user list
                    } else {
                        showAlert("Error", "Failed to update user information.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to display the update dialog.");
            }
        } else {
            showAlert("Notification", "Please select a user to update.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        // Get the selected user from the list and delete it
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            boolean canDelete = true; // Assume user can be deleted
            // Check if user has booked seats
            if (seatService.hasBookedSeats(selectedUser.getUserId())) {
                // Reset the seats for the user (set booked and user_id to NULL)
                canDelete = seatService.resetSeatsForUser(selectedUser.getUserId());
                if (!canDelete) {
                    showAlert("Notification", "Failed to reset seats for the user.");
                    return; // Exit if reset fails
                }
            }
            // Proceed to delete user
            boolean isDeleted = userService.deleteUser(selectedUser.getUserId());
            if (isDeleted) {
                loadAllUsers(); // Reload the user list
                showAlert("Notification", "User deleted successfully!");
            } else {
                showAlert("Notification", "Failed to delete user.");
            }
        } else {
            showAlert("Notification", "Please select a user to delete.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
