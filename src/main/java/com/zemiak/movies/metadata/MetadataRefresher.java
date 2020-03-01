package com.zemiak.movies.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.RefreshStatistics;
import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.batch.logs.CommandLine;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.scraper.WebMetadataReader;
import com.zemiak.movies.strings.Joiner;

@Dependent
public class MetadataRefresher {
    private static final BatchLogger LOG = BatchLogger.getLogger(MetadataRefresher.class.getName());

    private WebMetadataReader descriptions;

    @Inject MovieService service;
    @Inject RefreshStatistics stats;

    private final String mp4tags = ConfigurationProvider.getMp4Tags();
    private final String path = ConfigurationProvider.getPath();
    private final String imgPath = ConfigurationProvider.getImgPath();
    private final String ffmpeg = ConfigurationProvider.getFFMpegThumbnailer();
    private final Boolean developmentSystem = ConfigurationProvider.isDevelopmentSystem();

    private static final String GENRE = "-g";
    private static final String NAME = "-s";
    private static final String YEAR = "-y";
    private static final String COMMENTS = "-c";

    @PostConstruct
    public void init() {
        descriptions = new WebMetadataReader(imgPath, path, ffmpeg, developmentSystem);
    }

    private void update(final String fileName, final String commandLineSwitch, final String value) {
        final List<String> params = new ArrayList<>();

        params.add(commandLineSwitch);
        params.add(value);
        params.add(fileName);

        try {
            if (!developmentSystem) {
                CommandLine.execCmd(mp4tags, params);
            } else {
                LOG.log(Level.INFO, "dry run:{0} {1}", new Object[]{mp4tags, null == params ? "" : Joiner.join(params, "|")});
            }

            LOG.info(String.format("Updated %s with %s on %s", commandLineSwitch, value, fileName));
        } catch (IOException | InterruptedException | IllegalStateException ex) {
            Logger.getLogger(MetadataRefresher.class.getName()).log(Level.SEVERE, "Cannot update " + commandLineSwitch + " for " + fileName, ex);
        }
    }

    private void updateName(final String fileName, final MovieMetadata data) {
        if (! data.isNameEqual()) {
            update(fileName, NAME, data.getMovieName());
        }
    }

    private void updateYear(final String fileName, final MovieMetadata data) {
        if (! data.isYearEqual()) {
            update(fileName, YEAR, String.valueOf(data.getMovie().year));
        }
    }

    private void updateGenre(final String fileName, final MovieMetadata data) {
        if (! data.isGenreEqual()) {
            update(fileName, GENRE, data.getMovie().composeGenreName());
        }
    }

    private void updateComment(final String fileName, final MovieMetadata data) {
        if (data.commentsShouldBeUpdatedQuiet()) {
            final String desc = descriptions.parseDescription(data.getMovie());

            if (null != desc && !desc.trim().isEmpty() && !desc.equals(data.getComments())) {
                update(fileName, COMMENTS, desc);

                data.getMovie().description = desc;
                service.mergeAndSave(data.getMovie());
            } else {
                if (null == desc) {
                    LOG.info("Wanted to update comment, but not because of null desc");
                }

                if (desc.trim().isEmpty()) {
                    LOG.info("Wanted to update comment, but not because of desc is empty");
                }

                if (!desc.equals(data.getComments())) {
                    LOG.info("Wanted to update comment, but not because of desc is filled in and correct");
                }
            }
        }
    }

    public void process(final List<String> files) {
        files.stream().forEach(fileName -> {
            Movie movie = service.findByFilename(fileName.substring(path.length()));
            MovieMetadata data = new MetadataReader(fileName, movie, service).get();

            if (null != movie && null != data) {
                if (! data.isMetadataEqual()) {
                    LOG.info("Metadata: going to update " + fileName);
                    updateName(fileName, data);
                    updateGenre(fileName, data);
                    updateComment(fileName, data);
                    updateYear(fileName, data);
                    stats.incrementUpdated();
                }

                if (null != movie.serie && !movie.serie.isEmpty() && (null == movie.displayOrder || movie.displayOrder.equals(0))) {
                    LOG.log(Level.SEVERE, "Movie with serie and no order " + fileName, movie);
                }
            }
        });
    }
}
