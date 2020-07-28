package com.zemiak.movies.itunes;

import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class ItunesArtwork {
    public static int DIMENSION = 1024;
    private String trackName;
    private Integer trackNumber;
    private Integer trackCount;
    private String artworkUrl;

    public static ItunesArtwork mapFromEntry(JsonValue value) {
        ItunesArtwork item = new ItunesArtwork();
        JsonObject entry = (JsonObject) value;
        item.setTrackName(entry.getString("trackName", null));
        item.setTrackNumber(entry.getInt("trackNumber", -1));
        item.setTrackCount(entry.getInt("trackCount", -1));
        item.setArtworkUrl(entry.getString("artworkUrl100", null));

        if (null != item.getArtworkUrl()) {
            item.setArtworkUrl(item.getArtworkUrl().replace("100x100", String.format("%dx%d", DIMENSION, DIMENSION)));
        }
        return item;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.trackName);
        hash = 11 * hash + Objects.hashCode(this.trackNumber);
        hash = 11 * hash + Objects.hashCode(this.trackCount);
        hash = 11 * hash + Objects.hashCode(this.artworkUrl);
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
        if (!Objects.equals(this.trackName, other.trackName)) {
            return false;
        }
        if (!Objects.equals(this.artworkUrl, other.artworkUrl)) {
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
