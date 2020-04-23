package com.zemiak.movies.scraper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

    @Test
    public void shoudAcceptHttpsUrl() {
        assertTrue(cut.accepts(movie));
    }

    @Test
    public void fetchDescription() {
        String desc = cut.parseDescription(movie);
        assertNotNull(desc, "Description must not be null");
        assertFalse(desc.trim().isEmpty(), "Description must not be blank");
        assertTrue(desc.contains("Anderson"), "Thomas Anderson is Neo");
        assertTrue(desc.contains("Matrix"), "Title is Matrix");
    }

    @Test
    public void urlcandidatesMustReturnSomeEntries() {
        List<UrlDTO> candidates = cut.getUrlCandidates("Matrix");
        assertFalse(candidates.isEmpty(), "Candidates must return at least one item");
    }

    @Test
    public void urlCandidatesMustReturnResultsAccordingToMovieName() {
        List<UrlDTO> candidates = cut.getUrlCandidates("Matrix");
        UrlDTO first = candidates.get(0);
        String desc = first.getDescription();

        assertNotNull(desc, "Description must not be null");
        assertFalse(desc.trim().isEmpty(), "Description must not be blank");
        assertTrue(desc.contains("Matrix"), "Title is Matrix");
    }

    @Test
    public void fetchWebPage() {
        String content = cut.getWebPage(movie);
        assertNotNull(content, "Web Page must not be empty");
        assertFalse(content.isEmpty(), "Web Page must not be blank");
        assertTrue(content.contains("Matrix"), "Title is Matrix");
    }

    @Test
    public void fetchYear() {
        movie.webPage = cut.getWebPage(movie);
        Integer year = cut.parseYear(movie);
        assertNotNull(year, "Year must not be null");
        assertEquals(1999, year, "Matrix release date is 1999");
    }

    @Test
    public void fetchThumbnail() {
        String url = cut.getImageUrl(movie);
        assertNotNull(url, "URL must not be empty");
        assertFalse(url.isEmpty(), "URL must not be blank");
        assertTrue(url.contains("http"), "URL must be valid");
    }
}
