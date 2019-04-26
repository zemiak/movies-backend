package com.zemiak.movies.service.ui.admin;

import com.zemiak.movies.batch.service.UpdateMoviesScheduler;
import com.zemiak.movies.domain.CacheClearEvent;
import com.zemiak.movies.service.*;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("systemForm")
public class SystemForm implements Serializable {
    @Inject GenreService genres;
    @Inject SerieService series;
    @Inject LanguageService languages;
    @Inject MovieService movies;
    @Inject Event<CacheClearEvent> clearCacheEvents;
    @Inject UpdateMoviesScheduler runner;

    private final Boolean developmentSystem = ConfigurationProvider.isDevelopmentSystem();

    public SystemForm() {
    }

    public String getTitleIndex() {
        return developmentSystem ? "Movies (" + ConfigurationProvider.getSystemName() + ")" : "Movies";
    }

    public String getTitleNew() {
        return developmentSystem ? "New Movies (" + ConfigurationProvider.getSystemName() + ")" : "New Movies";
    }

    public String getTitleSystem() {
        return developmentSystem ? "System (" + ConfigurationProvider.getSystemName() + ")" : "System";
    }

    public Integer getGenres() {
        return genres.all().size();
    }

    public Integer getSeries() {
        return series.all().size();
    }

    public Integer getLanguages() {
        return languages.all().size();
    }

    public Integer getAllMovies() {
        return movies.all().size();
    }

    public Integer getNewMovies() {
        return movies.findAllNew().size();
    }

    public void clearCache() {
        clearCacheEvents.fire(new CacheClearEvent());
        JsfMessages.addSuccessMessage("The cache has been cleared.");
    }

    public void runImport() {
        try {
            runner.start();
            JsfMessages.addSuccessMessage("Import has been successful");
        } catch (RuntimeException ex) {
            JsfMessages.addErrorMessage("Cannot restart Plex: " + ex.getMessage());
        }
    }
}
