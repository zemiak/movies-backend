package com.zemiak.movies.genre;

import java.text.SimpleDateFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class GenreDTO {
    private Long id;
    private String name;
    private String pictureFileName;
    private Integer displayOrder;
    private String created;

    public GenreDTO() {
    }

    public GenreDTO(PanacheEntityBase pe) {
        Genre source = (Genre) pe;
        id = source.getId();
        name = source.getName();
        pictureFileName = source.getPictureFileName();
        displayOrder = source.getDisplayOrder();
        created = null == source.getCreated() ? "" : new SimpleDateFormat("yyyy-MM-dd").format(source.getCreated());
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
