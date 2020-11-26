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
import javax.json.JsonValue;
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

    private JsonObject getHelloWorldLanguage(String code) {
        return Json.createObjectBuilder()
                .add("name", "Hello, World")
                .add("displayOrder", 90)
                .add("code", code)
                .add("created", DateFormatter.format(LocalDateTime.now().minusYears(20)).toString())
                .add("pictureFileName", "u-a.jpg").build();
    }

    @Test
    public void create() {
        String code = "ua";
        JsonObject language = getHelloWorldLanguage(code);
        Long id = Long.valueOf(req.post("/languages", language).asString());
        assertTrue(null != id, "Create language returns ID");

        Language entity = req.get("/languages/" + code).jsonPath().getObject("$", Language.class);
        assertEquals(id, entity.id, "Language ID must be the same as created");
        assertEquals(language.getString("name"), entity.name, "Name must be the same as created");
        assertEquals(language.getString("created"), DateFormatter.format(entity.created),
                "created must be the same as created");
        assertEquals(language.getString("pictureFileName"), entity.pictureFileName,
                "pictureFileName must be the same as created");
    }

    @Test
    public void find() {
        String code = "en";
        Language entity = req.get("/languages/" + code).jsonPath().getObject("$", Language.class);
        assertEquals(code, entity.code, "Language CODE must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");
    }

    @Test
    public void remove() {
        JsonObject language = getHelloWorldLanguage("xx");
        String code = language.getString("code");

        req.post("/languages", language).asString();
        req.delete("/languages/" + code, Status.NO_CONTENT.getStatusCode());
        req.get("/languages/" + code, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void update() {
        String code = "en";
        Language entity = req.get("/languages/" + code).jsonPath().getObject("$", Language.class);
        assertEquals(code, entity.code, "Language CODE must be the same as specified");
        assertEquals("English", entity.name, "Name must be English");

        entity.name = "Some";
        JsonObject json = entity.toJson();

        System.err.println("json:" + json.toString());

        req.put("/languages", json, Status.NO_CONTENT.getStatusCode());

        entity = req.get("/languages/" + code).jsonPath().getObject("$", Language.class);
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
        assertEquals("(None)", languages.get(0).name, "One None should be found");
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
        String code = "he";
        req.get("/languages/" + code, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteMustFailIfEntityDoesNotExist() {
        String code = "he";
        req.delete("/languages/" + code, Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void updateMustFailIfEntityDoesNotExist() {
        Long id = -10l;
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
    public void removeMustFailIfMoviesWithLanguageExist() {
        String code = "en";
        req.delete("/languages/" + code,
                Status.NOT_ACCEPTABLE.getStatusCode());
    }
}
