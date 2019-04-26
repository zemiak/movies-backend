package com.zemiak.movies.batch.infuse;

import com.zemiak.movies.batch.service.lists.PrepareMovieFileList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class InfuseService {
    @Inject BasicInfuseFolderStructureCreator basic;
    @Inject InfuseMovieWriter writer;
    @Inject PrepareMovieFileList movieFileList;

    public void process() {
        basic.cleanAndCreate();
        writer.process(movieFileList.getFiles());
    }
}
