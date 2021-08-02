package com.example.myapp.playlist;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.data.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


public class ShowPlaylistDialogWorkout extends DialogFragment
{

    public Dialog d;
    private static PlaylistsViewModel playlistsViewModel;
    private RecyclerView recyclerView;
    private ActivityWithDrawer mainActivity;

    public ShowPlaylistDialogWorkout()
    {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        playlistsViewModel = new ViewModelProvider(mainActivity).get(PlaylistsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_show_playlist_workout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_playlist_workout);


        ///////////////////////////////KLIKNUTO NA PLAY DUGME - PAMTIMO PJESME KOJE CE SE PUSTATI//////////////////////////
        PlaylistDialogAdapter playlistDialogAdapter = new PlaylistDialogAdapter(null, "workout_fragment");

        playlistDialogAdapter.setCallback(new PlaylistDialogAdapter.CallbackShow()
        {
            Semaphore semaphore = new Semaphore(1);

            @Override
            public void onShowClick(String plejlista)
            {
                playlistsViewModel.setCurrent_playlist_workout(plejlista); //slusamo trenutno tu playlisytu
                final int[] i = {0};
                final List<String>[] songs_from_chosen_playlist = new List[]{new ArrayList<>()}; //pjesme iz te plejliste

                try
                {
                    semaphore.acquire();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                AsyncTask.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<Playlist> playlists = playlistsViewModel.getSpecificPlaylist(plejlista, playlistsViewModel.getLogin_username());
                        String[] songs = playlists.get(0).getSongs().split(",");


                        for (int i = 0; i < songs.length; i++)
                        {
                            songs_from_chosen_playlist[0].add(songs[i]);
                        }
                        if (songs[0] == "") songs_from_chosen_playlist[0] = null;

                        semaphore.release();
                    }
                });

                //tek kad smo sigurni da je zarseno dohvatanje iz baze postavljamo pjesme
                try
                {
                    semaphore.acquire();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                playlistsViewModel.setSongs_to_show_workout(songs_from_chosen_playlist[0]);

                dismiss();
                semaphore.release();
            }

            @Override
            public void onDeleteClick(String plejlista)
            {
                //ne treba nam ovo kad pustamo pjesme
                //necemo uopste imati ovo dugme
            }
        });

        playlistsViewModel.getAllLiveDataForUser(playlistsViewModel.getLogin_username()).observe(
                getViewLifecycleOwner(),
                playlists -> {
                    if (playlists == null || playlists.size() == 0)
                    {
                        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_playlist_workout);
                        recyclerView.setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.empty_dialog_show_playlists).setVisibility(View.VISIBLE);
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty_dialog_show_playlists).setVisibility(View.INVISIBLE);
                    playlistDialogAdapter.setAllPlaylists(playlists);
                });

        recyclerView.setAdapter(playlistDialogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));


        ///////////////////////////////KLIK DUGME - DA SE ZATVORI DIALOG//////////////////////////
        Button button_close = (Button) view.findViewById(R.id.button_close);
        button_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        return view;
    }


}











