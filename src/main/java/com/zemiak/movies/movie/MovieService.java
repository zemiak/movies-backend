package com.zemiak.movies.movie;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@Transactional
@ApplicationScoped
public class MovieService {

    public List<Movie> all() {
        return Movie.findAll(Sort.ascending("genre", "serie", "displayOrder")).list();
    }

    public Movie find(Integer id) {
        return Serie.findById(id);
    }

    public Movie findByFilename(final String fileNameStart) {
        String fileName = removeFileSeparatorFromStartIfNeeded(fileNameStart);

        Movie movie;

        try {
            movie = Movie.findByFileName(fileName);
        } catch (NoResultException | NonUniqueResultException ex) {
            movie = null;
        }

        return movie;
    }

    public Movie create(String newFile) {
        final Movie movie = new Movie();
        final String baseFileName = new File(newFile).getName();
        final String name = baseFileName.substring(0, baseFileName.lastIndexOf("."));

        movie.setFileName(newFile);
        movie.setGenre(Genre.findById(0));
        movie.setSerie(Serie.findById(0));
        movie.setName(name);
        movie.setPictureFileName(name + ".jpg");
        movie.setDisplayOrder(0);
        movie.persist();

        return movie;
    }

    public void mergeAndSave(Movie movie) {
        movie.persist();
    }

    public void save(Movie bean, Integer genreId, Integer serieId, String languageId, String originalLanguageId, String subtitlesId) {
        bean.setGenre(Genre.findById(genreId));
        bean.setSerie(Serie.findById(serieId));
        bean.setLanguage(Language.findById(languageId));
        bean.setOriginalLanguage(Language.findById(originalLanguageId));
        bean.setSubtitles(Language.findById(subtitlesId));

        Movie target = Movie.findById(bean.getId());
        target.copyFrom(bean);
        target.persist();
    }

    public List<Movie> getNewReleases() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        List<Movie> movies = new ArrayList<>();
        Movie.streamAll(Sort.ascending("genre", "serie", "displayOrder"))
                .map(base -> (Movie) base)
                .filter((movie) -> (null != movie.getYear() && movie.getYear() >= (cal.get(Calendar.YEAR) - 3)))
                .forEach((movie) -> {
                    movies.add(movie);
                });
        Collections.sort(movies, (Movie o1, Movie o2) -> o1.getYear().compareTo(o2.getYear()) * -1);

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

        Movie.stream("serie", Sort.ascending("displayOrder"), movie.getSerie())
                .map(base -> (Movie) base)
                .peek(m -> i.inc())
                .filter(m -> m.getId().equals(movie.getId()))
                .findFirst();

        int count = i.get();
        return String.format("%0" + String.valueOf(count).length() + "d", count);
    }

    public List<Movie> getRecentlyAdded() {
        PanacheQuery<Movie> query = Movie.findAll(Sort.descending("id"));
        query.page(Page.ofSize(64));
        return query.lastPage().list();
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }
}
