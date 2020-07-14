package com.zemiak.movies.ui;

import javax.ws.rs.FormParam;

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
    public void setFileData(final byte[] filedata) {
        this.filedata = filedata;
    }

    @FormParam("id")
    public void setId(String idString) {
        this.id = Long.parseLong(idString);
    }
}
