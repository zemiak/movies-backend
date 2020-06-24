package com.zemiak.movies.movie;

import java.time.LocalDateTime;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class MovieUI {
    public Long id;
    public String name;
    public Integer displayOrder;
    public String genre;
    public String serie;
    public LocalDateTime created;
    public String tvShow;
    public Integer year;
    public String language;
    public String originalLanguage;
    public String subtitles;

    public static MovieUI of(PanacheEntityBase base) {
        MovieUI dto = new MovieUI();
        MovieUI.copy(dto, base);
        return dto;
    }

    public static MovieUI copy(MovieUI dto, PanacheEntityBase base) {
        Movie entity = (Movie) base;
        dto.id = entity.id;
        dto.name = entity.name;
        dto.displayOrder = entity.displayOrder;
        dto.genre = ((Genre) Genre.findById(entity.genreId)).name;
        dto.created = entity.created;
        dto.serie = ((Serie) Serie.findById(entity.serieId)).name;
        dto.year = entity.year;
        dto.language = ((Language) Language.findById(entity.languageId)).name;
        dto.originalLanguage = ((Language) Language.findById(entity.originalLanguageId)).name;
        dto.subtitles = ((Language) Language.findById(entity.subtitlesId)).name;

        return dto;
    }
}
