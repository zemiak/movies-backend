package com.zemiak.movies.domain;

import com.zemiak.movies.service.scraper.Csfd;
import com.zemiak.movies.service.scraper.Imdb;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "movie", schema="data")
@NamedQueries({
    @NamedQuery(name = "Movie.findByGenreWithoutSerie", query = "SELECT m FROM Movie m WHERE m.genre = :genre AND (m.serie IS NULL OR m.serie.id = 0) ORDER BY m.name"),
    @NamedQuery(name = "Movie.findAll", query = "SELECT m FROM Movie m ORDER BY m.id"),
    @NamedQuery(name = "Movie.findById", query = "SELECT m FROM Movie m WHERE m.id = :id"),
    @NamedQuery(name = "Movie.findByIdDesc", query = "SELECT m FROM Movie m ORDER BY m.id DESC"),
    @NamedQuery(name = "Movie.findByFileName", query = "SELECT m FROM Movie m WHERE m.fileName = :fileName"),
    @NamedQuery(name = "Movie.findByName", query = "SELECT m FROM Movie m WHERE m.name = :name"),
    @NamedQuery(name = "Movie.findByOriginalName", query = "SELECT m FROM Movie m WHERE m.originalName = :originalName"),
    @NamedQuery(name = "Movie.findByUrl", query = "SELECT m FROM Movie m WHERE m.url = :url"),
    @NamedQuery(name = "Movie.findByPictureFileName", query = "SELECT m FROM Movie m WHERE m.pictureFileName = :pictureFileName"),
    @NamedQuery(name = "Movie.findByDisplayOrder", query = "SELECT m FROM Movie m WHERE m.displayOrder = :displayOrder"),
    @NamedQuery(name = "Movie.findByDescription", query = "SELECT m FROM Movie m WHERE m.description = :description")})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Movie implements Serializable, Comparable<Movie> {
    private static final long serialVersionUID = 3L;

    @Id
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1, schema = "data")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Integer id;

    @Size(max = 512)
    @Column(name = "file_name")
    private String fileName;

    @Size(max = 128, min = 1)
    @Column(name = "name")
    @NotNull
    private String name;

    @Size(max = 128)
    @Column(name = "original_name")
    private String originalName;

    @Size(max = 128)
    @Column(name = "url")
    private String url;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    private String pictureFileName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "serie_id", referencedColumnName = "id")
    @ManyToOne
    private Serie serie;

    @JoinColumn(name = "subtitles", referencedColumnName = "id")
    @ManyToOne
    private Language subtitles;

    @JoinColumn(name = "original_language", referencedColumnName = "id")
    @ManyToOne
    private Language originalLanguage;

    @JoinColumn(name = "language", referencedColumnName = "id")
    @ManyToOne
    private Language language;

    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @NotNull
    private Genre genre;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "year")
    private Integer year;

    @Transient
    private String webPage;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Movie() {
        this.created = new Date();
    }

    public Movie(Integer id) {
        this();
        this.id = id;
    }

    public void copyFrom(Movie entity) {
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setDisplayOrder(entity.getDisplayOrder());
        this.setPictureFileName(entity.getPictureFileName());
        this.setDescription(entity.getDescription());
        this.setFileName(entity.getFileName());
        this.setGenre(entity.getGenre());
        this.setLanguage(entity.getLanguage());
        this.setOriginalLanguage(entity.getOriginalLanguage());
        this.setOriginalName(entity.getOriginalName());
        this.setSerie(entity.getSerie());
        this.setSubtitles(entity.getSubtitles());
        this.setUrl(entity.getUrl());
        this.setYear(entity.getYear());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Language getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(Language subtitles) {
        this.subtitles = subtitles;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String composeGenreName() {
        return getGenre().getName();
    }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", name=" + name + '}';
    }

    public boolean isDescriptionEmpty() {
        return null == description || "".equals(description.trim()) || "''".equals(description.trim());
    }

    public boolean isUrlEmpty() {
        return null == url || "".equals(url.trim()) || "''".equals(url.trim());
    }

    public String getUrlFlag() {
        if (new Csfd().accepts(this)) {
            return "CSFD";
        } else if (new Imdb().accepts(this)) {
            return "IMDB";
        }

        return "";
    }

    public String getSerieName() {
        return null == serie ? "<None>" : (serie.isEmpty() ? "<None>" : serie.getName());
    }

    public String getLanguageName() {
        return null == language ? "<None>" : language.getName();
    }

    public String getOriginalLanguageName() {
        return null == originalLanguage ? "<None>" : originalLanguage.getName();
    }

    public String getSubtitlesName() {
        return null == subtitles ? "<None>" : subtitles.getName();
    }

    public boolean isEmptySerie() {
        return null == serie || serie.getId() == 0;
    }

    @Override
    public int compareTo(Movie o) {
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

    public String getGenreName() {
        return null == genre ? "<None>" : (genre.isEmpty() ? "<None>" : genre.getName());
    }

    public static Movie create() {
        Movie movie = new Movie();
        movie.setCreated(new Date());

        return movie;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }
}
