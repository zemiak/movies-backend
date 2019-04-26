package com.zemiak.movies.service.ui.play;

import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.SerieService;
import com.zemiak.movies.service.ui.admin.JsfMessages;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("serieViewForm")
public class SerieViewForm implements Serializable {
    @Inject private SerieService service;
    @Inject private GenreViewForm genreWebService;

    private Integer id;
    private Serie bean;

    public List<Serie> getByGenre() {
        if (-1 == genreWebService.getId() || -2 == genreWebService.getId()) {
            return Collections.EMPTY_LIST;
        }

        return service
                .all()
                .stream()
                .filter(serie -> genreWebService.getId().equals(serie.getGenre().getId()))
                .sorted()
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String check() {
        if (null != id) {
            bean = service.find(id);
        }

        if (null == id || null == bean) {
            JsfMessages.addErrorMessage("Serie #" + id + " cannot be found");
            return "index";
        }

        return null;
    }

    public Serie getBean() {
        return bean;
    }
}
