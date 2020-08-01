package com.zemiak.movies.csfd;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.scraper.UrlDTO;
import com.zemiak.movies.strings.Encodings;

@RequestScoped
@Path("metadata/csfd")
@Produces(MediaType.APPLICATION_JSON)
public class CsfdResource {
    @Inject
    Csfd service;

    @GET
    @Path("{pattern}")
    public List<JsonObject> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        String patternAscii = Encodings.toAscii(pattern.trim().toLowerCase());
        return service.getUrlCandidates(patternAscii).stream().map(UrlDTO::toJson).collect(Collectors.toList());
    }
}
