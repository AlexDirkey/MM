// File: org/example/movies/Movie.java
package org.example.movies;

public class Movie {
    private String title;
    private double imdbRating;

    public Movie(String title, double imdbRating) {
        this.title = title;
        this.imdbRating = imdbRating;
    }

    public String getTitle() {
        return title;
    }

    public double getImdbRating() {
        return imdbRating;
    }
}
