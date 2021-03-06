package com.zemiak.movies.scraper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zemiak.movies.movie.Movie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class JsoupUtils {
    private static final Logger LOG = Logger.getLogger(JsoupUtils.class.getName());

    private JsoupUtils() {
    }

    public static Document getDocument(final String url) {
        try {
            return Jsoup.connect(url).timeout(5000).get();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read {0}: {1}", new Object[]{url, ex});
            return null;
        }
    }

    public static Document getMovieDocument(final Movie movie) {
        String url = movie.url;

        return getDocument(url);
    }

    public static Document getMovieDocumentFromString(Movie movie) {
        if (null == movie.webPage && null != movie.url) {
            Document webPage = getMovieDocument(movie);
            if (null == webPage) {
                return null;
            }

            movie.webPage = webPage.toString();
        }

        return Jsoup.parse(movie.webPage);
    }
}
