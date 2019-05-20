package com.zemiak.movies.metadata;

import java.util.List;
import java.util.logging.Level;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.RefreshStatistics;
import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.lookup.ConfigurationProvider;
import com.zemiak.movies.movie.MovieService;

@Dependent
public class NewMoviesCreator {
    private final String path = ConfigurationProvider.getPath();

    @Inject MovieService service;
    @Inject RefreshStatistics stats;

    private static final BatchLogger LOG = BatchLogger.getLogger(NewMoviesCreator.class.getName());

    public void process(final List<String> files) {
        files.stream()
                .map(this::getRelativeFilename)
                .filter(fileName -> null == service.findByFilename(fileName))
                .map(fileName -> service.create(fileName))
                .forEach(m -> {
                    stats.incrementCreated();
                    LOG.log(Level.INFO, "Created a new movie ''{0}''/''{1}'', id {2}...",
                            new Object[]{m.getFileName(), m.getName(), m.getId()});
                });
    }

    private String getRelativeFilename(final String absoluteFilename) {
        String relative = absoluteFilename;
        if (relative.startsWith(path)) {
            relative = relative.substring(path.length());
        }

        relative = MovieService.removeFileSeparatorFromStartIfNeeded(relative);

        return relative;
    }
}
