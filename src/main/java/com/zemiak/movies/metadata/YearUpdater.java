package com.zemiak.movies.metadata;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.scraper.WebMetadataReader;

@Dependent
public class YearUpdater {
    private static final BatchLogger LOG = BatchLogger.getLogger("YearUpdater");

    @Inject MovieService service;

    public void process(final List<String> files) {
        WebMetadataReader reader = new WebMetadataReader(null);

        files.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(ConfigurationProvider.getPath().length())))
                .filter(movie -> null != movie)
                .filter(movie -> null != movie.url && !movie.url.trim().isEmpty())
                .filter(movie -> null == movie.year)
                .filter(movie -> null != movie.year)
                .forEach(movie -> {
                    final Integer year = reader.parseYear(movie);
                    if (null == year) {
                        return;
                    }

                    movie.year = year;
                    movie.persist();

                    LOG.log(Level.INFO, "... updated year in DB of " + movie.fileName + " to " + movie.year, movie.id);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.FINE, "1000ms waiting interrupted", ex);
                    }
                });
    }
}
