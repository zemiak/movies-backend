package com.zemiak.movies.scraper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.zemiak.movies.ProvideConfiguration;
import com.zemiak.movies.movie.Movie;

import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;

public class ImdbTest {
    Imdb cut;
    Movie movie;

    @BeforeAll
    public static void init() {
        ProvideConfiguration.init();
    }

    public ImdbTest() {
        cut = new Imdb();
        movie = Movie.create();
        movie.url = "https://www.imdb.com/title/tt0133093/?ref_=fn_al_tt_1";
    }

    // @Test
    public void shoudAcceptHttpUrl() {
        movie.url = "http://www.imdb.com/title/tt0133093/?ref_=fn_al_tt_1";
        assertTrue(cut.accepts(movie));
    }

    // @Test
    public void shoudAcceptHttpsUrl() {
        assertTrue(cut.accepts(movie));
    }

    // @Test
    public void fetchDescription() {
        String desc = cut.parseDescription(movie);
        assertNotNull(desc, "Description must not be null");
        assertFalse(desc.trim().isEmpty(), "Description must not be blank");
        assertTrue(desc.contains("Keanu Reeves"), "Keanu Reeves is Neo");
        assertTrue(desc.contains("hacker"), "Neo is Hacker");
    }

    // @Test
    public void urlcandidatesMustReturnSomeEntries() {
        List<UrlDTO> candidates = cut.getUrlCandidates("Matrix");
        assertFalse(candidates.isEmpty(), "Candidates must return at least one item");
    }

    // @Test
    public void urlCandidatesMustReturnResultsAccordingToMovieName() {
        List<UrlDTO> candidates = cut.getUrlCandidates("Matrix");
        UrlDTO first = candidates.get(0);
        String desc = first.getDescription();

        assertNotNull(desc, "Description must not be null");
        assertFalse(desc.trim().isEmpty(), "Description must not be blank");
        assertTrue(desc.contains("Matrix"), "Title is Matrix");
    }

    // @Test
    public void fetchWebPage() {
        String content = cut.getWebPage(movie);
        assertNotNull(content, "Web Page must not be empty");
        assertFalse(content.isEmpty(), "Web Page must not be blank");
        assertTrue(content.contains("Matrix"), "Title is Matrix");
    }

    // @Test
    public void fetchYear() {
        movie.webPage = cut.getWebPage(movie);
        Integer year = cut.parseYear(movie);
        assertNotNull(year, "Year must not be null");
        assertEquals(1999, year, "Matrix release date is 1999");
    }

    // @Test
    public void fetchThumbnail() {
        String url = cut.getImageUrl(movie);
        assertNotNull(url, "URL must not be empty");
        assertFalse(url.isEmpty(), "URL must not be blank");
        assertTrue(url.contains("http"), "URL must be valid");
    }
}
