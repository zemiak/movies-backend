package com.zemiak.movies.domain;

import java.util.Objects;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class ItunesArtwork {
    private String trackName;
    private Integer trackNumber;
    private Integer trackCount;
    private String artworkUrl100;

    public static ItunesArtwork mapFromEntry(JsonValue value) {
        ItunesArtwork item = new ItunesArtwork();
        JsonObject entry = (JsonObject) value;
        item.setTrackName(entry.getString("trackName", null));
        item.setTrackNumber(entry.getInt("trackNumber", -1));
        item.setTrackCount(entry.getInt("trackCount", -1));
        item.setArtworkUrl100(entry.getString("artworkUrl100", null));
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

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.trackName);
        hash = 11 * hash + Objects.hashCode(this.trackNumber);
        hash = 11 * hash + Objects.hashCode(this.trackCount);
        hash = 11 * hash + Objects.hashCode(this.artworkUrl100);
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
        if (!Objects.equals(this.artworkUrl100, other.artworkUrl100)) {
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
