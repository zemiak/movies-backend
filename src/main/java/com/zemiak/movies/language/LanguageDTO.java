package com.zemiak.movies.language;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDTO {
    private Integer id;
    private String name;
    private String code;
    private Integer displayOrder;
    private String created;

    public LanguageDTO() {
    }

    public LanguageDTO(Language source) {
        id = source.getId();
        name = source.getName();
        code = source.getCode();
        displayOrder = source.getDisplayOrder();
        created = null == source.getCreated() ? "" : new SimpleDateFormat("yyyy-MM-dd").format(source.getCreated());
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
