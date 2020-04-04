package com.zemiak.movies.genre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import javax.inject.Inject;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ui.GuiDTO;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;


@QuarkusTest
public class GenreUIServiceTest {
    AssuredRequests req;

    @Inject
    GenreUIService cut;

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
        List<Genre> genres = req.get("/genres/all").jsonPath().getList("$", Genre.class);
        assertEquals(root.size(), genres.size() + 3, "Root genres are all genres, size must be the same. Artificial: unassigned, fresh, recently added");
    }

    @Test
    public void getByExpression() {
        List<GuiDTO> res = cut.getByExpression("one");
        assertEquals(1, res.size(), "There is None genre");
    }
}
