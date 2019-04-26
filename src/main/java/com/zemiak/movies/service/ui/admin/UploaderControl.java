package com.zemiak.movies.service.ui.admin;

import com.zemiak.movies.service.ConfigurationProvider;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

@Dependent
public class UploaderControl implements Serializable {
    private static final int IMAGE_SIZE = 500*1024;

    private Part file;

    public void upload(String subfolder, String pictureFileName) {
        try {
            Path imagePath = Paths.get(ConfigurationProvider.getImgPath(), subfolder, pictureFileName);
            Files.copy(file.getInputStream(), imagePath);

            JsfMessages.addSuccessMessage("The image has been uploaded to " + imagePath.toString());
        } catch (IOException e) {
            JsfMessages.addErrorMessage(e);
        }
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public void validateFile(FacesContext ctx,
            UIComponent comp,
            Object value) {
        List<FacesMessage> msgs = new ArrayList<>();
        if (null == value) {
            return;
        }

        Part validatingFile = (Part) value;
        if (validatingFile.getSize() > IMAGE_SIZE) {
            msgs.add(new FacesMessage("File too big"));
        }

        if (!"image/jpeg".equals(validatingFile.getContentType().toLowerCase())) {
            msgs.add(new FacesMessage("Not a JPEG file"));
        }

        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }
}
