package com.zemiak.movies.language;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class LanguageServiceIntegrationTest {
    @Inject
    LanguageService service;

    @Test
    public void allLanguagesCountGreaterThanZero() {
        assertTrue(service.all().size() > 0);
    }
}
