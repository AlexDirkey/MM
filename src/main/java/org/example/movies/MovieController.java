package org.example.movies;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class MovieController {
    public Button addButton;
    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Button addCategoryButton;

    @FXML
    private Button assignCategoryButton;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private ObservableList<String> categoryList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadMovies();
        loadCategories();

        TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Movie, Double> imdbCol = new TableColumn<>("IMDB Rating");
        imdbCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getImdbRating()).asObject());

        movieTable.getColumns().addAll(titleCol, imdbCol);
        movieTable.setItems(movieList);

        categoryComboBox.setItems(categoryList);
    }

    private void loadMovies() {
        List<Movie> movies = DatabaseManager.getAllMovies();
        movieList.setAll(movies);
    }

    private void loadCategories() {
        List<String> categories = DatabaseManager.getAllCategories();
        categoryList.setAll(categories);
    }

    @FXML
    private void onAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Add a new category");
        dialog.setContentText("Category name:");

        dialog.showAndWait().ifPresent(categoryName -> {
            DatabaseManager.addCategory(categoryName);
            loadCategories();
        });
    }

    @FXML
    private void onAssignCategory() {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        String selectedCategory = categoryComboBox.getValue();

        if (selectedMovie != null && selectedCategory != null) {
            DatabaseManager.assignCategoryToMovie(selectedMovie.getId(), selectedCategory);
        }
    }

    public void onAddMovie(ActionEvent actionEvent) {
    }
}