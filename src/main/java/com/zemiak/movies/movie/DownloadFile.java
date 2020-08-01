package com.zemiak.movies.movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

public class DownloadFile {
    public Response download(URL url, Path path) {
        DownloadClient client = RestClientBuilder.newBuilder().baseUrl(url).build(DownloadClient.class);
        Response response = client.download();
        if (200 != response.getStatus()) {
            return Response.status(Status.BAD_GATEWAY).entity("Provided url cannot be downloaded").build();
        }

        InputStream stream = response.readEntity(InputStream.class);
        try {
            Files.write(path, stream.readAllBytes());
        } catch (IOException e) {
            return Response.serverError().entity("Cannot write provided file").build();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // pass
            }

            response.close();
        }

        return Response.ok().build();
    }
}
