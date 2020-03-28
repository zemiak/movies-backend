package com.zemiak.movies.batch;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ProvideConfiguration;
import com.zemiak.movies.movie.Movie;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UpdateMoviesSchedulerTest {
    private static final String MOVIES_FOLDER = "/tmp";
    private static final String ENDPOINT = "/jobs/update-movies";

    AssuredRequests req;

    public UpdateMoviesSchedulerTest() throws IOException {
        req = new AssuredRequests();
        ProvideConfiguration.init();
    }

    @Test
    public void makeSureNewMP4WillGetImported() throws IOException {
        String name = "HelloWorld1";
        Files.write(Paths.get(MOVIES_FOLDER + "/Movies/new/" + name + ".mp4"), "Hello".getBytes());
        given().when().get(ENDPOINT).then().statusCode(Status.NO_CONTENT.getStatusCode());
        List<Movie> movies = req.get("/movies/search/" + URLEncoder.encode(name, "UTF-8")).jsonPath().getList("$", Movie.class);
        assertEquals(1, movies.size(), "New file can be found in DB");
    }

    @Test
    public void makeSureNewM4VWillGetImported() throws IOException {
        String name = "HelloWorld2";
        Files.write(Paths.get(MOVIES_FOLDER + "/Movies/new/" + name + ".m4v"), "Hello".getBytes());
        given().when().get(ENDPOINT).then().statusCode(Status.NO_CONTENT.getStatusCode());
        List<Movie> movies = req.get("/movies/search/" + URLEncoder.encode(name, "UTF-8")).jsonPath().getList("$", Movie.class);
        assertEquals(1, movies.size(), "New file can be found in DB");
    }
}
