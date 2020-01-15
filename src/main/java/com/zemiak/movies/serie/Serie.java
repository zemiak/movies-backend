package com.zemiak.movies.serie;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.genre.Genre;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "serie")
public class Serie extends PanacheEntity implements Serializable, Comparable<Serie> {
    private static final long serialVersionUID = 4L;

    @Id
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Long id;

    @Size(max = 128, min = 1)
    @Column(name = "name")
    @NotNull
    private String name;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    private String pictureFileName;

    @Column(name = "display_order")
    @NotNull
    private Integer displayOrder;

    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @NotNull
    private Genre genre;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "tv_show")
    private Boolean tvShow;

    public Serie() {
        this.created = new Date();
        this.tvShow = Boolean.FALSE;
    }

    public Serie(Long id) {
        this();
        this.id = id;
    }

    public void copyFrom(Serie entity) {
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setDisplayOrder(entity.getDisplayOrder());
        this.setPictureFileName(entity.getPictureFileName());
        this.setGenre(entity.getGenre());
        this.setTvShow(entity.isTvShow());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureFileName() {
        return pictureFileName;
    }

    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
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
        return getName();
    }

    @Override
    public int compareTo(Serie o) {
        if (null == displayOrder && null != o.getDisplayOrder()) {
            return -1;
        }

        if (null != displayOrder && null == o.getDisplayOrder()) {
            return 1;
        }

        if (null == displayOrder && null == o.getDisplayOrder()) {
            return 0;
        }

        return displayOrder.compareTo(o.getDisplayOrder());
    }

    public String computeGenreName() {
        return null == genre ? "<None>" : (0 == genre.getId() ? "<None>" : genre.getName());
    }

    public static Serie create() {
        Serie serie = new Serie();
        serie.setCreated(new Date());
        serie.setDisplayOrder(9000);
        serie.setGenre(Genre.findById(0));

        return serie;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean isTvShow() {
        return tvShow;
    }

    public Boolean getTvShow() {
        return tvShow;
    }

    public void setTvShow(Boolean tvShow) {
        this.tvShow = tvShow;
    }

    public static List<Serie> findByGenre(Genre genre) {
        return list("genre", genre);
    }
}
