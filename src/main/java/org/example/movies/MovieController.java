package org.example.movies;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MovieController {
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

    @FXML
    private Button addButton; // Button for adding a movie

    @FXML
    private Button playButton; // Button for playing a movie

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private ObservableList<String> categoryList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadMovies();
        loadCategories();

        // Set up the TableView columns
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
    private void onAddMovie() {
        // Logic to add a movie
        System.out.println("Add Movie button clicked!");
        // You can implement the logic to show a dialog or form to add a new movie
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

    @FXML
    private void onPlayMovie() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an MP4 file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4 files", "*.mp4"));

        File selectedFile = fileChooser.showOpenDialog(playButton.getScene().getWindow());

        if (selectedFile != null) {
            // Valider at filen faktisk er en MP4-fil
            if (selectedFile.getName().toLowerCase().endsWith(".mp4")) {
                String fileName = selectedFile.getName();

                // Tilføj filmen til databasen, hvis den ikke allerede findes
                DatabaseManager.addMovieIfNotExists(
                        fileName,          // Titel
                        0.0,               // IMDb-rating (standardværdi)
                        0.0,               // Personlig vurdering (standardværdi)
                        LocalDate.now()    // Dagens dato som lastView
                );

                // Opdater lastView for filmen
                DatabaseManager.updateLastView(fileName, LocalDate.now());

                // Spil filmen i standard medieafspiller
                playMovie(selectedFile);

                // Opdater filmene i GUI'en
                loadMovies();
            } else {
                showError("Invalid File", "The selected file is not an MP4 file.");
            }
        } else {
            showError("No File Selected", "Please select a file to play.");
        }
    }

    private void playMovie(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                showError("Unsupported Operation", "Your system does not support opening media files.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Unable to play the selected movie.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onRateMovie() {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();

        if (selectedMovie != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedMovie.getPersonalRating()));
            dialog.setTitle("Rate Movie");
            dialog.setHeaderText("Update Personal Rating");
            dialog.setContentText("Enter a new rating (0.0 to 10.0):");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    double newRating = Double.parseDouble(input);
                    if (newRating >= 0.0 && newRating <= 10.0) {
                        // Opdater vurderingen i databasen
                        DatabaseManager.updatePersonalRating(selectedMovie.getId(), newRating);

                        // Opdater GUI'en
                        loadMovies();
                    } else {
                        showError("Invalid Rating", "Please enter a rating between 0.0 and 10.0.");
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter a valid numeric rating.");
                }
            });
        } else {
            showError("No Movie Selected", "Please select a movie to rate.");
        }
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();

        List<Movie> filteredMovies = DatabaseManager.getAllMovies().stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(keyword))
                .toList();

        movieList.setAll(filteredMovies);
    }

}
