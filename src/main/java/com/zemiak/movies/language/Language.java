package com.zemiak.movies.language;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "language")
@NamedQueries({
    @NamedQuery(name = "Language.findAll", query = "SELECT l FROM Language l ORDER BY l.name"),
    @NamedQuery(name = "Language.findById", query = "SELECT l FROM Language l WHERE l.id = :id"),
    @NamedQuery(name = "Language.findByName", query = "SELECT l FROM Language l WHERE l.name = :name"),
    @NamedQuery(name = "Language.findByPictureFileName", query = "SELECT l FROM Language l WHERE l.pictureFileName = :pictureFileName"),
    @NamedQuery(name = "Language.findByDisplayOrder", query = "SELECT l FROM Language l WHERE l.displayOrder = :displayOrder")})
public class Language implements Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @SequenceGenerator(name="seq_global", sequenceName="seq_global", initialValue = 47000000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_global")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Size(max = 512)
    @Column(name = "picture_file_name")
    private String pictureFileName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Language() {
        this.created = new Date();
    }

    public Language(String id) {
        this();
        this.id = id;
    }

    public void copyFrom(Language entity) {
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setDisplayOrder(entity.getDisplayOrder());
        this.setPictureFileName(entity.getPictureFileName());
    }

    public Language(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return getName();
    }

    public boolean isNone() {
        return "  ".equals(id);
    }

    public static Language create() {
        Language lang = new Language();
        lang.setCreated(new Date());
        lang.setDisplayOrder(9000);

        return lang;
    }
}
