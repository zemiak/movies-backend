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

public class LanguageServiceTest {
    AssuredRequests req;

    public LanguageServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void exists() {
        List<Language> languages = req.get("/languages/paged?page=0&pageSize=10").jsonPath().getList("$", Language.class);
        assertFalse(languages.isEmpty(), "Languages are not empty");
    }

    private JsonObject getHelloWorldLanguage() {
        return Json.createObjectBuilder()
                .add("name", "Hello, World")
                .add("displayOrder", 90)
                .add("id", "ua")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)).toString())
                .add("pictureFileName", "u-a.jpg").build();
    }

    @Test
    public void create() {
        JsonObject language = getHelloWorldLanguage();
        String id = req.post("/languages", language).asString();
        assertTrue(null != id, "Create language returns ID");

        Language entity = req.get("/languages/" + id).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as created");
        assertEquals(language.getString("name"), entity.name, "Name must be the same as created");
        assertEquals(language.getString("created"), DateFormatter.format(entity.created),
                "created must be the same as created");
        assertEquals(language.getString("pictureFileName"), entity.pictureFileName,
                "pictureFileName must be the same as created");
    }

    @Test
    public void find() {
        String id = "en";
        Language entity = req.get("/languages/" + id).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");
    }

    @Test
    public void remove() {
        JsonObject language = getHelloWorldLanguage();
        String id = language.getString("id");

        if (404 == req.get("/languages/" + id).getStatusCode()) {
            req.post("/languages", language).asString();
        }

        req.delete("/languages/" + id, Status.NO_CONTENT.getStatusCode());
        req.get("/languages/" + id, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void update() {
        String id = "en";
        Language entity = req.get("/languages/" + id).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");

        entity.name = "Some";
        JsonObject json = entity.toJson();

        req.put("/languages", json, Status.NO_CONTENT.getStatusCode());

        entity = req.get("/languages/" + id).jsonPath().getObject("$", Language.class);
        assertEquals("Some", entity.name, "Updated name must be: Some");

        entity.name = "English";
        req.put("/languages", entity.toJson(), Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void search() throws UnsupportedEncodingException {
        String text = "On";
        List<Language> languages = req.get("/languages/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath()
                .getList("$", Language.class);
        assertFalse(languages.isEmpty());
        assertEquals("None", languages.get(0).name, "One None should be found");
    }

    @Test
    public void updateMustFailIfIDIsEmpty() {
        JsonObject language = Json.createObjectBuilder().addNull("id").add("name", "Hello, World")
                .add("fileName", "hello-world.m4v")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)))
                .add("pictureFileName", "u-a.jpg").build();

        req.put("/languages", language, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void findMustFailIfEntityDoesNotExist() {
        String id = "he";
        req.get("/languages/" + id, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteMustFailIfEntityDoesNotExist() {
        String id = "he";
        req.delete("/languages/" + id, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void updateMustFailIfEntityDoesNotExist() {
        String id = "he";
        JsonObject language = Json.createObjectBuilder().add("id", id).add("name", "Hello, World")
                .add("fileName", "hello-world.m4v")
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)))
                .add("pictureFileName", "u-a.jpg").build();
        req.put("/languages", language, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void searchMustReturnEmptyListOnNonExistingCriteria() throws UnsupportedEncodingException {
        String text = "Does Not Exist";
        List<Language> languages = req.get("/languages/search/" + URLEncoder.encode(text, "UTF-8")).jsonPath()
                .getList("$", Language.class);
        assertTrue(languages.isEmpty());
    }

    @Test
    public void removeMustFailIfSeriesWithLanguageExist() {
        String idThatIsReferencedInSeries = "en";
        req.delete("/languages/" + idThatIsReferencedInSeries, Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void removeMustFailIfMoviesWithLanguageExist() {
        String idThatIsReferencedInMoviesButNotInSeries = "no";
        req.delete("/languages/" + idThatIsReferencedInMoviesButNotInSeries,
                Status.NOT_ACCEPTABLE.getStatusCode());
    }
}
