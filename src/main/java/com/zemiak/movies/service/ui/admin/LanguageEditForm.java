package com.zemiak.movies.service.ui.admin;

import com.zemiak.movies.domain.Language;
import com.zemiak.movies.service.LanguageService;
import java.io.Serializable;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("languageEditForm")
public class LanguageEditForm implements Serializable {
    private String id;
    private Language bean;
    private String code;

    @Inject
    private LanguageService service;

    public LanguageEditForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String check() {
        bean = isNew() ? Language.create() : service.find(id);

        if (null == bean) {
            JsfMessages.addErrorMessage("Cannot find language #" + id);
            return close();
        }

        code = bean.getId();

        return null;
    }

    public Language getBean() {
        return bean;
    }


    public String save() {
        bean.setId(code);
        service.save(bean);
        JsfMessages.addSuccessMessage("The language has been saved");

        return close();
    }

    public String close() {
        return "index";
    }

    public String remove() {
        try {
            service.remove(id);
            JsfMessages.addSuccessMessage("The language has been deleted");
        } catch (EJBException ex) {
            JsfMessages.addErrorMessage(ex);
        }

        return close();
    }

    public String getActionTitle() {
        return isNew() ? "Create" : "Edit";
    }

    public boolean isNew() {
        return "-1".equals(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
