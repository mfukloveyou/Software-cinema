module org.example.cinemamanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;

    exports app;
    opens app to javafx.fxml;
    exports Controller.Movie;
    opens Controller.Movie to javafx.fxml;
    exports Controller.Showtime;
    opens Controller.Showtime to javafx.fxml;
    exports Controller.Seat;
    opens Controller.Seat to javafx.fxml;
    exports Controller.Ticket;
    opens Controller.Ticket to javafx.fxml;
    exports Controller.User;
    opens Controller.User to javafx.fxml;
    opens model to javafx.controls;
    exports model to javafx.fxml;
    opens Service to javafx.fxml;
    exports Service to javafx.fxml;
    exports Manager to javafx.fxml;
    opens Manager to javafx.fxml;
}