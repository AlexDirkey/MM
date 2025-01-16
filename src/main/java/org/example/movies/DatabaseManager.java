// File: org/example/movies/DatabaseManager.java
package org.example.movies;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=YourDatabaseName";
    private static final String USER = "yourUsername";
    private static final String PASSWORD = "yourPassword";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Retrieves all categories from the database.
     */
    public static List<String> getAllCategories() {
        String sql = "SELECT name FROM Categories";
        List<String> categories = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Adds a new category to the database.
     */
    public static void addCategory(String categoryName) {
        String sql = "INSERT INTO Categories (name) VALUES (?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assigns a category to a movie.
     */
    public static void assignCategoryToMovie(int movieId, String categoryName) {
        String sql = """
                INSERT INTO CatMovie (categoryId, movieId)
                SELECT c.id, ? FROM Categories c WHERE c.name = ?
                """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, categoryName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves movies for a specific category.
     */
    public static List<Movie> getMoviesByCategory(String categoryName) {
        String sql = """
                SELECT m.id, m.title, m.imdbRating, m.personalRating, m.lastView
                FROM Movie m
                JOIN CatMovie cm ON m.id = cm.movieId
                JOIN Categories c ON cm.categoryId = c.id
                WHERE c.name = ?
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("imdbRating"),
                        rs.getDouble("personalRating"),
                        rs.getDate("lastView").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public static void setupDatabase() {
    }

    public static List<Movie> getAllMovies() {
        return List.of();
    }
    }