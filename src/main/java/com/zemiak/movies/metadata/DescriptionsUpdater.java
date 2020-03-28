package com.zemiak.movies.metadata;

import java.util.List;
import java.util.logging.Level;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.scraper.WebMetadataReader;

@Dependent
public class DescriptionsUpdater {
    private static final BatchLogger LOG = BatchLogger.getLogger(DescriptionsUpdater.class.getName());

    private final WebMetadataReader reader = new WebMetadataReader(null);
    private final String path = ConfigurationProvider.getPath();

    @Inject MovieService service;

    public void process(final List<String> files) {
        files.stream()
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie && movie.isDescriptionEmpty())
                .filter(movie -> reader.canFetchDescription(movie))
                .filter(movie -> null != movie.webPage)
                .forEach(movie -> {
                    String desc = reader.parseDescription(movie);

                    movie.description = desc;
                    movie.persist();

                    LOG.log(Level.INFO, "... update description in DB of ", movie.fileName);
                });
    }
}
