package com.zemiak.movies.batch.metadata;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.scraper.WebMetadataReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class YearUpdater {
    private static final BatchLogger LOG = BatchLogger.getLogger("YearUpdater");

    @Inject
    MovieService service;

    public void process(final List<String> files) {
        WebMetadataReader reader = new WebMetadataReader(null, null, null, true);

        files.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(ConfigurationProvider.getPath().length())))
                .filter(movie -> null != movie)
                .filter(movie -> null != movie.getUrl() && !movie.getUrl().trim().isEmpty())
                .filter(movie -> null == movie.getYear())
                .filter(movie -> null != movie.getWebPage())
                .forEach(movie -> {
                    final Integer year = reader.parseYear(movie);
                    if (null == year) {
                        return;
                    }

                    movie.setYear(year);
                    service.mergeAndSave(movie);

                    LOG.log(Level.INFO, "... updated year in DB of " + movie.getFileName() + " to " + movie.getYear(), movie.getId());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.FINE, "1000ms waiting interrupted", ex);
                    }
                });
    }
}
