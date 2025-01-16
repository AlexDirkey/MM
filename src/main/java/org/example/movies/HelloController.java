// File: org/example/movies/MovieController.java
package org.example.movies;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MovieController {
    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private TextField searchField;

    @FXML
    private Button addButton;

    @FXML
    private void initialize() {
        TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, Double> imdbCol = new TableColumn<>("IMDB Rating");
        imdbCol.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));

        movieTable.getColumns().addAll(titleCol, imdbCol);
    }

    @FXML
    private void onAddMovie() {
        // Handle adding movie logic
    }

    @FXML
    private void onSearchMovie() {
        // Handle searching logic
    }
}
