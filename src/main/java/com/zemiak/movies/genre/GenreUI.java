package com.zemiak.movies.genre;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class GenreUI {
    public Long id;
    public String name;
    public Long displayOrder;
    public LocalDateTime created;
    public String protectedGenre;

    public static GenreUI of(PanacheEntityBase base) {
        Genre entity = (Genre) base;
        GenreUI dto = new GenreUI();
        dto.id = entity.id;
        dto.name = entity.name;
        dto.displayOrder = entity.displayOrder;
        dto.created = entity.created;
        dto.protectedGenre = entity.protectedGenre != 0l ? "Yes" : "";

        return dto;
    }
}
