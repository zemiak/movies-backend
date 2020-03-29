package com.zemiak.movies.metadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.zemiak.movies.scraper.ItunesArtwork;

import org.junit.jupiter.api.Test;

public class ItunesArtworkServiceTest {
    private static int KB_128 = 128 * 1024;

    ItunesArtworkService cut;

    public ItunesArtworkServiceTest() {
        cut = new ItunesArtworkService();
    }

    @Test
    public void jsonDataNotEmpty() {
        JsonObject data = cut.getMovieArtworkResultsJson("Matrix");
        assertTrue(data.containsKey("results"), "Contains results key");
        assertTrue(data.containsKey("resultCount"), "Contains resultCount key");
        assertFalse(data.getInt("resultCount") == 0, "resultCount > 0");
    }

    @Test
    public void jsonDataContainsArtwork() {
        JsonObject data = cut.getMovieArtworkResultsJson("Matrix");
        JsonArray entries = data.getJsonArray("results");
        assertFalse(entries.isEmpty(), "Results not empty");
        assertFalse(entries.isNull(0), "First result not null");

        ItunesArtwork first = entries.stream().map(ItunesArtwork::mapFromEntry).findFirst().get();
        assertNotNull(first.getArtworkUrl100(), "URL must not be null");
        assertFalse(first.getArtworkUrl100().isBlank(), "URL must not be empty");

        assertNotNull(first.getTrackName(), "Track name must not be null");
        assertFalse(first.getTrackName().isBlank(), "Track name must not be empty");
    }

    @Test
    public void fetchArtwork() throws IOException {
        JsonObject data = cut.getMovieArtworkResultsJson("Matrix");
        JsonArray entries = data.getJsonArray("results");
        assertFalse(entries.isEmpty(), "Results not empty");
        assertFalse(entries.isNull(0), "First result not null");

        ItunesArtwork first = entries.stream().map(ItunesArtwork::mapFromEntry).findFirst().get();
        assertNotNull(first.getArtworkUrl100(), "URL must not be null");
        assertFalse(first.getArtworkUrl100().isBlank(), "URL must not be empty");

        InputStream stream = cut.getMovieArtwork(first);
        assertNotNull(stream, "Stream must not be null");

        int streamLength;
        streamLength = stream.readAllBytes().length;
        assertTrue(streamLength > KB_128, "Stream must contain data, but it contains only # of bytes: " + streamLength);

        stream.close();
    }
}