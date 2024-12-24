package Controller.User;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class UpdateUserDialogController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField passwordField;

    private User user;
    private boolean isUpdated = false;

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhoneNumber());
            passwordField.setText(user.getPassword());
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    @FXML
    private void handleUpdate() {
        if (user != null) {
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setPhone(phoneField.getText());
            user.setPassword(passwordField.getText());
            isUpdated = true;
            closeDialog();
        }
    }

    @FXML
    private void handleCancel() {
        isUpdated = false;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
