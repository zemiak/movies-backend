package com.zemiak.movies.service.scraper;

import com.zemiak.movies.domain.Movie;
import java.nio.file.Paths;
import java.util.Arrays;

public class WebMetadataReader {
    private final IWebMetadataReader[] readers;
    private final String imgPath, path, ffmpeg;
    private final boolean developmentSystem;

    public WebMetadataReader(String imgPath, String path, String ffmpeg, boolean developmentSystem) {
        this.readers = new IWebMetadataReader[]{
            new Csfd(),
            new Imdb(),
            new GeneralMetadataReader(path, ffmpeg, developmentSystem)
        };

        this.imgPath = imgPath;
        this.path = path;
        this.ffmpeg = ffmpeg;
        this.developmentSystem = developmentSystem;
    }

    public String parseDescription(final Movie movie) {
        for (IWebMetadataReader reader: readers) {
            if (reader.accepts(movie)) {
                return reader.parseDescription(movie);
            }
        }

        return null;
    }

    public boolean canFetchDescription(final Movie movie) {
        return Arrays.asList(readers).stream()
                .filter(reader -> reader.accepts(movie))
                .findFirst()
                .isPresent();
    }

    public boolean processThumbnail(final Movie movie) {
        for (IWebMetadataReader reader: readers) {
            if (reader.accepts(movie)) {
                reader.setImageFileName(getImageFileName(movie));
                reader.processThumbnail(movie);

                return true;
            }
        }

        return false;
    }

    public String getImageFileName(final Movie movie) {
        return Paths.get(imgPath, "movie", movie.getPictureFileName()).toString();
    }

    public Integer parseYear(final Movie movie) {
        for (IWebMetadataReader reader: readers) {
            if (reader.accepts(movie)) {
                return reader.parseYear(movie);
            }
        }

        return null;
    }

    public String readPage(Movie movie) {
        for (IWebMetadataReader reader: readers) {
            if (reader.accepts(movie)) {
                return reader.getWebPage(movie);
            }
        }

        return null;
    }
}
