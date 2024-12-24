package app;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Duration;

public class MainApp extends Application {

    public static void main(String[] args) {

        launch(args); // Gọi phương thức launch() của lớp Application
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Tải giao diện từ FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CinemaManagementDialog.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Cinema Management Program");
            primaryStage.setScene(new Scene(root));

            // Thêm hiệu ứng mờ
            primaryStage.getScene().getRoot().setOpacity(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), primaryStage.getScene().getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(e -> primaryStage.show());

            // Hiệu ứng mờ khi hộp thoại được mở
            primaryStage.setOnShowing(event -> {
                fadeIn.play();
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
