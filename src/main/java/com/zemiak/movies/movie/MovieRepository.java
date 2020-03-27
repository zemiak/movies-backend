package com.zemiak.movies.movie;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.genre.GenreRepository;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.language.LanguageRepository;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.serie.SerieRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<Movie> {
    @Inject
    GenreRepository genreRepo;

    @Inject
    SerieRepository serieRepo;

    @Inject
    LanguageRepository langRepo;

    public String composeGenreName(Movie entity) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(entity.genreId);
        return genreRepo.findById(entity.genreId).name;
    }

    public String getSerieName(Movie movie) {
        Serie serieEntity = serieRepo.findById(movie.serieId);
        return null == serieEntity ? "None" : (serieEntity.isEmpty() ? "None" : serieEntity.name);
    }

    public String getGenreName(Movie movie) {
        Genre genreEntity = genreRepo.findById(movie.genreId);
        return null == genreEntity ? "None" : (genreEntity.isEmpty() ? "None" : genreEntity.name);
    }

    private String getLanguageNameId(Movie movie, Long languageId) {
        Language languageEntity = langRepo.findById(languageId);
        return null == languageEntity ? "None" : languageEntity.name;
    }

    public String getLanguageName(Movie movie) {
        return getLanguageNameId(movie, movie.languageId);
    }

    public String getOriginalLanguageName(Movie movie) {
        return getLanguageNameId(movie, movie.originalLanguageId);
    }

    public String getSubtitlesLanguageName(Movie movie) {
        return getLanguageNameId(movie, movie.subtitlesId);
    }

    public String getGenrePictureFileName(Movie movie) {
        Genre genreEntity = genreRepo.findById(movie.genreId);
        return null == genreEntity ? "null.jpg" : genreEntity.pictureFileName;
    }
}
