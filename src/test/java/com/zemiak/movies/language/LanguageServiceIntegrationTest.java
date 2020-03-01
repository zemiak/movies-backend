package com.zemiak.movies.language;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class LanguageServiceIntegrationTest {
    @Inject
    LanguageService service;

    // @Test
    // TODO: Panache tests will work again in 1.3.0
    public void allLanguagesCountGreaterThanZero() {
        assertTrue(service.all().size() > 0);
    }
}
