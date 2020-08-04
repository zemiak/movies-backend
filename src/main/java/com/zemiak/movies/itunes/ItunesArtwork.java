package com.zemiak.movies.itunes;

import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class ItunesArtwork {
    public static int DIMENSION = 1024;
    public String description;
    public Integer trackNumber;
    public Integer trackCount;
    public String imageUrl;

    public static ItunesArtwork mapFromEntry(JsonValue value) {
        ItunesArtwork item = new ItunesArtwork();
        JsonObject entry = (JsonObject) value;
        item.description = entry.getString("trackName", null);
        item.trackNumber = entry.getInt("trackNumber", -1);
        item.trackCount = entry.getInt("trackCount", -1);
        item.imageUrl = entry.getString("artworkUrl100", null);

        if (null != item.imageUrl) {
            item.imageUrl = item.imageUrl.replace("100x100", String.format("%dx%d", DIMENSION, DIMENSION));
        }
        return item;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.description);
        hash = 11 * hash + Objects.hashCode(this.trackNumber);
        hash = 11 * hash + Objects.hashCode(this.trackCount);
        hash = 11 * hash + Objects.hashCode(this.imageUrl);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItunesArtwork other = (ItunesArtwork) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.imageUrl, other.imageUrl)) {
            return false;
        }
        if (!Objects.equals(this.trackNumber, other.trackNumber)) {
            return false;
        }
        if (!Objects.equals(this.trackCount, other.trackCount)) {
            return false;
        }
        return true;
    }
}
