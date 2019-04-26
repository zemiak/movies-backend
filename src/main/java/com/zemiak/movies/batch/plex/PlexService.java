package com.zemiak.movies.batch.plex;

import com.zemiak.movies.batch.plex.movie.PlexMovieWriter;
import com.zemiak.movies.batch.plex.movie.PrepareMovieFileList;
import com.zemiak.movies.batch.plex.music.PlexMusicWriter;
import com.zemiak.movies.batch.plex.music.PrepareMusicFileList;
import com.zemiak.movies.batch.plex.photo.PlexPhotoWriter;
import com.zemiak.movies.batch.plex.photo.PreparePhotoFileList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PlexService {
    @Inject BasicPlexFolderStructureCreator plexFolders;
    @Inject PlexMovieWriter plexMovies;
    @Inject PlexMusicWriter plexMusic;
    @Inject PlexPhotoWriter plexPhotos;

    public void process(List<String> movieFileList, List<String> musicFileList, List<String> photosFileList) {
        plexFolders.cleanAndCreate();
        plexMovies.process(movieFileList);
        plexMusic.process(musicFileList);
        plexPhotos.process(photosFileList);
    }

    public void process() {
        PrepareMovieFileList movieList = new PrepareMovieFileList();
        movieList.init();

        PrepareMusicFileList musicList = new PrepareMusicFileList();
        musicList.init();

        PreparePhotoFileList photoList = new PreparePhotoFileList();
        photoList.init();

        process(movieList.getFiles(), musicList.getFiles(), photoList.getFiles());
    }
}
