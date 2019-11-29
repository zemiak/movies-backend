package com.zemiak.movies.batch;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.batch.logs.SendLogFile;
import com.zemiak.movies.infuse.InfuseService;
import com.zemiak.movies.lookup.ConfigurationProvider;
import com.zemiak.movies.metadata.MetadataService;

import io.quarkus.scheduler.Scheduled;

@RequestScoped
@Transactional
public class UpdateMoviesScheduler {
    private static final Logger LOG = Logger.getLogger(UpdateMoviesScheduler.class.getName());
    private static final BatchLogger LOG1 = BatchLogger.getLogger(UpdateMoviesScheduler.class.getName());

    @Inject SendLogFile logFileMailer;
    @Inject RefreshStatistics stats;
    @Inject InfuseService infuseService;
    @Inject MetadataService metadataService;

    @Scheduled(cron = "0 15 03 * * ?")
    public void startScheduled() {
        if (ConfigurationProvider.isDevelopmentSystem()) {
            LOG.log(Level.INFO, "Scheduled batch update cancelled, a development system is in use.");
            return;
        }


        start();
    }

    public void start() {
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
    }
}
