package com.zemiak.movies.serie;

import java.time.LocalDateTime;

import javax.json.JsonObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.genre.GenreRepository;
import com.zemiak.movies.strings.DateFormatter;
import com.zemiak.movies.strings.NullAwareJsonObject;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Serie extends PanacheEntityBase implements Comparable<Serie> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Size(max = 128, min = 1)
    @Column(name = "name")
    @NotNull
    public String name;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    public String pictureFileName;

    @Column(name = "display_order")
    @NotNull
    public Integer displayOrder;

    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @NotNull
    public Genre genre;

    @Column(name = "created")
    public LocalDateTime created;

    @Column(name = "tv_show")
    public Boolean tvShow;

    public static JsonObject toJson(PanacheEntityBase baseEntity) {
        Serie entity = (Serie) baseEntity;

        return NullAwareJsonObjectBuilder.create()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("pictureFileName", entity.pictureFileName)
            .add("displayOrder", entity.displayOrder)
            .add("created", DateFormatter.format(entity.created))
            .add("tvShow", entity.tvShow)
            .add("genre", null == entity.genre ? null : entity.genre.id)
            .build();
    }

    public JsonObject toJson() {
        return toJson(this);
    }

    public Serie(JsonObject from, GenreRepository genreRepo) {
        NullAwareJsonObject data = NullAwareJsonObject.create(from);

        this.id = data.getLong("id");
        this.name = data.getString("name");
        this.pictureFileName = data.getString("pictureFileName");
        this.displayOrder = data.getInteger("displayOrder");
        this.created = data.getDateTime("created");
        this.tvShow = data.getBoolean("tvShow");

        Long genreId = data.getLong("genre");
        if (null != genreId) {
            this.genre = genreRepo.findById(genreId);
        }
    }

    public Serie() {
        this.created = LocalDateTime.now();
        this.tvShow = Boolean.FALSE;
    }

    public Serie(Long id) {
        this();
        this.id = id;
    }

    public void copyFrom(Serie entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
        this.genre = entity.genre;
        this.tvShow = entity.tvShow;
        this.created = entity.created;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Serie)) {
            return false;
        }
        Serie other = (Serie) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Serie o) {
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

    @JsonIgnore
    public boolean isEmpty() {
        return 0 == id;
    }

    public String getGenreName() {
        return null == genre ? "<None>" : (genre.isEmpty() ? "<None>" : genre.name);
    }

    public static Serie create() {
        Serie serie = new Serie();
        serie.created = LocalDateTime.now();
        serie.displayOrder = 9000;
        serie.genre = Genre.findById(0l);

        return serie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
