package com.zemiak.movies;

import static io.restassured.RestAssured.given;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

public class AssuredRequests {
    private final static String BASE = "http://127.0.0.1:8081";

    public AssuredRequests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.defaultParser = Parser.JSON;
    }

    public Response delete(String endpoint, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                baseUri(BASE).
                when().delete(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response get(String endpoint, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                baseUri(BASE).
                when().get(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response post(String endpoint, JsonValue body, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                baseUri(BASE).
                when().body(body.toString()).post(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response put(String endpoint, JsonValue body, int expectedCode) {
        return  given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                baseUri(BASE).
                when().body(body.toString()).put(endpoint).
                then().statusCode(expectedCode).extract().response();
    }

    public Response get(String endpoint) {
        return get(endpoint, 200);
    }

    public JsonArray getArray(String endpoint) {
        return Json.createReader(new StringReader(get(endpoint).body().asString())).readArray();
    }

    public JsonObject getObject(String endpoint) {
        return Json.createReader(new StringReader(get(endpoint).body().asString())).readObject();
    }

    public Response delete(String endpoint) {
        return delete(endpoint, 200);
    }

    public Response post(String endpoint, JsonValue body) {
        return post(endpoint, body, 200);
    }

    public Response post(String endpoint, String body) {
        return  given().headers("Content-Type", ContentType.TEXT, "Accept", ContentType.JSON).
                baseUri(BASE).
                when().body(body.toString()).post(endpoint).
                then().statusCode(200).extract().response();
    }

    public Response put(String endpoint, JsonValue body) {
        return put(endpoint, body, 200);
    }
}
