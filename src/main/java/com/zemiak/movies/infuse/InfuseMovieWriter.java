package com.zemiak.movies.infuse;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.zemiak.movies.batch.RefreshStatistics;
import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.movie.MovieService;

@Dependent
public class InfuseMovieWriter {
    private static final BatchLogger LOG = BatchLogger.getLogger(InfuseMovieWriter.class.getName());

    @Inject MovieService service;
    private final String path = ConfigurationProvider.getPath();
    @Inject RefreshStatistics stats;
    @Inject InfuseCoversAndLinks metadataFiles;
    @PersistenceContext EntityManager em;

    public void process(final List<String> list) {
        list.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie)
                .forEach(this::makeMovieLinkNoException);

        makeRecentlyAdded();
        makeNewReleases();

        metadataFiles.createGenreAndSerieCovers();
    }

    private void makeMovieLinkNoException(Movie movie) {
        try {
            makeMovieLink(movie);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot make movie link for " + movie.fileName + ": " + ex.getMessage(), null);
        }
    }

    private void makeMovieLink(Movie movie) throws IOException {
        if (null == movie.genre) {
            LOG.log(Level.SEVERE, "Movie {0} has no genre", movie.fileName);
            return;
        }

        String movieName = getNumberPrefix(movie) +
                ((null == movie.originalName || "".equals(movie.originalName.trim()))
                ? movie.name : movie.originalName);
        if (null == movieName || "".equals(movieName)) {
            LOG.log(Level.SEVERE, "Movie {0} has no name", movie.fileName);
            return;
        }

        int i = 0;
        while (!metadataFiles.createLink(movie, movieName, i)) {
            i++;
        }

        LOG.log(Level.FINE, "Created Infuse movie link for movie ", movie.fileName);
    }

    private void makeRecentlyAdded() {
        Genre genre = Genre.create();
        genre.id = -1l;
        genre.name = "X-Recently Added";

        service.getRecentlyAdded().stream().forEach(movie -> {
            em.detach(movie);
            movie.genre = genre;
            movie.serie = null;
            makeMovieLinkNoException(movie);
        });
    }

    private void makeNewReleases() {
        Genre genre = Genre.create();
        genre.id = -2l;
        genre.name = "X-New Releases";

        service.getNewReleases().stream().forEach(movie -> {
            em.detach(movie);
            movie.genre = genre;
            movie.serie = null;
            makeMovieLinkNoException(movie);
        });
    }

    private String getNumberPrefix(Movie movie) {
        if ((null == movie.serie || movie.serie.isEmpty()) && Objects.nonNull(movie.year) && movie.year > 1800) {
            return String.format("%03d", (2500 - movie.year)) + "-";
        }

        if (null == movie.displayOrder || movie.displayOrder.equals(0) || movie.displayOrder > 999) {
            return "";
        }

        return String.valueOf(movie.displayOrder);
    }
}
