package com.zemiak.movies.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.strings.DateFormatter;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MovieServiceTest {
    AssuredRequests req;

    public MovieServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void all() {
        List<Movie> movies = req.get("/movies/all").jsonPath().getList("$", Movie.class);
        assertFalse(movies.isEmpty(), "Movies are not empty");
    }

    // @Test
    public void create() {
        JsonObject movie = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .add("genre", 0l)
            .build();

        Long id = req.post("/movies", movie).as(Long.class);
        assertTrue(null != id, "Create movie returns ID");

        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as created");
        assertEquals(movie.getString("name"), entity.name, "Name must be the same as created");
        assertEquals(movie.getString("created"), DateFormatter.format(entity.created).toString(), "created must be the same as created");
        assertEquals(movie.getString("pictureFileName"), entity.pictureFileName, "pictureFileName must be the same as created");
    }

    // @Test
    public void find() {
        Long id = 0l;
        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as specified");
        assertEquals("Not defined", entity.name, "Name must be Not defined");
    }

    // @Test
    public void remove() {
        JsonObject movie = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .add("genre", 0l)
            .build();

        Long id = req.post("/movies", movie).as(Long.class);
        assertTrue(null != id, "Create movie returns ID");

        req.delete("/movies/" + String.valueOf(id), Status.NO_CONTENT.getStatusCode());
        req.get("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    // @Test
    public void update() {
        Long id = 0l;
        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as specified");
        assertEquals("Not defined", entity.name, "Name must be Not defined");

        entity.name = "Some";
        JsonObject json = entity.toJson();

        req.put("/movies", json, Status.NO_CONTENT.getStatusCode());

        entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals("Some", entity.name, "Updated name must be: Some");

        entity.name = "Not defined";
        req.put("/movies", entity.toJson(), Status.NO_CONTENT.getStatusCode());
    }

    // @Test
    public void search() throws UnsupportedEncodingException {
        String text = "not";
        List<Movie> movies = req.get("/movies/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$", Movie.class);
        assertFalse(movies.isEmpty());
        assertEquals("Not defined", movies.get(0).name, "One Not defined should be found");
    }

    // @Test
    public void createMustFailIfIDIsNotEmpty() {
        JsonObject movie = Json.createObjectBuilder()
            .add("id", 42)
            .add("name", "Hello, World")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .add("genre", 0l)
            .build();
        req.post("/movies", movie, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    // @Test
    public void updateMustFailIfIDIsEmpty() {
        JsonObject movie = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .add("genre", 0l)
            .build();
        req.put("/movies", movie, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    // @Test
    public void findMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.get("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    // @Test
    public void deleteMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.delete("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    // @Test
    public void updateMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        JsonObject movie = Json.createObjectBuilder()
            .add("id", id)
            .add("name", "Hello, World")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .add("genre", 0l)
            .build();
        req.put("/movies", movie, Status.NOT_FOUND.getStatusCode());
    }

    // @Test
    public void searchMustReturnEmptyListOnNonExistingCriteria() throws UnsupportedEncodingException {
        String text = "Does Not Exist";
        List<Movie> movies = req.get("/movies/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$", Movie.class);
        assertTrue(movies.isEmpty());
    }

    // @Test
    public void removeMustFailIfMoviesWithMovieExist() {
        Long idThatIsReferencedInMoviesButNotInMovies = 30010l;
        req.delete("/movies/" + String.valueOf(idThatIsReferencedInMoviesButNotInMovies), Status.NOT_ACCEPTABLE.getStatusCode());
    }
}