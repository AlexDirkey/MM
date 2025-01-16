// File: org/example/movies/MovieApplication.java
package org.example.movies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MovieApplication extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseManager.setupDatabase();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("movie-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setTitle("Movie Collection");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
