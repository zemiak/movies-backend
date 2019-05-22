package com.zemiak.movies.language;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Test;

public class LanguageServiceTest {
    @Test
    public void saveMustThrowWhenIdNull() {
        LanguageService service = new LanguageService();
        Language lang = new Language();

        assertThrows(WebApplicationException.class, () -> {
            service.save(lang);
        });
    }
}
