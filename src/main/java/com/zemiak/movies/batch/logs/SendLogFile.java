package com.zemiak.movies.batch.logs;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.config.ConfigurationProvider;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@Dependent
public class SendLogFile {
    private static final String TEXT_PLAIN = "text/plain";

    private static final Logger LOG = Logger.getLogger(SendLogFile.class.getName());

    private final String mailSubject = "Movies " + ConfigurationProvider.getSystemName() + ": Batch Results";
    private final String mailTo = ConfigurationProvider.getMailTo();
    private final String mailFrom = "noreply@movies-" + ConfigurationProvider.getSystemName().toLowerCase()
            + ".zemiakbox.com";

    @Inject
    Mailer mailer;

    public void send() {
        if (ConfigurationProvider.isDevelopmentSystem()) {
            return;
        }

        final File file = new File(BatchLogger.getLogFileName());
        if (!file.exists()) {
            LOG.log(Level.INFO, "Log file does not exist, not sending...");
            return;
        }

        if (file.length() == 0) {
            LOG.log(Level.INFO, "Log file is empty, not sending...");
            return;
        }

        try {
            sendLogFile();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error mailing the log file", ex);
        }
    }

    private void sendLogFile() throws Exception {
        mailer.send(Mail
            .withText(mailTo, mailSubject, "Batch run ended on " + new Date())
            .setFrom(mailFrom)
            .addAttachment("batch.log", new File(BatchLogger.getLogFileName()), TEXT_PLAIN)
        );
    }
}
