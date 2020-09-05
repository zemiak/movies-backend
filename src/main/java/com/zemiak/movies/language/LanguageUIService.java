package com.zemiak.movies.language;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.ui.VaadingGridPagingResult;

import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("languages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class LanguageUIService {
    @GET
    @Path("count")
    public JsonObject count() {
        long count = Language.count();
        return Json.createObjectBuilder().add("count", count).build();
    }

    @GET
    @Path("items")
    public VaadingGridPagingResult<Language> getItems(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        return new VaadingGridPagingResult<>(Language.count(), Language.findAll(Sort.by("displayOrder")).page(page, pageSize).list());
    }

    @GET
    @Path("search/{pattern}")
    public List<Language> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        return Language.find("UPPER(name) LIKE ?1", "%" + pattern.toUpperCase() + "%").list();
    }
}
