package com.zemiak.movies.ui;

public class GuiDTO {
    public String title;
    public String type;
    public String url;
    public String thumbnail;
    public Long id;

    public GuiDTO() {

    }

    public GuiDTO(String type, String title, String url, String thumbnail, Long id) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.thumbnail = thumbnail;
        this.id = id;
    }
}
