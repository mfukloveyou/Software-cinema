package Controller.User;

import java.io.IOException;

import Controller.Movie.MovieController;
import Controller.Seat.SeatController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.User;
import Service.UserService;
import javafx.stage.Stage; // To use the Stage class
import javafx.scene.Parent; // To use the Parent class
import javafx.scene.Scene; // To use the Scene class
import javafx.fxml.FXMLLoader; // To use FXMLLoader
import javafx.stage.Modality; // To use Modality
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.regex.Pattern;
import java.util.Optional;

import javafx.application.Platform;

import javafx.scene.control.Dialog;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserController {
    public VBox root;
    public Pane mainPane;
    @FXML
    private ImageView logoImage;
    public StackPane contentArea;
    private UserService userService;

    private Dialog<ButtonType> dialog;
    // Constructor requires movieController

    public UserController() {
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        showLoginDialog();
    }

    @FXML
    public void handleCancel() {
        System.out.println("Login has been canceled.");
    }
    private Stage loginStage;

    // This method is to set the Stage from outside
    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }
    @FXML
    public void initialize() {
        this.userService = new UserService();
        if (logoImage != null) {
            logoImage.setImage(new Image(getClass().getResourceAsStream("/images/non-removebg-preview.png")));
        }
    }

    @FXML
    public void closeDialog() {
        if (loginStage != null) {
            loginStage.close();  // Close the window when the "Close" button is clicked
        }
    }

    public void showLoginDialog() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginDialog.fxml"));
            AnchorPane loginDialog = loader.load();

            // Create a new Stage for the login dialog
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");

            // Create Scene and set it to the Stage
            Scene scene = new Scene(loginDialog);
            loginStage.setScene(scene);

            // Get the controller from FXML
            UserController controller = loader.getController();
            controller.setLoginStage(loginStage);  // Pass Stage to the controller to close after successful login

            // Show the login dialog
            loginStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public boolean loginuser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isLoggedIn = handleLoginAction(username, password);

        // Close the dialog (Stage) if login is successful
        if (isLoggedIn) {
            loginStage.close();  // Close the Stage if login is successful
        }

        return isLoggedIn;
    }

    private boolean handleLoginAction(String username, String password) {
        User loggedInUser = userService.loginUser(username, password);

        if (loggedInUser != null) {
            System.out.println("Login successful with: " + loggedInUser.getUsername());

            // Save the user ID to static variables
            SeatController.LoggedInUser.setUserId(loggedInUser.getUserId());
            MovieController.LoggedInUser.setUserId(loggedInUser.getUserId());

            // Open the movie selection interface
            openMovieDialog();
            return true;  // Login successful
        } else {
            System.out.println("Incorrect username or password.");
            showAlert(Alert.AlertType.ERROR, "Error", "Incorrect username or password.");
            return false;  // Login failed
        }
    }

    // Method to set Dialog from outside the controller
    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    private void openMovieDialog() {
        try {
            // Create a FXMLLoader to load MovieDialog.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Movie/MovieDialog.fxml"));
            Parent root = loader.load();

            // Create a new window
            Stage movieStage = new Stage();
            movieStage.setTitle("Manage Movies");
            movieStage.setScene(new Scene(root));

            movieStage.show();
            MovieController movieController = loader.getController();
            movieController.loadMovies();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open movie interface.");
        }
    }

    // Method to show the admin login dialog
    @FXML
    private void handleStaffLogin() {
        try {
            // Load AdminDialog.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Admin/AdminDialog.fxml"));
            Parent root = loader.load();

            // Create a new Stage for the Admin login dialog
            Stage adminLoginStage = new Stage();
            adminLoginStage.setTitle("Admin Login");
            adminLoginStage.setScene(new Scene(root));
            adminLoginStage.initModality(Modality.APPLICATION_MODAL);

            // Show the dialog
            adminLoginStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load login interface.");
        }
    }

    @FXML
    public void handleRegister() throws IOException {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("User Registration");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/RegisterDialog.fxml"));
        dialog.setDialogPane(fxmlLoader.load());

        // Get references to buttons in DialogPane
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().setAll(registerButtonType, cancelButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                TextField usernameField = (TextField) dialog.getDialogPane().lookup("#usernameField");
                TextField emailField = (TextField) dialog.getDialogPane().lookup("#emailField");
                TextField phoneField = (TextField) dialog.getDialogPane().lookup("#phoneField");
                PasswordField passwordField = (PasswordField) dialog.getDialogPane().lookup("#passwordField");

                String username = usernameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String password = passwordField.getText();

                // Kiểm tra đầu vào trước khi tạo đối tượng User
                if (username == null || username.isEmpty()) {
                    showAlert("Username không được để trống");
                    return null;
                }
                if (username.length() < 4) {
                    showAlert("Username phải có ít nhất 4 ký tự");
                    return null;
                }
                if (password == null || password.isEmpty()) {
                    showAlert("Password không được để trống");
                    return null;
                }
                if (password.length() < 6) {
                    showAlert("Password phải có ít nhất 6 ký tự");
                    return null;
                }
                if (email == null || email.isEmpty()) {
                    showAlert("Email không được để trống");
                    return null;
                }
                String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                if (!Pattern.matches(emailPattern, email)) {
                    showAlert("Email không hợp lệ");
                    return null;
                }
                if (phone == null || phone.isEmpty()) {
                    showAlert("Số điện thoại không được để trống");
                    return null;
                }
                String phonePattern = "^\\d{10}$"; // Đảm bảo là 10 số
                if (!Pattern.matches(phonePattern, phone)) {
                    showAlert("Số điện thoại phải có đúng 10 chữ số");
                    return null;
                }

                // Tạo đối tượng User nếu tất cả các kiểm tra đầu vào hợp lệ
                User newUser = new User(username, password);
                newUser.setEmail(email);
                newUser.setPhone(phone);

                return newUser;
            }
            return null; // Nếu nhấn Cancel
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            boolean isRegistered = userService.registerUser(user);
            if (isRegistered) {
                System.out.println("Registration successful!");
                showAlert(Alert.AlertType.INFORMATION, "Notification", "Registration successful!");
            }
        });
    }

    // Phương thức hiển thị thông báo lỗi
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi Đầu Vào");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void handleExit() {
        // Close the application
        Platform.exit();
    }
}
