package com.zemiak.movies;

import static io.restassured.RestAssured.given;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.JsonValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

public class AssuredRequests {
    public AssuredRequests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.defaultParser = Parser.JSON;
    }

    public Response delete(String endpoint, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().delete(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response get(String endpoint, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().get(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response post(String endpoint, JsonValue body, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().body(body.toString()).post(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response put(String endpoint, JsonValue body, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().body(body.toString()).put(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response get(String endpoint) {
        return get(endpoint, 200);
    }

    public Response delete(String endpoint) {
        return delete(endpoint, 200);
    }

    public Response post(String endpoint, JsonValue body) {
        return post(endpoint, body, 200);
    }

    public Response put(String endpoint, JsonValue body) {
        return put(endpoint, body, 200);
    }
}
