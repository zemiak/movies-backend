package com.zemiak.movies.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ProvideConfiguration;
import com.zemiak.movies.serie.Serie;
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
    private static Long GENRE_CHILDREN = 3l;
    private static Long NON_EXISTING_ID = -42l;

    AssuredRequests req;

    public MovieUIServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void getSerieMoviesForScoobyDooContains4EpisodesBrowse() {
        String url = "/series/browse?id=" + String.valueOf(SERIE_SCOOBYDOO);
        List<GuiDTO> movies = req.get(url).jsonPath().getList("$", GuiDTO.class);
        assertEquals(4, movies.size(), "ScoobyDoo contains 4 episodes: " + url);
    }

    @Test
    public void getGenreMoviesDoesNotContainSerieMoviesBecauseTheyBelongToSerie() {
        assertFalse(cut.getGenreMovies(GENRE_CHILDREN)
            .stream()
            .map(dto -> { return (Movie) Movie.findById(dto.id);})
            .filter(m -> null != m.serieId && m.serieId != Serie.ID_NONE)
            .findAny()
            .isPresent(), "Movie with a serie does not belong to a genre list");
    }
}
