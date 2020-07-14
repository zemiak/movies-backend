package com.zemiak.movies.ui;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FileUploadForm {
    private byte[] filedata;
    private Long id;

    public FileUploadForm() {}

    public byte[] getFileData() {
        return filedata;
    }

    public Long getId() {
        return id;
    }

    @FormParam("file")
    @PartType("application/octet-stream")
    public void setFileData(final byte[] filedata) {
        this.filedata = filedata;
    }

    @FormParam("id")
    @PartType("text/plain")
    public void setId(String idString) {
        this.id = Long.parseLong(idString);
    }
}
