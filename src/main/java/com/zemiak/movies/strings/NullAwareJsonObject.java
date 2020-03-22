package com.zemiak.movies.strings;

import java.time.LocalDateTime;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class NullAwareJsonObject {
    private JsonObject data;

    public static NullAwareJsonObject create(JsonObject data) {
        return new NullAwareJsonObject(data);
    }

    private NullAwareJsonObject(JsonObject data) {
        this.data = data;
    }

    public Long getLong(String name) {
        JsonNumber value = data.getJsonNumber(name);
        return null == value ? null : value.longValue();
    }

    public LocalDateTime getDateTime(String name) {
        JsonString value = data.getJsonString(name);
        return null == value ? null : DateFormatter.parse(value.toString());
    }

    public Integer getInteger(String name) {
        JsonNumber value = data.getJsonNumber(name);
        return null == value ? null : value.intValue();
    }

    public String getString(String name) {
        JsonString value = data.getJsonString(name);
        return null == value ? null : value.toString();
    }

    public Boolean getBoolean(String name) {
        JsonValue value = data.get(name);
        return null == value ? null : JsonValue.TRUE.equals(value);
    }
}
