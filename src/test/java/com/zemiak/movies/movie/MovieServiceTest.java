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
import com.zemiak.movies.ui.GuiDTO;

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

    private JsonObject getHelloWorldMovie() {
        return Json.createObjectBuilder().add("name", "Hello, World").add("fileName", "hello-world.m4v")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)))
                .add("pictureFileName", "u-a.jpg").add("year", 2010).add("genre", 0l).build();
    }

    @Test
    public void create() {
        JsonObject movie = getHelloWorldMovie();
        Long id = req.post("/movies", movie).as(Long.class);
        assertTrue(null != id, "Create movie returns ID");

        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as created");
        assertEquals(movie.getString("name"), entity.name, "Name must be the same as created");
        assertEquals(movie.getString("created"), DateFormatter.format(entity.created).toString(),
                "created must be the same as created");
        assertEquals(movie.getString("pictureFileName"), entity.pictureFileName,
                "pictureFileName must be the same as created");

        req.delete("/movies/" + String.valueOf(id), Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void find() {
        Long id = 1l;
        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as specified");
        assertEquals("Patalie s Lochneskou", entity.name, "Name must be Patalie s Lochneskou");
    }

    @Test
    public void remove() {
        JsonObject movie = getHelloWorldMovie();
        Long id = req.post("/movies", movie).as(Long.class);
        assertTrue(null != id, "Create movie returns ID");

        req.delete("/movies/" + String.valueOf(id), Status.NO_CONTENT.getStatusCode());
        req.get("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void artificialTrue() {
        assertTrue(Boolean.TRUE);
    }

    @Test
    public void update() {
        Long id = 1l;
        Movie entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals(id, entity.id, "Movie ID must be the same as specified");
        assertEquals("Patalie s Lochneskou", entity.name, "Name must be Patalie s Lochneskou");

        entity.name = "Some";
        JsonObject json = entity.toJson();

        req.put("/movies", json, Status.NO_CONTENT.getStatusCode());

        entity = req.get("/movies/" + String.valueOf(id)).jsonPath().getObject("$", Movie.class);
        assertEquals("Some", entity.name, "Updated name must be: Some");

        entity.name = "Patalie s Lochneskou";
        req.put("/movies", entity.toJson(), Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void search() throws UnsupportedEncodingException {
        String text = "vezi";
        List<GuiDTO> movies = req.get("/movies/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$",
                GuiDTO.class);
        assertFalse(movies.isEmpty());
        assertEquals("Kde vezi ten vlkodlak", movies.get(0).title, "One Kde vezi ten vlkodlak should be found");
    }

    @Test
    public void createMustFailIfIDIsNotEmpty() {
        JsonObject movie = Json.createObjectBuilder().add("id", 42).add("name", "Hello, World")
                .add("fileName", "hello-world.m4v")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)))
                .add("pictureFileName", "u-a.jpg").add("genre", 0l).build();
        req.post("/movies", movie, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void updateMustFailIfIDIsEmpty() {
        JsonObject movie = getHelloWorldMovie();
        req.put("/movies", movie, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void findMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.get("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.delete("/movies/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void updateMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        JsonObject movie = Json.createObjectBuilder().add("id", id).add("name", "Hello, World")
                .add("fileName", "hello-world.m4v")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)))
                .add("pictureFileName", "u-a.jpg").add("genre", 0l).build();
        req.put("/movies", movie, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void searchMustReturnEmptyListOnNonExistingCriteria() throws UnsupportedEncodingException {
        String text = "Does Not Exist";
        List<GuiDTO> movies = req.get("/movies/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$",
                GuiDTO.class);
        assertTrue(movies.isEmpty());
    }
}
