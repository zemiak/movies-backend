package com.zemiak.movies.language;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDTO {
    private String lang;
    private String name;
    private Integer displayOrder;
    private String created;

    public LanguageDTO() {
    }

    public LanguageDTO(Language source) {
        lang = source.getLang();
        name = source.getName();
        displayOrder = source.getDisplayOrder();
        created = null == source.getCreated() ? "" : new SimpleDateFormat("yyyy-MM-dd").format(source.getCreated());
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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
}
