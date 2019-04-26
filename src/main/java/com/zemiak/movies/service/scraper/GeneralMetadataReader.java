package com.zemiak.movies.service.scraper;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.batch.service.logs.CommandLine;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.UrlDTO;
import com.zemiak.movies.strings.Joiner;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class GeneralMetadataReader implements IWebMetadataReader {
    private static final BatchLogger LOG = BatchLogger.getLogger(GeneralMetadataReader.class.getName());
    private String imageFileName;
    private final String ffmpegPath;
    private final String path;
    private final boolean developmentSystem;

    public GeneralMetadataReader(final String path, final String ffmpegPath, final boolean developmentSystem) {
        this.ffmpegPath = ffmpegPath;
        this.developmentSystem = developmentSystem;
        this.path = path;
    }

    @Override
    public boolean accepts(Movie movie) {
        return true;
    }

    @Override
    public List<UrlDTO> getUrlCandidates(String movieName) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getReaderName() {
        return "GeneralLocalScraper";
    }

    @Override
    public String parseDescription(Movie movie) {
        return "";
    }

    @Override
    public void processThumbnail(Movie movie) {
        final String movieFileName = Paths.get(path, movie.getFileName()).toString();

        final List<String> params = Arrays.asList(
            "-s", "180", "-i", movieFileName, "-o", imageFileName
        );

        try {
            if (!developmentSystem) {
                CommandLine.execCmd(ffmpegPath, params);
            } else {
                LOG.log(Level.FINE, "dry run:{0} {1}", new Object[]{ffmpegPath, null == params ? "" : Joiner.join(params, "|")});
            }

            LOG.log(Level.INFO, "Generated thumbnail {0} ...", imageFileName);
        } catch (IllegalStateException | InterruptedException | IOException ex) {
            LOG.log(Level.SEVERE, "DID NOT generate thumbnail {0}: {1} ...",
                    new Object[]{imageFileName, ex});
        }
    }

    @Override
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public Integer parseYear(final Movie movie) {
        return null;
    }

    @Override
    public String getWebPage(Movie movie) {
        return null;
    }
}
