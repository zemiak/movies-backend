package com.zemiak.movies.service.scraper;

public class SerieItemDescription {
    private String title;
    private String description;

    public SerieItemDescription(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public SerieItemDescription() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
