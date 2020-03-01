package com.zemiak.movies.movie;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@Transactional
@ApplicationScoped
public class MovieService {
    public List<Movie> all() {
        return Movie.findAll(Sort.ascending("genre", "serie", "displayOrder")).list();
    }

    public Movie find(Long id) {
        return Movie.findById(id);
    }

    public Movie findByFilename(final String fileNameStart) {
        String fileName = removeFileSeparatorFromStartIfNeeded(fileNameStart);

        List<Movie> list = Movie.find("fileName", fileName).list();
        return (list.isEmpty() || list.size() > 1) ? null : list.get(0);
    }

    public Movie create(String newFile) {
        final Movie movie = new Movie();
        final String baseFileName = new File(newFile).getName();
        final String name = baseFileName.substring(0, baseFileName.lastIndexOf("."));

        movie.fileName = newFile;
        movie.genre = Genre.findById(0l);
        movie.serie = Serie.findById(0l);
        movie.name = name;
        movie.pictureFileName = name + ".jpg";
        movie.displayOrder = 0;
        movie.persist();

        return movie;
    }

    public void mergeAndSave(Movie movie) {
        movie.persist();
    }

    public void save(Movie bean, Long genreId, Long serieId, Long languageId, Long originalLanguageId, Long subtitlesId) {
        bean.genre = Genre.findById(genreId);
        bean.serie = Serie.findById(serieId);
        bean.language = Language.findById(languageId);
        bean.originalLanguage = Language.findById(originalLanguageId);
        bean.subtitles = Language.findById(subtitlesId);
        bean.persist();
    }

    public List<Movie> getNewReleases() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        List<Movie> movies = new ArrayList<>();
        // TODO: limit to 50 results
        Movie.findAll(Sort.ascending("genre", "serie", "displayOrder")).stream().map(e -> (Movie) e)
                .filter((movie) -> (null != movie.year && movie.year >= (cal.get(Calendar.YEAR) - 3)))
                .forEach((movie) -> {
                    movies.add(movie);
                });
        Collections.sort(movies, (Movie o1, Movie o2) -> o1.year.compareTo(o2.year) * -1);

        return movies;
    }

    private class Counter {
        private Integer i = 0;

        public Integer get() {
            return i;
        }

        public void inc() {
            this.i++;
        }
    }

    public String getNiceDisplayOrder(Movie movie) {
        final Counter i = new Counter();

        PanacheQuery<Movie> q = Movie.find("serie", Sort.ascending("displayOrder"), movie.serie);
        long count = q.count();

        q.list().stream().map(e -> (Movie) e)
                .peek(m -> i.inc())
                .filter(m -> m.id.equals(movie.id))
                .findFirst();

        return String.format("%0" + String.valueOf(count).length() + "d", i.get());
    }

    public List<Movie> getRecentlyAdded() {
        return Movie.findAll(Sort.descending("id")).page(0, 64).list();
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }
}
