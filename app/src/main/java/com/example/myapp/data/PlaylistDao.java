package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    long insert(Playlist playlist);

    @Query("UPDATE Playlist SET songs=:songs WHERE playlist_name=:name AND username=:username")
    void update_playlist(String songs, String name, String username);

    @Query("DELETE FROM Playlist WHERE playlist_name=:name and username=:username")
    void delete_playlist_allsongs(String name, String username);

    @Query("SELECT * FROM Playlist")
    List<Playlist> getAll();

    @Query("SELECT * FROM Playlist WHERE playlist_name=:name AND username=:username")
    List<Playlist> getSpecificPlaylist(String name, String username);

    @Query("SELECT * FROM Playlist WHERE username=:name")
    List<Playlist> getPlaylistsForUser(String name);

    @Query("SELECT * FROM Playlist WHERE username=:name")
    LiveData<List<Playlist>> getPlaylistsForUserLive(String name);

    @Query("SELECT * FROM Playlist")
    LiveData<List<Playlist>> getAllLiveData();

    @Query("SELECT * FROM Playlist WHERE username=:username")
    LiveData<List<Playlist>> getAllLiveDataForUser(String username);

}