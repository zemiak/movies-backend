package com.zemiak.movies.metadata;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.batch.PrepareMovieFileList;

@Dependent
public class MetadataService {
    @Inject NewMoviesCreator creator;
    @Inject DescriptionsUpdater descUpdater;
    @Inject ThumbnailDownloader thumbnails;
    @Inject PrepareMovieFileList movieFileList;
    @Inject YearUpdater years;
    // @Inject WebPageScraper scraper;

    public void process() {
//        scraper.process(movieFileList.getFiles());
        creator.process(movieFileList.getFiles());
        descUpdater.process(movieFileList.getFiles());
        thumbnails.process(movieFileList.getFiles());
        years.process(movieFileList.getFiles());
    }
}
