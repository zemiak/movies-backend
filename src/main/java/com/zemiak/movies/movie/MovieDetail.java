package com.zemiak.movies.movie;

import java.util.HashMap;
import java.util.Map;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;

public class MovieDetail extends MovieUI  {
    public Long genreId;
    public Map<Long, String> genres;

    public Long serieId;
    public Map<Long, String> series;
    public Map<Long, Long> serieGenres;

    public Long languageId;
    public Long originalLanguageId;
    public Long subtitlesId;
    public Map<Long, String> languages;

    public String originalName;
    public String description;

	public static MovieDetail of(PanacheEntityBase base) {
        MovieDetail dto = new MovieDetail();
        MovieUI.copy(dto, base);

        Movie entity = (Movie) base;

        dto.genreId = entity.genreId;
        dto.genres = new HashMap<>();
        Genre.streamAll(Sort.by("displayOrder"))
            .map(genreBase -> {return (Genre) genreBase;})
            .forEach(genre -> {
                dto.genres.put(genre.id, genre.name);
            });

        dto.serieId = entity.serieId;
        dto.series = new HashMap<>();
        dto.serieGenres = new HashMap<>();
        Serie.streamAll(Sort.by("displayOrder"))
            .map(serieBase -> {return (Serie) serieBase;})
            .forEach(serie -> {
                dto.series.put(serie.id, serie.name);
                dto.serieGenres.put(serie.id, serie.genreId);
            });

        dto.languageId = entity.languageId;
        dto.originalLanguageId = entity.originalLanguageId;
        dto.subtitlesId = entity.subtitlesId;
        dto.languages = new HashMap<>();
        Language.streamAll(Sort.by("displayOrder"))
            .map(languageBase -> {return (Language) languageBase;})
            .forEach(language -> {
                dto.languages.put(language.id, language.name);
            });

        dto.originalName = entity.originalName;
        dto.description = entity.description;
        
        return dto;
	}
}
