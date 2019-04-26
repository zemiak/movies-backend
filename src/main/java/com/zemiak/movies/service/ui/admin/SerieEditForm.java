package com.zemiak.movies.service.ui.admin;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.GenreService;
import com.zemiak.movies.service.SerieService;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

@SessionScoped
@Named("serieEditForm")
public class SerieEditForm implements Serializable {
    private Integer id;
    private Serie bean;
    private Integer genreId;

    @Inject
    private SerieService service;

    @Inject
    private GenreService genres;

    @Inject
    private UploaderControl uploader;

    public SerieEditForm() {
    }

    public Integer getId() {
        return id;
    }

    public String check() {
        bean = isNew() ? Serie.create(service.getEm()) : service.find(id);

        if (null == bean) {
            JsfMessages.addErrorMessage("Cannot find serie #" + id);
            return close();
        }

        genreId = bean.getGenre().getId();

        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Serie getBean() {
        return bean;
    }

    public String save() {
        service.save(bean, genreId);
        JsfMessages.addSuccessMessage("The serie has been saved");

        return close();
    }

    public String close() {
        return "index";
    }

    public String remove() {
        try {
            service.remove(id);
            JsfMessages.addSuccessMessage("The serie has been deleted");
        } catch (EJBException ex) {
            JsfMessages.addErrorMessage(ex);
        }

        return close();
    }

    public String getActionTitle() {
        return isNew() ? "Create" : "Edit";
    }

    public boolean isNew() {
        return null == id || -1 == id;
    }

    public List<Genre> getGenres() {
        return genres.all();
    }

    public void upload() {
        uploader.upload("serie", bean.getPictureFileName());
    }

    public Part getFile() {
        return uploader.getFile();
    }

    public void setFile(Part file) {
        uploader.setFile(file);
    }

    public void validateFile(FacesContext ctx,
            UIComponent comp,
            Object value) {
        uploader.validateFile(ctx, comp, value);
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }
}
