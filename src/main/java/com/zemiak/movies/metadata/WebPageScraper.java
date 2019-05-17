package com.zemiak.movies.metadata;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.lookup.ConfigurationProvider;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.scraper.WebMetadataReader;

@Dependent
public class WebPageScraper {
    private static final BatchLogger LOG = BatchLogger.getLogger("YearUpdater");

    @Inject
    MovieService service;

    public void process(final List<String> files) {
        WebMetadataReader reader = new WebMetadataReader(null, null, null, true);

        files.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(ConfigurationProvider.getPath().length())))
                .filter(movie -> null != movie)
                .filter(movie -> null != movie.getUrl() && !"".equals(movie.getUrl()))
                .filter(movie -> null == movie.getWebPage() || "".equals(movie.getWebPage()))
                .limit(50)
                .forEach(movie -> {
                    String webPage = reader.readPage(movie);
                    movie.setWebPage(webPage);
                    service.mergeAndSave(movie);

                    LOG.log(Level.INFO, "... updated web page in DB of " + movie.getUrl(), movie.getId());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.FINE, "1000ms waiting interrupted", ex);
                    }
                });
    }
}
