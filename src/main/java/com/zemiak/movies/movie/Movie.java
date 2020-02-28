package com.zemiak.movies.movie;

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
import com.zemiak.movies.language.Language;
import com.zemiak.movies.scraper.Csfd;
import com.zemiak.movies.scraper.Imdb;
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Movie extends PanacheEntity implements Comparable<Movie> {
    @Size(max = 512)
    @Column(name = "file_name")
    public String fileName;

    @Size(max = 128, min = 1)
    @Column(name = "name")
    @NotNull
    public String name;

    @Size(max = 128)
    @Column(name = "original_name")
    public String originalName;

    @Size(max = 128)
    @Column(name = "url")
    public String url;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    public String pictureFileName;

    @Column(name = "display_order")
    public Integer displayOrder;

    @Size(max = 2147483647)
    @Column(name = "description", length = 16384)
    public String description;

    @JoinColumn(name = "serie_id", referencedColumnName = "id")
    @ManyToOne
    public Serie serie;

    @JoinColumn(name = "subtitles", referencedColumnName = "id")
    @ManyToOne
    public Language subtitles;

    @JoinColumn(name = "original_language", referencedColumnName = "id")
    @ManyToOne
    public Language originalLanguage;

    @JoinColumn(name = "language", referencedColumnName = "id")
    @ManyToOne
    public Language language;

    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @NotNull
    public Genre genre;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date created;

    @Column(name = "year")
    public Integer year;

    @Column(name = "web_page", length = 128)
    public String webPage;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Movie() {
        this.created = new Date();
    }

    public Movie(Long id) {
        this();
        this.id = id;
    }

    public void copyFrom(Movie entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
        this.description = entity.description;
        this.fileName = entity.fileName;
        this.genre = entity.genre;
        this.language = entity.language;
        this.originalLanguage = entity.originalLanguage;
        this.originalName = entity.originalName;
        this.serie = entity.serie;
        this.subtitles = entity.subtitles;
        this.url = entity.url;
        this.year = entity.year;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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
        return genre.name;
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
        return null == serie ? "<None>" : (serie.isEmpty() ? "<None>" : serie.name);
    }

    public String getLanguageName() {
        return null == language ? "<None>" : language.name;
    }

    public String getOriginalLanguageName() {
        return null == originalLanguage ? "<None>" : originalLanguage.name;
    }

    public String getSubtitlesName() {
        return null == subtitles ? "<None>" : subtitles.name;
    }

    public boolean isEmptySerie() {
        return null == serie || serie.id == 0;
    }

    @Override
    public int compareTo(Movie o) {
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

    public String getGenreName() {
        return null == genre ? "<None>" : (genre.isEmpty() ? "<None>" : genre.name);
    }

    public static Movie create() {
        Movie movie = new Movie();
        movie.setCreated(new Date());

        return movie;
    }
}
