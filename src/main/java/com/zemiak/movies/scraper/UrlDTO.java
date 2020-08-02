package com.zemiak.movies.scraper;

import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;

public class UrlDTO {
    String url;
    String description;
    String imageUrl;
    Integer year;

    public UrlDTO() {
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("url", url)
            .add("description", description)
            .add("imageUrl", imageUrl);
        NullAwareJsonObjectBuilder.addInteger(builder, "year", year);

        return builder.build();
    }

    public UrlDTO(String url, String description, String imageUrl, Integer year) {
        this.url = url;
        this.description = description;
        this.imageUrl = imageUrl;
        this.year = year;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UrlDTO other = (UrlDTO) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }
}
