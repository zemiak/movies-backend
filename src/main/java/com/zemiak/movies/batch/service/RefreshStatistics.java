package com.zemiak.movies.batch.service;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.service.ConfigurationProvider;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshStatistics {
    private BatchLogger LOG = null;

    private int created;
    private int updated;

    @PostConstruct
    public void reset() {
        created = 0;
        updated = 0;
    }

    public void dump() {
        dump(ConfigurationProvider.isDevelopmentSystem() || haveBeenChanged() ? Level.INFO : Level.FINE);
    }

    private void dump(Level level) {
        if (null == LOG) {
            LOG = BatchLogger.getLogger(RefreshStatistics.class.getName());
        }

        LOG.log(level, "Metadata Refresh Statistics", null);
        LOG.log(level, "Movies Created: {0}", created);
        LOG.log(level, "Movies Updated: {0}", updated);
    }

    public void incrementCreated() {
        created++;
    }

    public void incrementUpdated() {
        updated++;
    }

    boolean haveBeenChanged() {
        return created > 0 || updated > 0;
    }
}
