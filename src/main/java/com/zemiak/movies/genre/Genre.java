package com.zemiak.movies.genre;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;
import com.zemiak.movies.ui.GuiDTO;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

@Entity
@JsonbNillable
public class Genre extends PanacheEntity implements Comparable<Genre> {
    public static final Long EMPTY = 9l;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    public String name;

    @Column(name = "protected")
    public Long protectedGenre;

    @Column(name = "picture_file_name")
    @Size(max = 512)
    public String pictureFileName;

    @Column(name = "display_order")
    public Long displayOrder;

    @Column(name = "created")
    public LocalDateTime created;

    public static Genre create() {
        Genre genre = new Genre();
        genre.created = LocalDateTime.now();
        genre.displayOrder = 9000l;
        genre.protectedGenre = 0l;

        return genre;
    }

    public Genre() {
    }

    public Genre(Long id) {
        this();
        this.id = id;
    }

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void copyFrom(Genre entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Genre)) {
            return false;
        }
        Genre other = (Genre) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @JsonbTransient
    public boolean isEmpty() {
        return id == EMPTY;
    }

    @Override
    public String toString() {
        return name;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("name", this.name)
            .add("pictureFileName", this.pictureFileName);

        NullAwareJsonObjectBuilder.addLong(builder, "id", this.id);
        NullAwareJsonObjectBuilder.addLong(builder, "displayOrder", this.displayOrder);
        NullAwareJsonObjectBuilder.addLong(builder, "protectedGenre", this.protectedGenre);
        NullAwareJsonObjectBuilder.addDate(builder, "created", this.created);

        return builder.build();
    }

    public GuiDTO toDto() {
        return new GuiDTO("genre", this.name, ConfigurationProvider.getExternalURL() + "/browse?id=" + id, ConfigurationProvider.getExternalURL() + "/genres/thumbnail?id=" + id, id);
    }

    public String getThumbnailUrl() {
        return ConfigurationProvider.getExternalURL() + "/genres/thumbnail?id=" + this.id;
    }

    public void setThumbnailUrl(String url) {
        // pass - so the JSONB does not complain
    }

    @Override
    public int compareTo(Genre o) {
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

    public static Genre getFreshGenre() {
        Genre g = new Genre();
        g.id = GenreIds.ID_FRESH;
        g.name = "Fresh";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static Genre getUnassignedGenre() {
        Genre g = new Genre();
        g.id = GenreIds.ID_UNASSIGNED;
        g.name = "Unassigned";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static Genre getRecentlyAddedGenre() {
        Genre g = new Genre();
        g.id = GenreIds.ID_RECENTLY_ADDED;
        g.name = "New";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static boolean isArtificial(Long id) {
        return GenreIds.ID_FRESH.equals(id) || GenreIds.ID_RECENTLY_ADDED.equals(id) || GenreIds.ID_UNASSIGNED.equals(id);
    }

    public static Genre findArtificial(Long id) {
        if (GenreIds.ID_FRESH.equals(id)) {
            return getFreshGenre();
        }

        if (GenreIds.ID_UNASSIGNED.equals(id)) {
            return getUnassignedGenre();
        }

        if (GenreIds.ID_RECENTLY_ADDED.equals(id)) {
            return getRecentlyAddedGenre();
        }

        throw new IllegalArgumentException("Unknown ID " + id);
    }

    public static void traverse(Sort sort, Consumer<Genre> action) {
        long count = count();
        int pageSize = 10;
        long pageCount = count / pageSize + (count % pageSize > 0 ? 1 : 0);
        int pageIndex = 0;
        while (pageIndex < pageCount) {
            findAll(sort).page(pageIndex, pageSize).stream().map(e -> (Genre) e).forEach(action);
            pageIndex++;
        }
    }
}
