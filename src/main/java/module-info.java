module org.example.movies {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens org.example.movies to javafx.fxml;
    exports org.example.movies;
}