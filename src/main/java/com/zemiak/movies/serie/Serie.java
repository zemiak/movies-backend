package com.zemiak.movies.serie;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.genre.Genre;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Serie extends PanacheEntity implements Comparable<Serie> {
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
    @Temporal(TemporalType.TIMESTAMP)
    public Date created;

    @Column(name = "tv_show")
    public Boolean tvShow;

    public Serie() {
        this.created = new Date();
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

    public boolean isEmpty() {
        return 0 == id;
    }

    public String getGenreName() {
        return null == genre ? "<None>" : (genre.isEmpty() ? "<None>" : genre.name);
    }

    public static Serie create() {
        Serie serie = new Serie();
        serie.created = new Date();
        serie.displayOrder = 9000;
        serie.genre = Genre.findById(0l);

        return serie;
    }
}
