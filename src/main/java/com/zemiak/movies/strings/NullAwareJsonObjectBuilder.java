package com.zemiak.movies.strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class NullAwareJsonObjectBuilder implements JsonObjectBuilder {
    public static JsonObjectBuilder create(JsonObjectBuilder builder) {
        return new NullAwareJsonObjectBuilder(builder);
    }

    public static JsonObjectBuilder create() {
        return create(Json.createObjectBuilder());
    }

    private final JsonObjectBuilder builder;

    private NullAwareJsonObjectBuilder(JsonObjectBuilder builder) {
        Objects.requireNonNull(builder);
        this.builder = builder;
    }

    public JsonObjectBuilder add(String name, JsonValue value) {
        Objects.requireNonNull(name);

        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        Objects.requireNonNull(name);
        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        Objects.requireNonNull(name);
        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        Objects.requireNonNull(name);
        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        Objects.requireNonNull(name);
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        Objects.requireNonNull(name);
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        Objects.requireNonNull(name);
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        Objects.requireNonNull(name);
        builder.add(name, value);
        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder value) {
        Objects.requireNonNull(name);
        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder value) {
        Objects.requireNonNull(name);
        if (null == value) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }

        return this;
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        Objects.requireNonNull(name);
        builder.addNull(name);
        return this;
    }

    @Override
    public JsonObject build() {
        return builder.build();
    }

    public static void addLong(JsonObjectBuilder builder2, String name, Long value) {
        if (null != value) {
            builder2.add(name, value);
        }
    }

    public static void addInteger(JsonObjectBuilder builder2, String name, Integer value) {
        if (null != value) {
            builder2.add(name, value);
        }
    }

    public static void addString(JsonObjectBuilder builder2, String name, String value) {
        if (null != value) {
            builder2.add(name, value);
        }
    }

    public static void addBoolean(JsonObjectBuilder builder2, String name, Boolean value) {
        if (null != value) {
            builder2.add(name, value);
        }
    }

    public static void addDate(JsonObjectBuilder builder2, String name, LocalDateTime value) {
        if (null != value) {
            builder2.add(name, DateFormatter.format(value));
        }
    }
}
