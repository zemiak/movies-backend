package com.zemiak.movies.serie;

import java.time.LocalDateTime;

import com.zemiak.movies.genre.Genre;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class SerieUI {
    public Long id;
    public String name;
    public Integer displayOrder;
    public String genre;
    public LocalDateTime created;
    public String tvShow;

    public static SerieUI of(PanacheEntityBase base) {
        Serie entity = (Serie) base;
        SerieUI dto = new SerieUI();
        dto.id = entity.id;
        dto.name = entity.name;
        dto.displayOrder = entity.displayOrder;
        dto.genre = ((Genre) Genre.findById(entity.genreId)).name;
        dto.created = entity.created;
        dto.tvShow = entity.tvShow ? "Yes" : "";

        return dto;
    }
}
