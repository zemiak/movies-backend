package com.zemiak.movies.batch;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Path;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.batch.logs.SendLogFile;
import com.zemiak.movies.infuse.InfuseService;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.metadata.MetadataService;


@RequestScoped
@Transactional
@Path("jobs")
public class UpdateMoviesScheduler {
    private static final Logger LOG = Logger.getLogger(UpdateMoviesScheduler.class.getName());
    private static final BatchLogger LOG1 = BatchLogger.getLogger(UpdateMoviesScheduler.class.getName());

    @Inject SendLogFile logFileMailer;
    @Inject RefreshStatistics stats;
    @Inject Event<CacheClearEvent> clearEvent;
    @Inject InfuseService infuseService;
    @Inject MetadataService metadataService;

    @Path("update-movies")
    public void startScheduled() {
        if (ConfigurationProvider.isDevelopmentSystem()) {
            LOG.log(Level.INFO, "Scheduled batch update cancelled, a development system is in use.");
            return;
        }

        BatchLogger.deleteLogFile();

        try {
            stats.reset();

            metadataService.process();
            infuseService.process();

            stats.dump();
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG1.log(Level.SEVERE, "Exception running movies update batch " + ex.getMessage(), ex);
        }

        logFileMailer.send();
        clearEvent.fire(new CacheClearEvent());
    }
}
