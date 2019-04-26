package com.zemiak.movies.batch.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

public class Listener implements JobListener, StepListener {
    private static final Logger LOG = Logger.getLogger(Listener.class.getName());

    @Inject
    private JobContext jobCtx;

    @Inject
    private StepContext stepCtx;

    @Override
    public void beforeJob() throws Exception {
        LOG.log(Level.INFO, "Job starting {0}", jobCtx.getJobName());
    }

    @Override
    public void afterJob() {
        LOG.log(Level.INFO, "Job finished {0}", jobCtx.getJobName());
    }

    @Override
    public void beforeStep() throws Exception {
        LOG.log(Level.INFO, "Start step {0}", stepCtx.getStepName());
    }

    @Override
    public void afterStep() throws Exception {
        LOG.log(Level.INFO, "End step {0}", stepCtx.getStepName());
    }
}
