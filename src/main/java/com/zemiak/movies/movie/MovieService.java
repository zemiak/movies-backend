package com.zemiak.movies.movie;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;

@Transactional
@ApplicationScoped
public class MovieService {
    @PersistenceContext
    EntityManager em;

    public List<Movie> all() {
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l ORDER BY l.genre, l.serie, l.displayOrder", Movie.class);

        return query.getResultList();
    }

    public Movie find(Integer id) {
        return em.find(Movie.class, id);
    }

    public Movie findByFilename(final String fileNameStart) {
        String fileName = removeFileSeparatorFromStartIfNeeded(fileNameStart);

        Query query = em.createNamedQuery("Movie.findByFileName");
        query.setParameter("fileName", fileName);
        Movie movie;

        try {
            movie = (Movie) query.getSingleResult();
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
        movie.setGenre(em.getReference(Genre.class, 0));
        movie.setSerie(em.getReference(Serie.class, 0));
        movie.setName(name);
        movie.setPictureFileName(name + ".jpg");
        movie.setDisplayOrder(0);
        em.persist(movie);

        return movie;
    }

    public void mergeAndSave(Movie movie) {
        em.merge(movie);
    }

    public void detach(Movie movie) {
        em.detach(movie);
    }

    public void save(Movie bean, Integer genreId, Integer serieId, String languageId, String originalLanguageId, String subtitlesId) {
        bean.setGenre(em.getReference(Genre.class, genreId));
        bean.setSerie(em.getReference(Serie.class, serieId));
        bean.setLanguage(em.getReference(Language.class, languageId));
        bean.setOriginalLanguage(em.getReference(Language.class, originalLanguageId));
        bean.setSubtitles(em.getReference(Language.class, subtitlesId));

        Movie target = em.find(Movie.class, bean.getId());
        target.copyFrom(bean);
    }

    public List<Movie> getNewReleases() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        List<Movie> movies = new ArrayList<>();
        List<Movie> all = em.createQuery("SELECT l FROM Movie l ORDER BY l.genre, l.serie, l.displayOrder", Movie.class).getResultList();
        all.stream()
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

        List<Movie> list = em.createQuery("SELECT l FROM Movie l WHERE l.serie = :serie ORDER BY l.displayOrder", Movie.class)
            .setParameter("serie", movie.getSerie()).getResultList();
        int count = list.size();

        list.stream()
                .peek(m -> i.inc())
                .filter(m -> m.getId().equals(movie.getId()))
                .findFirst();

        return String.format("%0" + String.valueOf(count).length() + "d", i.get());
    }

    public List<Movie> getRecentlyAdded() {
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l ORDER BY l.id DESC", Movie.class);
        query.setMaxResults(64);

        return query.getResultList();
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }
}
