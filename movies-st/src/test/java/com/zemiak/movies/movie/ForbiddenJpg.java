package com.zemiak.movies.movie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ForbiddenJpg {
    private static String DATA = "hello";

    public static void write(final String fileName) throws IOException {
        File f = new File(fileName);
        FileWriter writer = new FileWriter(f);
        writer.write(DATA);
        writer.close();
    }

    public static boolean equalsTo(final String data, final String fileName) throws IOException {
        Files.delete(Paths.get(fileName));
        return data.equals(DATA);
    }
}
