package com.zemiak.movies.batch.logs;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;

import com.zemiak.movies.lookup.ConfigurationProvider;

@Dependent
public class SendLogFile {
    private static final Logger LOG = Logger.getLogger(SendLogFile.class.getName());

//    @Resource(name = "mail/movies")
//    private Session mailSession;

    private final String mailSubject = "Movies " + ConfigurationProvider.getSystemName() + ": Batch Results";
    private final String mailTo = ConfigurationProvider.getMailTo();
    private final String mailFrom = "noreply@movies-" + ConfigurationProvider.getSystemName().toLowerCase() + ".zemiakbox.com";

    public void send() {
        if (ConfigurationProvider.isDevelopmentSystem()) {
            return;
        }

        final File file = new File(BatchLogger.getLogFileName());
        if (! file.exists()) {
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
//        final Message message = new MimeMessage(mailSession);
//        message.setFrom(new InternetAddress(mailFrom));
//        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
//        message.setSubject(mailSubject);
//        message.setText("Batch run ended on " + new Date());
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        Multipart multipart = new MimeMultipart();
//
//        DataSource source = new FileDataSource(BatchLogger.getLogFileName());
//        messageBodyPart.setDataHandler(new DataHandler(source));
//        messageBodyPart.setFileName("batch.log");
//        multipart.addBodyPart(messageBodyPart);
//
//        message.setContent(multipart);
//
//        Transport.send(message);
//
//        LOG.log(Level.INFO, "Sent LOG file to {0}", mailTo);
    }
}
