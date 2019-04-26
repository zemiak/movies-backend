package com.zemiak.movies.batch.plex.movie;

import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.MovieService;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PlexMovieWriter {
    private static final Logger LOG = Logger.getLogger(PlexMovieWriter.class.getName());

    @Inject private StandaloneMovieWriter movieWriter;
    @Inject private SerieItemWriter serieItemWriter;
    @Inject private MovieService service;

    public void process(final List<String> list) {
        String path = ConfigurationProvider.getPath();

        list.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie)
                .forEach(movie -> {
                    Serie serie = movie.getSerie();

                    if (null != serie && serie.getTvShow()) {
                        try {
                            serieItemWriter.process(movie);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "IO/Error creating a link for serie item " + movie.getFileName() + ":" + ex.getMessage(), ex);
                        }
                    } else {
                        try {
                            movieWriter.process(movie);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "IO/Error creating a link for movie " + movie.getFileName() + ": " + ex.getMessage(), ex);
                        }
                    }
                });
    }
}
