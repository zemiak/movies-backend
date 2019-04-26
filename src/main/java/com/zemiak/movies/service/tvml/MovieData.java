package com.zemiak.movies.service.tvml;

public class MovieData {
    private String name;
    private String path;
    private String description;
    private String year;
    private String genreName;
    private String serieName;
    private Integer displayOrder;
    private Integer id;
    private String genreKey;
    private String serieKey;

    public String getGenreKey() {
        return genreKey;
    }

    public void setGenreKey(String genreKey) {
        this.genreKey = genreKey;
    }

    public String getSerieKey() {
        return serieKey;
    }

    public void setSerieKey(String serieKey) {
        this.serieKey = serieKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getSerieName() {
        return serieName;
    }

    public void setSerieName(String serieName) {
        this.serieName = serieName;
    }
}
