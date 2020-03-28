package com.zemiak.movies.scraper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zemiak.movies.movie.Movie;

import org.junit.jupiter.api.Test;

public class CsfdTest {
    Csfd cut;
    Movie movie;

    public CsfdTest() {
        cut = new Csfd();
        movie = Movie.create();
        movie.url = "https://www.csfd.cz/film/9499-matrix/prehled/";
    }

    @Test
    public void shoudAcceptHttpUrl() {
        movie.url = "http://www.csfd.cz/film/9499-matrix/prehled/";
        assertTrue(cut.accepts(movie));
    }

    // @Test
    public void shoudAcceptHttpsUrl() {
        assertTrue(cut.accepts(movie));
    }

    // @Test
    public void fetchDescription() {
        String desc = cut.parseDescription(movie);
        assertTrue(null != desc, "Description must not be null");
        assertTrue(!desc.trim().isEmpty(), "Description must not be blank");
    }
}
