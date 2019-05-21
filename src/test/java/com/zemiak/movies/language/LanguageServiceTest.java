package com.zemiak.movies.language;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LanguageServiceTest {
    @Test(expected = NullPointerException.class)
    public void saveMustThrowWhenIdNull() {
        LanguageService service = new LanguageService();
        Language lang = new Language();
        service.save(lang);
    }
}
