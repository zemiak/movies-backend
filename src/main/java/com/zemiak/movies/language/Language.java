package com.zemiak.movies.language;

import java.time.LocalDateTime;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@JsonbNillable
public class Language extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

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
    public LocalDateTime created;

    public JsonObject toJson() {
        JsonObjectBuilder builder = NullAwareJsonObjectBuilder.create()
            .add("id", this.id)
            .add("code", this.code)
            .add("name", this.name)
            .add("pictureFileName", this.pictureFileName)
            .add("displayOrder", this.displayOrder);
        NullAwareJsonObjectBuilder.addDate(builder, "created", this.created);

        return builder.build();
    }

    public Language() {
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

    @JsonbTransient
    public boolean isNone() {
        return "  ".equals(code);
    }

    public static Language create() {
        Language lang = new Language();
        lang.created = LocalDateTime.now();
        lang.displayOrder = 9000;

        return lang;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
