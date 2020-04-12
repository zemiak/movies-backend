package com.zemiak.movies.genre;

import java.time.LocalDateTime;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;
import com.zemiak.movies.ui.GuiDTO;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Genre extends PanacheEntityBase implements Comparable<Genre> {
    public static final Long ID_NONE = 0L;
    public static final Long ID_FRESH = -1L;
    public static final Long ID_UNASSIGNED = -2L;
    public static final Long ID_RECENTLY_ADDED = -3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

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

        return genre;
    }

    public Genre() {
        this.created = LocalDateTime.now();
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

    @JsonIgnore
    public boolean isEmpty() {
        return id == 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("id", this.id)
            .add("name", this.name)
            .add("pictureFileName", this.pictureFileName);

        NullAwareJsonObjectBuilder.addLong(builder, "displayOrder", this.displayOrder);
        NullAwareJsonObjectBuilder.addLong(builder, "protectedGenre", this.protectedGenre);
        NullAwareJsonObjectBuilder.addDate(builder, "created", this.created);

        return builder.build();
    }

    public GuiDTO toDto() {
        return new GuiDTO("genre", this.name, "/genres/browse?id=" + id, "/genres/thumbnail?id=" + id);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static GuiDTO getFreshGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_FRESH;
        g.name = "Fresh";
        return g.toDto();
    }

    public static GuiDTO getUnassignedGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_UNASSIGNED;
        g.name = "Unassigned";
        return g.toDto();
    }

    public static GuiDTO getRecentlyAddedGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_RECENTLY_ADDED;
        g.name = "New";
        return g.toDto();
    }
}
