package com.zemiak.movies.metadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import javax.json.JsonArray;
import javax.json.JsonObject;

import com.zemiak.movies.scraper.ItunesArtwork;

import org.junit.jupiter.api.Test;

public class ItunesArtworkServiceTest {
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
        assertNotNull(first.getArtworkUrl100());
        assertFalse(first.getArtworkUrl100().isBlank());

        assertNotNull(first.getTrackName());
        assertFalse(first.getTrackName().isBlank());
    }
}
