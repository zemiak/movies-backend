package com.zemiak.movies.language;

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
public class LanguageServiceTest {
    AssuredRequests req;

    public LanguageServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void all() {
        List<Language> languages = req.get("/languages/all").jsonPath().getList("$", Language.class);
        assertFalse(languages.isEmpty(), "Languages are not empty");
    }

    @Test
    public void create() {
        JsonObject language = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("fileName", "hello-world.m4v")
            .add("code", "ua")
            .add("created", DateFormatter.format(LocalDateTime.now()).toString())
            .add("pictureFileName", "u-a.jpg")
            .build();

        Long id = req.post("/languages", language).as(Long.class);
        assertTrue(null != id, "Create language returns ID");

        Language entity = req.get("/languages/" + String.valueOf(id)).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as created");
        assertEquals(language.getString("name"), entity.name, "Name must be the same as created");
        assertEquals(language.getString("created"), DateFormatter.format(entity.created), "created must be the same as created");
        assertEquals(language.getString("pictureFileName"), entity.pictureFileName, "pictureFileName must be the same as created");
    }

    @Test
    public void find() {
        Long id = 0l;
        Language entity = req.get("/languages/" + String.valueOf(id)).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");
    }

    @Test
    public void remove() {
        JsonObject language = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("fileName", "hello-world.m4v")
            .add("code", "ua")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .build();

        Long id = req.post("/languages", language).as(Long.class);
        assertTrue(null != id, "Create language returns ID");

        req.delete("/languages/" + String.valueOf(id), Status.NO_CONTENT.getStatusCode());
        req.get("/languages/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void update() {
        Long id = 0l;
        Language entity = req.get("/languages/" + String.valueOf(id)).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");

        entity.name = "Some";
        JsonObject json = entity.toJson();

        req.put("/languages", json, Status.NO_CONTENT.getStatusCode());

        entity = req.get("/languages/" + String.valueOf(id)).jsonPath().getObject("$", Language.class);
        assertEquals("Some", entity.name, "Updated name must be: Some");

        entity.name = "English";
        req.put("/languages", entity.toJson(), Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void search() throws UnsupportedEncodingException {
        String text = "On";
        List<Language> languages = req.get("/languages/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$", Language.class);
        assertFalse(languages.isEmpty());
        assertEquals("None", languages.get(0).name, "One None should be found");
    }

    @Test
    public void createMustFailIfIDIsNotEmpty() {
        JsonObject language = Json.createObjectBuilder()
            .add("id", 42)
            .add("code", "ua")
            .add("name", "Hello, World")
            .add("fileName", "hello-world.m4v")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .build();
        req.post("/languages", language, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void updateMustFailIfIDIsEmpty() {
        JsonObject language = Json.createObjectBuilder()
            .add("name", "Hello, World")
            .add("fileName", "hello-world.m4v")
            .add("code", "ua")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .build();
        req.put("/languages", language, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void findMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.get("/languages/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        req.delete("/languages/" + String.valueOf(id), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void updateMustFailIfEntityDoesNotExist() {
        Long id = 42000l;
        JsonObject language = Json.createObjectBuilder()
            .add("id", id)
            .add("code", "ua")
            .add("name", "Hello, World")
            .add("fileName", "hello-world.m4v")
            .add("created", DateFormatter.format(LocalDateTime.now()))
            .add("pictureFileName", "u-a.jpg")
            .build();
        req.put("/languages", language, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void searchMustReturnEmptyListOnNonExistingCriteria() throws UnsupportedEncodingException {
        String text = "Does Not Exist";
        List<Language> languages = req.get("/languages/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath().getList("$", Language.class);
        assertTrue(languages.isEmpty());
    }

    @Test
    public void removeMustFailIfSeriesWithLanguageExist() {
        Long idThatIsReferencedInSeries = 0l;
        req.delete("/languages/" + String.valueOf(idThatIsReferencedInSeries), Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void removeMustFailIfMoviesWithLanguageExist() {
        Long idThatIsReferencedInMoviesButNotInSeries = 13l;
        req.delete("/languages/" + String.valueOf(idThatIsReferencedInMoviesButNotInSeries), Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void findByCode() {
        String code = "en";
        Language entity = req.get("/languages/" + String.valueOf(code) + "/code").jsonPath().getObject("$", Language.class);
        assertEquals(code, entity.code, "Language Code must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");
    }

    @Test
    public void findByCodeMustFailIfEntityDoesNotExist() {
        String code = "xy";
        req.get("/languages/" + String.valueOf(code) + "/code", Status.NOT_FOUND.getStatusCode());
    }
}
