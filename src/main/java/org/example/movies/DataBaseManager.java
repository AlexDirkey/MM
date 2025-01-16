// File: org/example/movies/DatabaseManager.java
package org.example.movies;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // SQL Server Connection Details
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=YourDatabaseName";
    private static final String USER = "yourUsername";
    private static final String PASSWORD = "yourPassword";

    /**
     * Establishes a connection to the SQL Server database.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Retrieves all movies from the database.
     */
    public static List<Movie> getAllMovies() {
        String sql = "SELECT id, title, imdbRating, personalRating, lastView FROM Movie";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
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

    /**
     * Adds a new movie to the database.
     */
    public static void addMovie(String title, double imdbRating, double personalRating, Date lastView) {
        String sql = "INSERT INTO Movie (title, imdbRating, personalRating, lastView) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setDouble(2, imdbRating);
            pstmt.setDouble(3, personalRating);
            pstmt.setDate(4, lastView);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes old movies based on the given criteria.
     */
    public static void deleteOldMovies() {
        String sql = """
                DELETE FROM Movie
                WHERE personalRating < 6 AND lastView < DATEADD(year, -2, GETDATE())
                """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println(rowsDeleted + " old movies deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for movies by title.
     */
    public static List<Movie> searchMovies(String keyword) {
        String sql = "SELECT id, title, imdbRating, personalRating, lastView FROM Movie WHERE title LIKE ?";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
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

    /**
     * Adds a category to the database.
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
    public static void assignCategoryToMovie(int categoryId, int movieId) {
        String sql = "INSERT INTO CatMovie (categoryId, movieId) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
