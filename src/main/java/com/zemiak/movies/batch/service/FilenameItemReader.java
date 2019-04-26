package com.zemiak.movies.batch.service;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("FilenameItemReader")
public class FilenameItemReader extends AbstractItemReader {
    private static final Logger LOG1 = Logger.getLogger(FilenameItemReader.class.getName());

    @Inject
    private JobContext jobCtx;

    private FileInputStream inputStream;
    private BufferedReader br;
    private int recordNumber;

    public FilenameItemReader() {
    }

    @Override
    public void open(Serializable prevCheckpointInfo) throws IOException {
        String fileList = jobCtx.getProperties().getProperty("fileList");

        inputStream = new FileInputStream(fileList);
        br = new BufferedReader(new InputStreamReader(inputStream));

        if (prevCheckpointInfo != null) {
            recordNumber = (Integer) prevCheckpointInfo;
        }

        for (int i = 1; i < recordNumber; i++) {   //Skip upto recordNumber
            br.readLine();
        }

        LOG1.log(Level.INFO, "Opened file list from record: {0}", recordNumber);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return recordNumber;
    }

    @Override
    public Object readItem() throws IOException {
        String line = br.readLine();

        return (null == line) ? null : line.trim();
    }
}
