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
@Table(name = "serie", schema="data")
@NamedQueries({
    @NamedQuery(name = "Serie.findAll", query = "SELECT s FROM Serie s ORDER BY s.genre, s.displayOrder"),
    @NamedQuery(name = "Serie.findById", query = "SELECT s FROM Serie s WHERE s.id = :id"),
    @NamedQuery(name = "Serie.findByName", query = "SELECT s FROM Serie s WHERE s.name = :name"),
    @NamedQuery(name = "Serie.findByPictureFileName", query = "SELECT s FROM Serie s WHERE s.pictureFileName = :pictureFileName"),
    @NamedQuery(name = "Serie.findByDisplayOrder", query = "SELECT s FROM Serie s WHERE s.displayOrder = :displayOrder"),
    @NamedQuery(name = "Serie.findByGenre", query = "SELECT s FROM Serie s WHERE s.genre = :genre")})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Serie implements Serializable, Comparable<Serie> {
    private static final long serialVersionUID = 4L;

    @Id
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1, schema = "data")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Integer id;

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

    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY)
    @XmlTransient
    private List<Movie> movieList;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "tv_show")
    private Boolean tvShow;

    public Serie() {
        this.created = new Date();
        this.tvShow = Boolean.FALSE;
    }

    public Serie(Integer id) {
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

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
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

    public boolean isEmpty() {
        return 0 == id;
    }

    public String getGenreName() {
        return null == genre ? "<None>" : (genre.isEmpty() ? "<None>" : genre.getName());
    }

    public static Serie create(EntityManager em) {
        Serie serie = new Serie();
        serie.setCreated(new Date());
        serie.setMovieList(new ArrayList<>());
        serie.setDisplayOrder(9000);
        serie.setGenre(em.find(Genre.class, 0));

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
}
