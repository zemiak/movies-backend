package com.zemiak.movies.batch.metadata;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.scraper.WebMetadataReader;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class DescriptionsUpdater {
    private static final BatchLogger LOG = BatchLogger.getLogger(DescriptionsUpdater.class.getName());

    private final WebMetadataReader reader = new WebMetadataReader(null, null, null, true);
    @Inject private MovieService service;
    private final String path = ConfigurationProvider.getPath();

    public void process(final List<String> files) {
        files.stream()
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie && movie.isDescriptionEmpty())
                .filter(movie -> reader.canFetchDescription(movie))
                .filter(movie -> null != movie.getWebPage())
                .forEach(movie -> {
                    String desc = reader.parseDescription(movie);

                    movie.setDescription(desc);
                    service.mergeAndSave(movie);

                    LOG.log(Level.INFO, "... update description in DB of ", movie.getFileName());
                });
    }
}
