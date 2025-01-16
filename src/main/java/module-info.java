module org.example.movies {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.movies to javafx.fxml;
    exports org.example.movies;
}