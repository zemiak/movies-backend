package com.zemiak.movies.genre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ProvideConfiguration;
import com.zemiak.movies.movie.ForbiddenJpg;
import com.zemiak.movies.ui.GuiDTO;

import org.junit.jupiter.api.Test;

public class GenreUIServiceTest {
    AssuredRequests req;

    public GenreUIServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void getRootItemsNotEmpty() {
        List<GuiDTO> genres = req.get("/ui/root").jsonPath().getList("$", GuiDTO.class);
        assertFalse(genres.isEmpty(), "Root genres must not be empty");
    }

    @Test
    public void getRootItemsContainsAllGenres() {
        List<GuiDTO> root = req.get("/ui/root").jsonPath().getList("$", GuiDTO.class);
        List<Genre> genres = req.get("/genres/paged?page=0&pageSize=10").jsonPath().getList("$", Genre.class);
        assertEquals(root.size(), genres.size() + 3, "Root genres are all genres, size must be the same. Artificial: unassigned, fresh, recently added");
    }

    @Test
    public void getByExpression() {
        List<GuiDTO> res = req.get("/genres/search/on").jsonPath().getList("$", GuiDTO.class);
        assertFalse(res.isEmpty(), "There is None genre");
    }
}
