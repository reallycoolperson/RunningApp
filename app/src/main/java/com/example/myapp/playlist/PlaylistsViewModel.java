package com.example.myapp.playlist;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myapp.data.Playlist;
import com.example.myapp.data.PlaylistRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class PlaylistsViewModel extends ViewModel
{

    public int to_show=0;
    private PlaylistRepository playlistRepository;
    private MutableLiveData<List<String>> songs_to_show = new MutableLiveData<List<String>>()
    {
    };
    private MutableLiveData<List<String>> songs_to_show_workout = new MutableLiveData<List<String>>()
    {
    }; //pjesme koje se trenutno slusaju
    private int current_index_song_playing = 0;
    private List<Playlist> playlists_to_show = new LinkedList<Playlist>();
    private String chosen_song = "";
    private int chosen_song_index = -1;
    private AddToPlaylistDialog add_dialog;
    private int music_shown = 0;
    private String login_username = "";
    private String current_songs_on_phone = "";
    private String plus_or_minus = "plus";
    private String current_playlist = "";
    private String current_playlist_workout = "";
    private String current_song_playing = ""; //bez .mp3
    private int current_song_duration = 0;

    public int getTo_show()
    {
        return to_show;
    }

    public void setTo_show(int to_show)
    {
        this.to_show = to_show;
    }

    public int getCurrent_song_duration()
    {
        return current_song_duration;
    }

    public void setCurrent_song_duration(int current_song_duration)
    {
        this.current_song_duration = current_song_duration;
    }

    public String getCurrent_song_playing()
    {
        return current_song_playing;
    }

    public void setCurrent_song_playing(String current_song_playing)
    {
        this.current_song_playing = current_song_playing;
    }

    public int getCurrent_index_song_playing()
    {
        return current_index_song_playing;
    }

    public void setCurrent_index_song_playing(int current_index_song_playing)
    {
        this.current_index_song_playing = current_index_song_playing;
    }

    public String getCurrent_playlist_workout()
    {
        return current_playlist_workout;
    }

    public void setCurrent_playlist_workout(String current_playlist_workout)
    {
        this.current_playlist_workout = current_playlist_workout;
    }

    public String getCurrent_playlist()
    {
        return current_playlist;
    }

    public void setCurrent_playlist(String current_playlist)
    {
        this.current_playlist = current_playlist;
    }

    public String getPlus_or_minus()
    {
        return plus_or_minus;
    }

    public void setPlus_or_minus(String plus_or_minus)
    {
        this.plus_or_minus = plus_or_minus;
    }

    public String getCurrent_songs_on_phone()
    {
        return current_songs_on_phone;
    }

    public void setCurrent_songs_on_phone(String current_songs_on_phone)
    {
        this.current_songs_on_phone = current_songs_on_phone;
    }

    public String getLogin_username()
    {
        return login_username;
    }

    public void setLogin_username(String login_username)
    {
        this.login_username = login_username;
    }

    public int getMusic_shown()
    {
        return music_shown;
    }

    public void setMusic_shown(int music_shown)
    {
        this.music_shown = music_shown;
    }

    @ViewModelInject
    public PlaylistsViewModel(
            PlaylistRepository playlistRepository,
            @Assisted SavedStateHandle savedStateHandle)
    {
        this.playlistRepository = playlistRepository;
    }

    public AddToPlaylistDialog getAdd_dialog()
    {
        return add_dialog;
    }

    public void setAdd_dialog(AddToPlaylistDialog add_dialog)
    {
        this.add_dialog = add_dialog;
    }

    public int getChosen_song_index()
    {
        return chosen_song_index;
    }

    public void setChosen_song_index(int chosen_song_index)
    {
        this.chosen_song_index = chosen_song_index;
    }

    public String getChosen_song()
    {
        return chosen_song;
    }

    public void setChosen_song(String chosen_song)
    {
        this.chosen_song = chosen_song;
    }

    public MutableLiveData<List<String>> getSongs_to_show()
    {
        return songs_to_show;
    }

    public void setSongs_to_show(List<String> songs_to_show)
    {
        this.songs_to_show.setValue(songs_to_show);
    }


    public MutableLiveData<List<String>> getSongs_to_show_workout()
    {
        return songs_to_show_workout;
    }

    public void setSongs_to_show_workout(List<String> songs_to_show)
    {
        this.songs_to_show_workout.setValue(songs_to_show);
    }

    public List<Playlist> getPlaylists_to_show()
    {
        return playlists_to_show;
    }

    public void setPlaylists_to_show(List<Playlist> playlists_to_show)
    {
        this.playlists_to_show = playlists_to_show;
    }

    public void insert(Playlist playlist)
    {
        playlistRepository.insert(playlist);
    }

    public List<Playlist> getAll()
    {
        return playlistRepository.getAll();
    }

    public LiveData<List<Playlist>> getAllLiveData()
    {
        return playlistRepository.getAllLiveData();
    }

    public List<Playlist> getSpecificPlaylist(String name, String username)
    {
        return playlistRepository.getSpecificPlaylist(name, username);
    }

    public void update_playlist(String songs, String name, String username)
    {
        playlistRepository.update_playlist(songs, name, username);
    }


    public List<Playlist> getPlaylistsForUser(String name)
    {
        return playlistRepository.getPlaylistsForUser(name);
    }

    public LiveData<List<Playlist>> getPlaylistsForUserLive(String name)
    {
        return playlistRepository.getPlaylistsForUserLive(name);
    }

    public LiveData<List<Playlist>> getAllLiveDataForUser(String name)
    {
        return playlistRepository.getAllLiveDataForUser(name);
    }


    public void delete_playlist_allsongs(String name, String username)
    {
        playlistRepository.delete_playlist_allsongs(name, username);
    }

}
