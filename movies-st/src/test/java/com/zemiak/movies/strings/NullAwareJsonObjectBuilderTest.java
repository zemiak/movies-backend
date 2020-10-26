package com.zemiak.movies.strings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.zemiak.movies.language.Language;

import org.junit.jupiter.api.Test;

public class NullAwareJsonObjectBuilderTest {
    JsonObjectBuilder builder;

    public NullAwareJsonObjectBuilderTest() {
        this.builder = NullAwareJsonObjectBuilder.create();
    }

    @Test
    public void testNotNullValue() {
        JsonObject data = this.builder.add("name", "John").build();
        assertEquals("John", data.getString("name"), "Name must be John");
    }

    @Test
    public void testNullValue() {
        String value = null;
        JsonObject data = this.builder.add("name", value).build();
        assertEquals(JsonValue.NULL, data.get("name"), "Null value must be null");
    }

    @Test
    public void testLanguageWithNulls() {
        Language entity = Language.create();
        entity.code = "en";
        entity.name = "English";
        entity.created = LocalDateTime.now();
        entity.displayOrder = 1;

        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("pictureFileName", entity.pictureFileName)
            .add("displayOrder", entity.displayOrder)
            .add("created", DateFormatter.format(entity.created));

        JsonObject data = builder.build();

        assertEquals("English", data.getString("name"), "Name must be English");
        assertEquals(JsonValue.NULL, data.get("pictureFileName"), "pictureFileName must be NULL");
    }

    @Test
    public void testLanguageWithNullsToJsonMethod() {
        Language entity = Language.create();
        entity.code = "en";
        entity.name = "English";
        entity.created = LocalDateTime.now();
        entity.displayOrder = 1;

        JsonObject data = entity.toJson();

        assertEquals("English", data.getString("name"), "Name must be English");
        assertEquals(JsonValue.NULL, data.get("pictureFileName"), "pictureFileName must be NULL");
    }
}
