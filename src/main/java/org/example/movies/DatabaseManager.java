package org.example.movies;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlserver://10.176.111.34:1433;databaseName=MovieManager;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "CSe2024a_e_0";
    private static final String PASSWORD = "CSe2024aE0!24";

    public static Connection connect() throws SQLException {
        System.out.println("Connecting to the database...");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void setupDatabase() {
        System.out.println("Database connection initialized.");
    }

    public static List<Movie> getAllMovies() {
        String sql = "SELECT id, title, imdb_rating, personal_rating, last_view FROM Movie";
        List<Movie> movies = new ArrayList<>();
        System.out.println("Fetching all movies...");

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("imdb_rating"),
                        rs.getDouble("personal_rating"),
                        rs.getDate("last_view").toLocalDate()
                );
                movies.add(movie);
                System.out.println("Loaded movie: " + movie.getTitle());
            }
            System.out.println("Total movies loaded: " + movies.size());
        } catch (SQLException e) {
            System.err.println("Error fetching movies: " + e.getMessage());
            e.printStackTrace();
        }

        return movies;
    }

    public static List<String> getAllCategories() {
        String sql = "SELECT name FROM Categories";
        List<String> categories = new ArrayList<>();
        System.out.println("Fetching all categories...");

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("name");
                categories.add(category);
                System.out.println("Loaded category: " + category);
            }
            System.out.println("Total categories loaded: " + categories.size());
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    public static void addCategory(String categoryName) {
        String sql = "INSERT INTO Categories (name) VALUES (?)";
        System.out.println("Adding category: " + categoryName);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();
            System.out.println("Category added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void assignCategoryToMovie(int movieId, String categoryName) {
        String sql = """
                INSERT INTO CatMovie (categoryId, movieId)
                SELECT c.id, ? FROM Categories c WHERE c.name = ?
                """;
        System.out.println("Assigning category '" + categoryName + "' to movie ID " + movieId);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, categoryName);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error assigning category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Movie> getMoviesByCategory(String categoryName) {
        String sql = """
                SELECT m.id, m.title, m.imdbRating, m.personalRating, m.lastView
                FROM Movie m
                JOIN CatMovie cm ON m.id = cm.movieId
                JOIN Categories c ON cm.categoryId = c.id
                WHERE c.name = ?
                """;

        List<Movie> movies = new ArrayList<>();
        System.out.println("Fetching movies for category: " + categoryName);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("imdbRating"),
                        rs.getDouble("personalRating"),
                        rs.getDate("lastView").toLocalDate()
                );
                movies.add(movie);
                System.out.println("Loaded movie: " + movie.getTitle());
            }
            System.out.println("Total movies loaded for category: " + movies.size());
        } catch (SQLException e) {
            System.err.println("Error fetching movies by category: " + e.getMessage());
            e.printStackTrace();
        }

        return movies;
    }

    public static void addMovieIfNotExists(String title, double imdbRating, double personalRating, LocalDate lastView) {
        String sql = """
                IF NOT EXISTS (SELECT 1 FROM Movie WHERE title = ?)
                INSERT INTO Movie (title, imdbRating, personalRating, lastView)
                VALUES (?, ?, ?, ?)
                """;
        System.out.println("Adding movie if not exists: " + title);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, title);
            pstmt.setDouble(3, imdbRating);
            pstmt.setDouble(4, personalRating);
            pstmt.setDate(5, Date.valueOf(lastView));
            pstmt.executeUpdate();
            System.out.println("Movie added successfully or already exists.");
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateLastView(String title, LocalDate lastView) {
        String sql = "UPDATE Movie SET lastView = ? WHERE title = ?";
        System.out.println("Updating lastView for movie: " + title);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(lastView));
            pstmt.setString(2, title);
            pstmt.executeUpdate();
            System.out.println("lastView updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating lastView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updatePersonalRating(int movieId, double personalRating) {
        String sql = "UPDATE Movie SET personalRating = ? WHERE id = ?";
        System.out.println("Updating personal rating for movie ID: " + movieId);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, personalRating);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
            System.out.println("Personal rating updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating personal rating: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
