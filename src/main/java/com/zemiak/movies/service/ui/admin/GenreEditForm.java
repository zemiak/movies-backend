package com.zemiak.movies.service.ui.admin;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.service.GenreService;
import java.io.Serializable;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

@SessionScoped
@Named("genreEditForm")
public class GenreEditForm implements Serializable {
    private Integer id;
    private Genre bean;

    @Inject
    private GenreService service;

    @Inject
    private UploaderControl uploader;

    public GenreEditForm() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String check() {
        bean = isNew() ? Genre.create() : service.find(id);

        if (null == bean) {
            JsfMessages.addErrorMessage("Cannot find genre #" + id);
            return close();
        }

        return null;
    }

    public Genre getBean() {
        return bean;
    }

    public String save() {
        service.save(bean);
        JsfMessages.addSuccessMessage("The genre has been saved");

        return close();
    }

    public String close() {
        return "index";
    }

    public String remove() {
        try {
            service.remove(id);
            JsfMessages.addSuccessMessage("The genre has been deleted");
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

    public void upload() {
        uploader.upload("genre", bean.getPictureFileName());
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
}
