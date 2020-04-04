package com.zemiak.movies.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ui.GuiDTO;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MovieUIServiceTest {
    private static int TESTING_YEAR = 2020;
    private static String FRESH_2020 = "Patalie s Lochneskou";
    private static String FRESH_2019 = "Kde vezi ten vlkodlak";
    private static String RECENT_2020 = "Riddick: Dark Fury 3";
    private static String RECENT_2019 = "Riddick: Dark Fury 2";
    private static Long SERIE_SCOOBYDOO = 30010l;
    private static Long GENRE_SF = 4l;
    private static Long NON_EXISTING_ID = -42l;

    AssuredRequests req;

    @Inject
    MovieUIService cut;

    public MovieUIServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void getFreshMoviesContainsTwoRecentMovies() {
        List<GuiDTO> movies = cut.getFreshMovies(TESTING_YEAR);
        assertNotNull(movies, "Movies must not be null");
        assertEquals(2, movies.size(), "Fresh movie list contains 2 items");
        assertEquals(movies.get(0).title, FRESH_2020, "The first movie is from 2020");
        assertEquals(movies.get(1).title, FRESH_2019, "The first movie is from 2019");
    }

    @Test
    public void getRecentlyAddedMoviesContainsTwoRecentlyAdded() {
        List<GuiDTO> movies = cut.getRecentlyAddedMovies();
        assertNotNull(movies, "Movies must not be null");
        assertTrue(movies.size() > 1, "Fresh movie list contains 2 items");
        assertEquals(movies.get(0).title, RECENT_2020, "The first movie is from 2020");
        assertEquals(movies.get(1).title, RECENT_2019, "The first movie is from 2019");
    }

    @Test
    public void getSerieMoviesForScoobyDooContains4Episodes() {
        List<GuiDTO> movies = cut.getSerieMovies(SERIE_SCOOBYDOO);
        assertNotNull(movies, "Movies must not be null");
        assertEquals(4, movies.size(), "ScoobyDoo contains 4 episodes");
    }

    @Test
    public void getSerieMoviesForUnknownContains0Episodes() {
        List<GuiDTO> movies = cut.getSerieMovies(NON_EXISTING_ID);
        assertNotNull(movies, "Movies must not be null");
        assertEquals(0, movies.size(), "Unknown contains 0 episodes");
    }

    @Test
    public void getGenreMoviesForSFContains2Movies() {
        List<GuiDTO> movies = cut.getGenreMovies(GENRE_SF);
        assertNotNull(movies, "Movies must not be null");
        assertEquals(2, movies.size(), "SF contains 2 movies");
    }

    @Test
    public void getGenreMoviesForUnknownContains0Episodes() {
        List<GuiDTO> movies = cut.getGenreMovies(NON_EXISTING_ID);
        assertNotNull(movies, "Movies must not be null");
        assertEquals(0, movies.size(), "Unknown contains 0 movies");
    }

    @Test
    public void getUnassignedMoviesForUnknownContains1Episode() {
        List<GuiDTO> movies = cut.getUnassignedMovies();
        assertNotNull(movies, "Movies must not be null");
        assertEquals(1, movies.size(), "Unknown contains 0 movies");
        assertEquals(RECENT_2020, movies.get(0).title, "New Riddick is still unassigned");
    }
}
