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
import com.zemiak.movies.lookup.ConfigurationProvider;
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
            LOG.log(Level.SEVERE, "Cannot make movie link for " + movie.getFileName() + ": " + ex.getMessage(), null);
        }
    }

    private void makeMovieLink(Movie movie) throws IOException {
        if (null == movie.getGenre()) {
            LOG.log(Level.SEVERE, "Movie {0} has no genre", movie.getFileName());
            return;
        }

        String movieName = getNumberPrefix(movie) +
                ((null == movie.getOriginalName() || "".equals(movie.getOriginalName().trim()))
                ? movie.getName() : movie.getOriginalName());
        if (null == movieName || "".equals(movieName)) {
            LOG.log(Level.SEVERE, "Movie {0} has no name", movie.getFileName());
            return;
        }

        int i = 0;
        while (!metadataFiles.createLink(movie, movieName, i)) {
            i++;
        }

        LOG.log(Level.FINE, "Created Infuse movie link for movie ", movie.getFileName());
    }

    private void makeRecentlyAdded() {
        Genre genre = Genre.create();
        genre.setId(-1);
        genre.setName("X-Recently Added");

        service.getRecentlyAdded().stream().forEach(movie -> {
            em.detach(movie);
            movie.setGenre(genre);
            movie.setSerie(null);
            makeMovieLinkNoException(movie);
        });
    }

    private void makeNewReleases() {
        Genre genre = Genre.create();
        genre.setId(-2);
        genre.setName("X-New Releases");

        service.getNewReleases().stream().forEach(movie -> {
            em.detach(movie);
            movie.setGenre(genre);
            movie.setSerie(null);
            makeMovieLinkNoException(movie);
        });
    }

    private String getNumberPrefix(Movie movie) {
        if ((null == movie.getSerie() || movie.getSerie().isEmpty()) && Objects.nonNull(movie.getYear()) && movie.getYear() > 1800) {
            return String.format("%03d", (2500 - movie.getYear())) + "-";
        }

        if (null == movie.getDisplayOrder() || movie.getDisplayOrder().equals(0) || movie.getDisplayOrder() > 999) {
            return "";
        }

        return String.valueOf(movie.getDisplayOrder());
    }
}
