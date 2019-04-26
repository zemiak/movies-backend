package com.zemiak.movies.domain;

import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieDTO {
    private Integer id;
    private String fileName;
    private String name;
    private String originalName;
    private String url;
    private String pictureFileName;
    private Integer displayOrder;
    private String description;
    private String subtitles;
    private String originalLanguage;
    private String language;
    private String genre;
    private String serie;
    private String created;
    private Integer year;

    public MovieDTO() {
    }

    public MovieDTO(Movie source) {
        id = source.getId();
        fileName = source.getFileName();
        name = source.getName();
        originalLanguage = source.getOriginalLanguageName();
        url = source.getUrl();
        pictureFileName = source.getPictureFileName();
        displayOrder = null == source.getDisplayOrder() ? Integer.MAX_VALUE : source.getDisplayOrder();
        description = source.getDescription();
        serie = source.getSerieName();
        subtitles = source.getSubtitlesName();
        originalLanguage = source.getOriginalLanguageName();
        language = source.getLanguageName();
        genre = source.getGenreName();
        created = null == source.getCreated() ? "" : new SimpleDateFormat("yyyy-MM-dd").format(source.getCreated());
        year = null == source.getYear() ? 1970 : source.getYear();
    }
}
