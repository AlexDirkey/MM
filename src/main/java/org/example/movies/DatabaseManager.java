package org.example.movies;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=MovieManager";
    private static final String USER = "CSe2024a_e_0 ";
    private static final String PASSWORD = "CSe2024aE0!24";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void setupDatabase() {
        System.out.println("Database connection initialized.");
    }

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

    public static void addCategory(String categoryName) {
        String sql = "INSERT INTO Categories (name) VALUES (?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void addMovieIfNotExists(String title, double imdbRating, double personalRating, LocalDate lastView) {
        String sql = """
                IF NOT EXISTS (SELECT 1 FROM Movie WHERE title = ?)
                INSERT INTO Movie (title, imdbRating, personalRating, lastView)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, title);
            pstmt.setDouble(3, imdbRating);
            pstmt.setDouble(4, personalRating);
            pstmt.setDate(5, Date.valueOf(lastView));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLastView(String title, LocalDate lastView) {
        String sql = "UPDATE Movie SET lastView = ? WHERE title = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(lastView));
            pstmt.setString(2, title);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
