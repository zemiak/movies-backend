package com.zemiak.movies.serie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response;

import com.zemiak.movies.AssuredRequests;
import com.zemiak.movies.ProvideConfiguration;
import com.zemiak.movies.movie.ForbiddenJpg;
import com.zemiak.movies.movie.MovieUIServiceTest;
import com.zemiak.movies.ui.GuiDTO;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SerieUIServiceTest {
    AssuredRequests req;

    public SerieUIServiceTest() {
        req = new AssuredRequests();
    }

    @Test
    public void getThumbnail() throws IOException {
        var e = new Serie();
        e.name = "Test";
        e.pictureFileName = "none.jpg";
        ForbiddenJpg.write(e.pictureFileName);

        SerieUIService svc = mock(SerieUIService.class);
        when(svc.find(0l)).thenReturn(e);
        when(svc.getThumbnail(0l)).thenCallRealMethod();

        ProvideConfiguration.init();

        Response r = svc.getThumbnail(0l);
        assertEquals(200, r.getStatus(), "Reply must be 200 OK, but is " + r.getStatus() + " - " + r.getEntity());

        FileInputStream stream = (FileInputStream) r.getEntity();
        assertNotNull(stream, "Stream must not be null");

        assertTrue(ForbiddenJpg.equalsTo(new String(stream.readAllBytes()), e.pictureFileName), "Must return our thumbnail data");
    }
}
