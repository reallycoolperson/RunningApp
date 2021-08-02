package com.example.myapp.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class PlaylistRepository {
    private final ExecutorService executorService;

    private final PlaylistDao playlistDao;

    @Inject
    public PlaylistRepository(
            ExecutorService executorService,
            PlaylistDao playlistDao) {
        this.executorService = executorService;
        this.playlistDao = playlistDao;
    }

    public void insert(Playlist playlist) {
        executorService.submit(() -> playlistDao.insert(playlist));
    }

    public List<Playlist> getAll() {
        return playlistDao.getAll();
    }

    public LiveData<List<Playlist>> getAllLiveData() {
        return playlistDao.getAllLiveData();
    }

    public List<Playlist> getSpecificPlaylist(String name, String username) { return playlistDao.getSpecificPlaylist(name, username); }

    public void update_playlist(String songs, String name, String username){  playlistDao.update_playlist(songs, name, username); }

    public List<Playlist> getPlaylistsForUser(String name) { return playlistDao.getPlaylistsForUser(name); }

    public LiveData<List<Playlist>> getPlaylistsForUserLive(String name) { return playlistDao.getPlaylistsForUserLive(name); }

    public void delete_playlist_allsongs(String name, String username) {playlistDao.delete_playlist_allsongs(name, username); }

    public LiveData<List<Playlist>> getAllLiveDataForUser(String name)
        {
             return playlistDao.getAllLiveDataForUser(name);
        }



}
