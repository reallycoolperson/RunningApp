package com.example.myapp.playlist;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.util.LinkedList;
import java.util.List;



public class AddToPlaylistDialog extends DialogFragment
{

    public static ActivityWithDrawer myactivity;
    public Dialog d;
    private static PlaylistsViewModel playlistsViewModel;
    private RecyclerView recyclerView;
    private ActivityWithDrawer mainActivity;
    private String login_username;

    public AddToPlaylistDialog()
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
        View view = inflater.inflate(R.layout.dialog_add_to_playlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_playlist_add);


        login_username = playlistsViewModel.getLogin_username();

        AddAdapter playlistDialogAdapter = new AddAdapter(null);
        playlistDialogAdapter.setCallback(new AddAdapter.CallbackAdd()
        {
            @Override
            //izabrana pjesma i plejlista, azurirati plejlistu sa pjesmom
            public void onAddClick(int position)
            {
                playlistsViewModel.getAllLiveDataForUser(login_username).observe(getViewLifecycleOwner(), playlists -> {

                    Playlist p = playlists.get(position);
                    String old_songs = p.getSongs();
                    String new_songs = "";


                    if(old_songs.equals(playlistsViewModel.getChosen_song()))
                    {
                        return;
                    }
                    else if(old_songs.contains(","))
                    {
                      String niz[] =   old_songs.split(",");
                        if(niz[niz.length-1].equals(playlistsViewModel.getChosen_song()))
                        return;
                    }

                    if (!old_songs.equals(""))
                    {
                        new_songs = old_songs + "," + playlistsViewModel.getChosen_song();
                    }
                    else
                    {
                        new_songs = playlistsViewModel.getChosen_song();
                    }
                    String finalNew_songs = new_songs;
                   // Log.d("lala", "pjesme za updaye " + finalNew_songs);
                    AsyncTask.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            playlistsViewModel.update_playlist(finalNew_songs, p.getPlaylist_name(), login_username);
                            dismiss();
                        }
                    });

                });
            }
        });

        List<String> playlists_strings = new LinkedList<String>();
        playlistsViewModel.getAllLiveDataForUser(login_username).observe(
                getViewLifecycleOwner(),
                playlists -> {
                    if (playlists == null || playlists.size() == 0)
                    {
                        recyclerView.setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.empty_dialog_show_playlists_add).setVisibility(View.VISIBLE);
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty_dialog_show_playlists_add).setVisibility(View.INVISIBLE);
                    playlistDialogAdapter.setPlaylists(playlists);

                });


        recyclerView.setAdapter(playlistDialogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //  this.getDialog().setTitle("radi");


        //////////////////////////CLOSE ADD DIALOG//////////////////////////////////////
        Button button_close = (Button) view.findViewById(R.id.button_close_add_dialog);
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











