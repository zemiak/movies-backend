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
public class ThumbnailCreator {
    private static final BatchLogger LOG = BatchLogger.getLogger("ThumbnailCreator");

    private final String path = ConfigurationProvider.getPath();
    private final String imgPath = ConfigurationProvider.getImgPath();

    @Inject MovieService service;

    public void process(final List<String> files) {
        files.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie)
                .filter(movie -> !Paths.get(imgPath, "movie", movie.pictureFileName).toFile().exists())
                .forEach(movie -> {
                    WebMetadataReader reader = new WebMetadataReader(imgPath);

                    if (reader.processThumbnail(movie)) {
                        LOG.log(Level.FINE, "Generated a thumbnail {0}", movie.pictureFileName);
                    } else {
                        LOG.log(Level.SEVERE, "Error generating a thumbnail {0}", movie.pictureFileName);
                    }
                });
    }
}
