package com.zemiak.movies.genre;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "genre")
public class Genre extends PanacheEntity implements Serializable, Comparable<Genre> {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Column(name = "protected")
    private Integer protectedGenre;

    @Column(name = "picture_file_name")
    @Size(max = 512)
    private String pictureFileName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public static Genre create() {
        Genre genre = new Genre();
        genre.setCreated(new Date());
        genre.setDisplayOrder(9000);

        return genre;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Genre() {
        this.created = new Date();
    }

    public Genre(Integer id) {
        this();
        this.id = id;
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void copyFrom(Genre entity) {
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setDisplayOrder(entity.getDisplayOrder());
        this.setPictureFileName(entity.getPictureFileName());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public boolean isEmpty() {
        return id == 0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Genre o) {
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
}
