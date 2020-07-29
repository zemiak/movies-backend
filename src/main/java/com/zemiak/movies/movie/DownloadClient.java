package com.zemiak.movies.movie;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface DownloadClient {
    @GET
    Response download();
}
