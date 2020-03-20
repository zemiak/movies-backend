package com.zemiak.movies.language;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class LanguageServiceIntegrationTest {
    @Test
    public void allLanguagesCountGreaterThanZero() {
        given()
          .when().get("/genres/all")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }
}
