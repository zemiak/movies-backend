package com.zemiak.movies.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import com.zemiak.movies.AssuredRequests;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ConfigServiceTest {
    AssuredRequests req;

    public ConfigServiceTest() {
        req = new AssuredRequests();
    }

//    @Test
    public void thereIsPortInConfig() {
        String body = req.get("/config").body().asString();

        assertNotNull(body, "Body must not be null");
        assertFalse(body.isBlank(), "Body must not be blank");

        JsonObject config = Json.createReader(new StringReader(body)).readObject();
        assertNotNull(config, "Parsed config must not be null");
        assertTrue(config.containsKey("port"), "Config must contain port");

        int port = config.getInt("port", -1);
        assertTrue(port > 999, "Config must contain a reasonable port, but it is " + port + ". Body: " + body);
    }
}
