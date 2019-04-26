package com.zemiak.movies.batch.plex.movie;

import com.zemiak.movies.batch.service.RefreshStatistics;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.strings.Encodings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
public class StandaloneMovieWriter {
    public static final String PATH = "Movies";
    private static final Logger LOG = Logger.getLogger(StandaloneMovieWriter.class.getName());

    @Inject RefreshStatistics stats;
    @PersistenceContext EntityManager em;

    public void process(Movie movie) throws IOException {
        String plexLinkPath = ConfigurationProvider.getPlexLinkPath();
        String path = ConfigurationProvider.getPath();
        String movieName = (null == movie.getOriginalName() || "".equals(movie.getOriginalName().trim()))
                ? movie.getName() : movie.getOriginalName();
        Path linkName = Paths.get(plexLinkPath, PATH, Encodings.deAccent(movieName) + ".m4v");

        if (null == movieName || "".equals(movieName)) {
            LOG.log(Level.SEVERE, "Movie {0} has no name", movie.getFileName());
            return;
        }

        Integer i = 2;
        while (Files.exists(linkName)) {
            linkName = Paths.get(plexLinkPath, PATH, movieName +  "-" + String.valueOf(i++) + ".m4v");

            if (i > 100) {
                throw new IllegalStateException("Cannot find a suitable name for " + linkName.toString());
            }
        }

        Path existing = Paths.get(path, movie.getFileName());
        Files.createSymbolicLink(linkName, existing);
        em.merge(movie);

        LOG.log(Level.FINE, "Created movie link {0} -> {1}", new Object[]{linkName.toString(), existing.toString()});
    }
}
