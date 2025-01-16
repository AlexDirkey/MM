package org.example.movies;

import java.time.LocalDate;

public class Movie {
    private int id;
    private String title;
    private double imdbRating;
    private double personalRating;
    private LocalDate lastView;

    public Movie(int id, String title, double imdbRating, double personalRating, LocalDate lastView) {
        this.id = id;
        this.title = title;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.lastView = lastView;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public double getPersonalRating() {
        return personalRating;
    }

    public LocalDate getLastView() {
        return lastView;
    }
}