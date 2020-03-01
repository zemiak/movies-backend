package com.zemiak.movies.language;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Language extends PanacheEntity {
    @Basic(optional = false)
    @NotNull
    @Column(name = "code")
    public String code;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    public String name;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    public String pictureFileName;

    @Column(name = "display_order")
    public Integer displayOrder;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date created;

    public Language() {
        this.created = new Date();
    }

    public Language(Long id) {
        this();
        this.id = id;
    }

    public void copyFrom(Language entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.displayOrder = entity.displayOrder;
        this.pictureFileName = entity.pictureFileName;
    }

    public Language(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Language)) {
            return false;
        }
        Language other = (Language) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isNone() {
        return "  ".equals(code);
    }

    public static Language create() {
        Language lang = new Language();
        lang.created = new Date();
        lang.displayOrder = 9000;

        return lang;
    }
}
