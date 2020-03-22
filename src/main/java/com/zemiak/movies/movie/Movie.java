package com.zemiak.movies.movie;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.scraper.Csfd;
import com.zemiak.movies.scraper.Imdb;
import com.zemiak.movies.strings.DateFormatter;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Movie extends PanacheEntityBase implements Comparable<Movie> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Size(max = 512)
    @Column(name = "file_name")
    public String fileName;

    @Size(max = 128, min = 1)
    @Column(name = "name")
    @NotNull
    public String name;

    @Size(max = 128)
    @Column(name = "original_name")
    public String originalName;

    @Size(max = 128)
    @Column(name = "url")
    public String url;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    public String pictureFileName;

    @Column(name = "display_order")
    public Integer displayOrder;

    @Size(max = 2147483647)
    @Column(name = "description", length = 16384)
    public String description;

    @Column(name = "serie_id")
    public Long serieId;

    @Column(name = "subtitles")
    public Long subtitlesId;

    @Column(name = "original_language")
    public Long originalLanguageId;

    @Column(name = "language")
    public Long languageId;

    @NotNull
    @Column(name = "genre_id")
    public Long genreId;

    @Column(name = "created")
    public LocalDateTime created;

    @Column(name = "year")
    public Integer year;

    @Column(name = "web_page", length = 128)
    public String webPage;

    public Movie() {
        this.created = LocalDateTime.now();
    }

    public Movie(Long id) {
        this();
        this.id = id;
    }

    public static JsonObject toJson(PanacheEntityBase baseEntity) {
        Objects.requireNonNull(baseEntity);
        Movie entity = (Movie) baseEntity;

        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("id", entity.id)
            .add("fileName", entity.fileName)
            .add("name", entity.name)
            .add("originalName", entity.originalName)
            .add("url", entity.url)
            .add("pictureFileName", entity.pictureFileName)
            .add("displayOrder", entity.displayOrder)
            .add("description", entity.description)
            .add("serieId", entity.serieId)
            .add("subtitlesId", entity.subtitlesId)
            .add("originalLanguageId", entity.originalLanguageId)
            .add("languageId", entity.languageId)
            .add("genreId", entity.genreId)
            .add("year", entity.year)
            .add("webPage", entity.webPage);

        if (null != entity.created) {
            builder.add("created", DateFormatter.format(entity.created));
        }

        return builder.build();
    }

    public JsonObject toJson() {
        return toJson(this);
    }

    public void copyFrom(Movie entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
        this.description = entity.description;
        this.fileName = entity.fileName;
        this.genreId = entity.genreId;
        this.languageId = entity.languageId;
        this.originalLanguageId = entity.originalLanguageId;
        this.originalName = entity.originalName;
        this.serieId = entity.serieId;
        this.subtitlesId = entity.subtitlesId;
        this.url = entity.url;
        this.year = entity.year;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", name=" + name + '}';
    }

    public boolean isDescriptionEmpty() {
        return null == description || "".equals(description.trim()) || "''".equals(description.trim());
    }

    public boolean isUrlEmpty() {
        return null == url || "".equals(url.trim()) || "''".equals(url.trim());
    }

    public String getUrlFlag() {
        if (new Csfd().accepts(this)) {
            return "CSFD";
        } else if (new Imdb().accepts(this)) {
            return "IMDB";
        }

        return "";
    }

    public boolean isEmptySerie() {
        return null == serieId || serieId == 0;
    }

    @Override
    public int compareTo(Movie o) {
        if (null == displayOrder && null != o.displayOrder) {
            return -1;
        }

        if (null != displayOrder && null == o.displayOrder) {
            return 1;
        }

        if (null == displayOrder && null == o.displayOrder) {
            return 0;
        }

        return displayOrder.compareTo(o.displayOrder);
    }

    public static Movie create() {
        Movie movie = new Movie();
        movie.created = LocalDateTime.now();

        return movie;
    }
}
