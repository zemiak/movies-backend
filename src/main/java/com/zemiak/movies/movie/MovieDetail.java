package com.zemiak.movies.movie;

import java.util.HashMap;
import java.util.Map;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

public class MovieDetail extends MovieUI  {
    public Long genreId;
    public Map<Long, String> genres;

    public Long serieId;
    public Map<Long, String> series;
    public Map<Long, Long> serieGenres;

    public Long language;
    public Long originalLanguage;
    public Long subtitlesLanguage;
    public Map<Long, String> languages;

    public String originalName;
    public String fileName;
    public String url;
    public String webPage;
    public Integer year;
    public String description;

    public static MovieDetail forNew() {
        MovieDetail dto = new MovieDetail();

        dto.genres = new HashMap<>();
        Genre.streamAll(Sort.by("displayOrder"))
            .map(genreBase -> {return (Genre) genreBase;})
            .forEach(genre -> {
                dto.genres.put(genre.id, genre.name);
            });

        dto.series = new HashMap<>();
        dto.serieGenres = new HashMap<>();
        Serie.streamAll(Sort.by("displayOrder"))
            .map(serieBase -> {return (Serie) serieBase;})
            .forEach(serie -> {
                dto.series.put(serie.id, serie.name);
                dto.serieGenres.put(serie.id, serie.genreId);
            });

        dto.languages = new HashMap<>();
        Language.streamAll(Sort.by("displayOrder"))
            .map(languageBase -> {return (Language) languageBase;})
            .forEach(language -> {
                dto.languages.put(language.id, language.name);
            });

        return dto;
    }

	public static MovieDetail of(PanacheEntity base) {
        MovieDetail dto = forNew();
        MovieUI.copy(dto, base);

        Movie entity = (Movie) base;
        dto.genreId = entity.genreId;
        dto.serieId = entity.serieId;
        dto.language = entity.language;
        dto.originalLanguage = entity.originalLanguage;
        dto.subtitlesLanguage = entity.subtitlesLanguage;
        dto.originalName = entity.originalName;
        dto.description = entity.description;
        dto.fileName = entity.fileName;
        dto.url = entity.url;
        dto.webPage = entity.webPage;
        dto.year = entity.year;

        return dto;
	}
}
