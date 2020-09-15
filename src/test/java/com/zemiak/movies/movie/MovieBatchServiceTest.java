package com.zemiak.movies.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.json.Json;

import com.zemiak.movies.AssuredRequests;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
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
}
