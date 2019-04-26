package com.zemiak.movies.service.ui.admin.resource;

import com.zemiak.movies.domain.DataTablesAjaxData;
import com.zemiak.movies.domain.LanguageDTO;
import com.zemiak.movies.service.LanguageService;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("languages")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class LanguagesResource {
    @Inject
    private LanguageService languages;

    @GET
    public DataTablesAjaxData<LanguageDTO> getAllMovies() {
        return new DataTablesAjaxData<>(languages.all().stream().map(language -> new LanguageDTO(language)).collect(Collectors.toList()));
    }
}
