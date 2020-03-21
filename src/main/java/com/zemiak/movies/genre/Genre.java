package com.zemiak.movies.genre;

import java.time.LocalDateTime;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zemiak.movies.strings.DateFormatter;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Genre extends PanacheEntity implements Comparable<Genre> {
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
        return Json.createObjectBuilder()
            .add("id", this.id)
            .add("name", this.name)
            .add("created", DateFormatter.format(this.created))
            .add("pictureFileName", this.pictureFileName)
            .add("protectedGenre", this.protectedGenre)
            .add("displayOrder", this.displayOrder)
            .build();
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
}
