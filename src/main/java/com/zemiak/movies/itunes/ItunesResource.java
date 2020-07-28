package com.zemiak.movies.itunes;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.strings.Encodings;

@RequestScoped
@Path("metadata/itunes")
@Produces(MediaType.APPLICATION_JSON)
public class ItunesResource {
    @Inject
    ItunesArtworkService service;

    @GET
    @Path("{pattern}")
    public Set<ItunesArtwork> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        String patternAscii = Encodings.toAscii(pattern.trim().toLowerCase());
        return service.getMovieArtworkResults(patternAscii);
    }
}
