package com.zemiak.movies.batch.plex.movie;

import com.zemiak.movies.batch.service.RefreshStatistics;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.strings.Encodings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
public class SerieItemWriter {
    private static final Logger LOG = Logger.getLogger(SerieItemWriter.class.getName());
    public static final String PATH = "TV Shows";
    static final Integer GOT = 1000;
    static final Integer MASH = 1;

    @Inject RefreshStatistics stats;
    @PersistenceContext EntityManager em;

    public void process(Movie movie) throws IOException {
        String plexLinkPath = ConfigurationProvider.getPlexLinkPath();
        Serie serie = movie.getSerie();
        Path folder = Paths.get(plexLinkPath, PATH, Encodings.deAccent(serie.getName()));

        Files.createDirectories(folder);

        if (Objects.equals(GOT, serie.getId())) {
            process(folder, movie, 2, movie.getDisplayOrder() / 100);
        } else if (Objects.equals(MASH, serie.getId())) {
            process(folder, movie, 3, 1);
        } else {
            process(folder, movie, 2, 1);
        }
    }

    private void process(Path folder, Movie movie, Integer decimals, Integer season) throws IOException {
        String path = ConfigurationProvider.getPath();
        String serie = Encodings.deAccent(movie.getSerieName());
        String seasonNumber = String.format("%02d", season);
        String format = "%0" + String.valueOf(decimals) + "d";
        Integer number = null == movie.getDisplayOrder() ? 0 : movie.getDisplayOrder();
        String movieName = (null == movie.getOriginalName() || "".equals(movie.getOriginalName().trim()))
                ? movie.getName() : movie.getOriginalName();

        if (null == movieName || "".equals(movieName)) {
            movieName = "";
        } else {
            movieName = " - " + Encodings.deAccent(movieName);
        }

        String episodeName = serie + " - s" + seasonNumber + "e" + String.format(format, number) + movieName + ".m4v";

        Path linkFolder = Paths.get(folder.toString(), "Season " + seasonNumber);
        Path linkName = Paths.get(linkFolder.toString(), episodeName);
        Path existing = Paths.get(path, movie.getFileName());

        Files.createDirectories(linkFolder);
        Files.createSymbolicLink(linkName, existing);

        em.merge(movie);

        LOG.log(Level.FINE, "Created TV Show link {0} -> {1}", new Object[]{linkName.toString(), existing.toString()});
    }
}
