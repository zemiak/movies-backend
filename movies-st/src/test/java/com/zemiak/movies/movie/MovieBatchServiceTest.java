package com.zemiak.movies.movie;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.json.Json;

import com.zemiak.movies.AssuredRequests;

import org.junit.jupiter.api.Test;

public class MovieBatchServiceTest {
    AssuredRequests req;

    public MovieBatchServiceTest() {
        this.req = new AssuredRequests();
    }

    @Test
    public void oneExisting() {
        var movies = Json.createArrayBuilder().add("201510/Scooby_Kde_vezi_ten_vlkodlak.m4v").build();
        List<String> newMovies = req.post("/movies/filternew", movies).jsonPath().getList("$", String.class);
        assertTrue(newMovies.isEmpty(), "No new movies in the provided list");
    }

    @Test
    public void oneNew() {
        var movies = Json.createArrayBuilder().add("3200/HelloWorld.mp9").build();
        List<String> newMovies = req.post("/movies/filternew", movies).jsonPath().getList("$", String.class);
        assertEquals(1, newMovies.size(), "One new movie in the list");
    }

    @Test
    public void fetchExisting() {
        var movies = Json.createArrayBuilder().add("201510/Scooby_Kde_vezi_ten_vlkodlak.m4v").build();
        List<MovieUI> newMovies = req.post("/movies/fetch", movies).jsonPath().getList("$", MovieUI.class);
        assertEquals(1, newMovies.size(), "One existing movie in the list");
    }

    @Test
    public void fetchNotExisting() {
        var movies = Json.createArrayBuilder().add("3200/HelloWorld.mp9").build();
        List<MovieUI> newMovies = req.post("/movies/fetch", movies).jsonPath().getList("$", MovieUI.class);
        assertTrue(newMovies.isEmpty(), "No movies in the provided list");
    }

    @Test
    public void newReleases() {
        List<MovieUI> newMovies = req.get("/movies/ui/new").jsonPath().getList("$", MovieUI.class);
        assertFalse(newMovies.isEmpty(), "Returned some new movies");
    }
}
