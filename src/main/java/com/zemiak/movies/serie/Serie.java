package com.zemiak.movies.serie;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;
import com.zemiak.movies.ui.GuiDTO;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Serie extends PanacheEntityBase implements Comparable<Serie> {
    public static final Long ID_NONE = 0l;

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

    @Column(name = "genre_id")
    @NotNull
    public Long genreId;

    @Column(name = "created")
    public LocalDateTime created;

    @Column(name = "tv_show")
    public Boolean tvShow;

    public static JsonObject toJson(PanacheEntityBase baseEntity) {
        Objects.requireNonNull(baseEntity);
        Serie entity = (Serie) baseEntity;

        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("pictureFileName", entity.pictureFileName);

        NullAwareJsonObjectBuilder.addLong(builder, "genreId", entity.genreId);
        NullAwareJsonObjectBuilder.addInteger(builder, "displayOrder", entity.displayOrder);
        NullAwareJsonObjectBuilder.addBoolean(builder, "tvShow", entity.tvShow);
        NullAwareJsonObjectBuilder.addDate(builder, "created", entity.created);

        return builder.build();
    }

    public JsonObject toJson() {
        return toJson(this);
    }

    public GuiDTO toDto() {
        return new GuiDTO("serie", this.name, ConfigurationProvider.getExternalURL() + "/series/browse?id=" + id, ConfigurationProvider.getExternalURL() + "/series/thumbnail?id=" + id, id);
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
        this.genreId = entity.genreId;
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

    public static Serie create() {
        Serie serie = new Serie();
        serie.created = LocalDateTime.now();
        serie.displayOrder = 9000;
        serie.genreId = 0l;

        return serie;
    }
}
