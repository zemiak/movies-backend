package com.zemiak.movies.movie;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.genre.GenreIds;
import com.zemiak.movies.language.Language;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;
import com.zemiak.movies.ui.GuiDTO;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;

@Entity
@JsonbNillable
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
    public String subtitlesLanguageCode;

    @Column(name = "original_language")
    public String originalLanguageCode;

    @Column(name = "language")
    public String languageCode;

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
            .add("description", entity.description)
            .add("webPage", entity.webPage);

        NullAwareJsonObjectBuilder.addInteger(builder, "displayOrder", entity.displayOrder);
        NullAwareJsonObjectBuilder.addLong(builder, "serieId", entity.serieId);
        NullAwareJsonObjectBuilder.addString(builder, "subtitlesId", entity.subtitlesLanguageCode);
        NullAwareJsonObjectBuilder.addString(builder, "originalLanguageId", entity.originalLanguageCode);
        NullAwareJsonObjectBuilder.addString(builder, "languageId", entity.languageCode);
        NullAwareJsonObjectBuilder.addLong(builder, "genreId", entity.genreId);
        NullAwareJsonObjectBuilder.addInteger(builder, "year", entity.year);
        NullAwareJsonObjectBuilder.addDate(builder, "created", entity.created);

        return builder.build();
    }

    public JsonObject toJson() {
        return toJson(this);
    }

    public GuiDTO toDto() {
        return getDto();
    }

    @JsonbTransient
    public GuiDTO getDto() {
        return new GuiDTO("movie", this.name, ConfigurationProvider.getExternalURL() + "/stream/" + id, ConfigurationProvider.getExternalURL() + "/movies/thumbnail?id=" + id, id);
    }

    public void copyFrom(Movie entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
        this.description = entity.description;
        this.fileName = entity.fileName;
        this.genreId = entity.genreId;
        this.languageCode = entity.languageCode;
        this.originalLanguageCode = entity.originalLanguageCode;
        this.originalName = entity.originalName;
        this.serieId = entity.serieId;
        this.subtitlesLanguageCode = entity.subtitlesLanguageCode;
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

    @JsonbTransient
    public boolean isDescriptionEmpty() {
        return null == description || "".equals(description.trim()) || "''".equals(description.trim());
    }

    @JsonbTransient
    public boolean isUrlEmpty() {
        return null == url || "".equals(url.trim()) || "''".equals(url.trim());
    }

    @JsonbTransient
    public boolean isEmptySerie() {
        return null == serieId || serieId == Serie.ID_NONE;
    }

    @JsonbTransient
    public boolean isEmptyGenre() {
        return null == genreId || genreId == GenreIds.ID_NONE;
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

    @JsonbTransient
    public String getSerieName() {
        if (isEmptySerie()) {
            return "None";
        }

        Serie serie = Serie.findById(this.serieId);
        return null == serie ? "Serie " + this.serieId + " not found" : serie.name;
    }

    @JsonbTransient
    public String getGenreName() {
        if (isEmptyGenre()) {
            return "None";
        }

        Genre genre = Genre.findById(this.genreId);
        return null == genre ? "Genre " + this.serieId + " not found" : genre.name;
    }

    @JsonbTransient
    public String getGenrePictureFileName() {
        if (null == genreId) {
            return "null.jpg";
        }

        Genre genre = Genre.findById(this.genreId);
        return null == genre ? "null.jpg" : genre.pictureFileName;
    }

    @JsonbTransient
    public String getLanguageName() {
        if (null == this.languageCode) {
            return "None";
        }

        Language lang = Language.findById(this.languageCode);
        return null == lang ? "None" : lang.name;
    }

    @JsonbTransient
    public String getOriginalLanguageName() {
        if (null == this.originalLanguageCode) {
            return "None";
        }

        Language lang = Language.findById(this.originalLanguageCode);
        return null == lang ? "None" : lang.name;
    }

    @JsonbTransient
    public String getSubtitlesName() {
        if (null == this.subtitlesLanguageCode) {
            return "None";
        }

        Language lang = Language.find(this.subtitlesLanguageCode).firstResult();
        return null == lang ? "None" : lang.name;
    }

    public String getThumbnailUrl() {
        return ConfigurationProvider.getExternalURL() + "/movies/thumbnail?id=" + this.id;
    }

    public void setThumbnailUrl(String url) {
        // pass - so the JSONB does not complain
    }

    public static void traverse(Sort sort, Consumer<Movie> action) {
        long count = count();
        int pageSize = 10;
        long pageCount = count / pageSize + (count % pageSize > 0 ? 1 : 0);
        int pageIndex = 0;
        while (pageIndex < pageCount) {
            findAll(sort).page(pageIndex, pageSize).stream().map(e -> (Movie) e).forEach(action);
            pageIndex++;
        }
    }
}
