package com.zemiak.movies.scraper;

import java.nio.file.Paths;
import java.util.Arrays;

import com.zemiak.movies.movie.Movie;

public class WebMetadataReader {
    private final IWebMetadataReader[] readers;
    private final String imgPath;

    public WebMetadataReader(String imgPath) {
        this.readers = new IWebMetadataReader[]{
            new Csfd()
        };

        this.imgPath = imgPath;
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
        return Paths.get(imgPath, "movie", movie.pictureFileName).toString();
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
