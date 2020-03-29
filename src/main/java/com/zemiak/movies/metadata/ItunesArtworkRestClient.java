package com.zemiak.movies.metadata;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/WebObjects/MZStoreServices.woa/wa")
@RegisterRestClient
public interface ItunesArtworkRestClient {
    @Path("/wsSearch")
    @Produces("application/json")
    @GET
    Response wsSearch(@QueryParam("country") String country, @QueryParam("entity") String entity, @QueryParam("term") String term);
}
