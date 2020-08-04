package com.zemiak.movies.itunes;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.zemiak.movies.scraper.UrlDTO;

public final class ItunesArtwork {
    public static int DIMENSION = 1024;
    
    private ItunesArtwork() {
    }

    public static UrlDTO mapFromEntry(JsonValue value) {
        UrlDTO item = new UrlDTO();
        JsonObject entry = (JsonObject) value;
        item.description = entry.getString("trackName", null);
        item.imageUrl = entry.getString("artworkUrl100", null);

        if (null != item.imageUrl) {
            item.imageUrl = item.imageUrl.replace("100x100", String.format("%dx%d", DIMENSION, DIMENSION));
        }
        return item;
    }
}
