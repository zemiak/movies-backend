package com.zemiak.movies.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "genre", schema="data")
@NamedQueries({
    @NamedQuery(name = "Genre.findAll", query = "SELECT g FROM Genre g ORDER BY g.displayOrder"),
    @NamedQuery(name = "Genre.findById", query = "SELECT g FROM Genre g WHERE g.id = :id"),
    @NamedQuery(name = "Genre.findByName", query = "SELECT g FROM Genre g WHERE g.name = :name"),
    @NamedQuery(name = "Genre.findByPictureFileName", query = "SELECT g FROM Genre g WHERE g.pictureFileName = :pictureFileName"),
    @NamedQuery(name = "Genre.findByDisplayOrder", query = "SELECT g FROM Genre g WHERE g.displayOrder = :displayOrder"),
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Genre implements Serializable, Comparable<Genre> {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1, schema = "data")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Column(name = "picture_file_name")
    @Size(max = 512)
    private String pictureFileName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    @XmlTransient
    private List<Serie> serieList;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    @XmlTransient
    private List<Movie> movieList;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public static Genre create() {
        Genre genre = new Genre();
        genre.setCreated(new Date());
        genre.serieList = new ArrayList<>();
        genre.movieList = new ArrayList<>();
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

    @XmlTransient
    public List<Serie> getSerieList() {
        return serieList;
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

    @XmlTransient
    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
