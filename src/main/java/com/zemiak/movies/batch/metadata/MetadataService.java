package com.zemiak.movies.batch.metadata;

import com.zemiak.movies.batch.service.lists.PrepareMovieFileList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MetadataService {
    @Inject NewMoviesCreator creator;
    @Inject MetadataRefresher refresher;
    @Inject DescriptionsUpdater descUpdater;
    @Inject ThumbnailCreator thumbnails;
    @Inject PrepareMovieFileList movieFileList;
    @Inject YearUpdater years;
    @Inject WebPageScraper scraper;

    public void process() {
//        scraper.process(movieFileList.getFiles());
        creator.process(movieFileList.getFiles());
        refresher.process(movieFileList.getFiles());
        descUpdater.process(movieFileList.getFiles());
        thumbnails.process(movieFileList.getFiles());
        years.process(movieFileList.getFiles());
    }
}
