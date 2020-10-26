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
    public String thumbnailUrl;
    public String seriePictureFileName;
    public String genrePictureFileName;
    public String moviePictureFileName;
    public String fileName;
    public String description;
    public String originalName;

    public static MovieUI of(PanacheEntityBase base) {
        MovieUI dto = new MovieUI();
        MovieUI.copy(dto, base);
        return dto;
    }

    public static MovieUI copy(MovieUI dto, PanacheEntityBase base) {
        Movie entity = (Movie) base;

        Genre genre = null == entity.genreId ? null : Genre.findById(entity.genreId);
        Serie serie = null == entity.serieId ? null : Serie.findById(entity.serieId);

        dto.id = entity.id;
        dto.name = entity.name;
        dto.displayOrder = entity.displayOrder;
        dto.genre = null == genre ? null : genre.name;
        dto.created = entity.created;
        dto.serie = null == serie ? null : serie.name;
        dto.year = entity.year;
        dto.language = findLanguageName(entity.languageCode);
        dto.originalLanguage =  findLanguageName(entity.originalLanguageCode);
        dto.subtitles = findLanguageName(entity.subtitlesLanguageCode);
        dto.thumbnailUrl = entity.getThumbnailUrl();
        dto.seriePictureFileName = null == serie ? null : serie.pictureFileName;
        dto.genrePictureFileName = null == genre ? null : genre.pictureFileName;
        dto.moviePictureFileName = entity.pictureFileName;
        dto.fileName = entity.fileName;
        dto.originalName = entity.originalName;
        dto.description = entity.description;

        return dto;
    }

    private static String findLanguageName(String languageCode) {
        if (null == languageCode) {
            return null;
        }

        Language entity = (Language) Language.findById(languageCode);
        if (null == entity) {
            return null;
        }

        return entity.name;
    }
}
