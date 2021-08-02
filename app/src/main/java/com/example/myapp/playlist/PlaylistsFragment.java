package com.example.myapp.playlist;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentPlaylistsBinding;
import com.example.myapp.routes.RouteViewModel;

import java.util.List;




public class PlaylistsFragment extends Fragment
{

    private FragmentPlaylistsBinding binding;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private PlaylistsViewModel playlistsViewModel;
    final CreatePlaylistDialog[] playlistDialog = new CreatePlaylistDialog[1];
    final ShowPlaylistDialog[] d = new ShowPlaylistDialog[1];

    public PlaylistsFragment()
    {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        playlistsViewModel = new ViewModelProvider(mainActivity).get(PlaylistsViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.playlists_toolbar);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_PLAYLISTS_FRAGMENT;

        //Log.d("lala", playlistsViewModel.getSongs_to_show().getValue().get(0).toString());
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(null);

        playlistAdapter.setCallback(new PlaylistAdapter.Callback()
        {
            @Override
            public void onAddClick(int position)
            {
                List<String> all_songs = playlistsViewModel.getSongs_to_show().getValue();
                String chosen_song = all_songs.get(position);
                playlistsViewModel.setChosen_song(chosen_song); //odabrana pjesma jos da je stavimo u plejlistu, a to cemo preko dijaloga
                playlistsViewModel.setChosen_song_index(position);
                final AddToPlaylistDialog d = new AddToPlaylistDialog();
                d.show(getActivity().getSupportFragmentManager(), "myTag2");
                playlistsViewModel.setAdd_dialog(d);
            }

            @Override
            public void onMinusClick(String song)
            {
                List<String> songs_from_playlist = playlistsViewModel.getSongs_to_show().getValue();

                int to_delete = 0;
                String songs_to_update = "";
                for (int i = 0; i < songs_from_playlist.size(); i++)
                {
                    String s = songs_from_playlist.get(i);
                    if (s.equals(song))
                    {
                        to_delete = i;
                    }
                    else
                    {
                        if (songs_to_update.equals(""))
                        {
                            songs_to_update = s;
                        }
                        else
                        {
                            songs_to_update = songs_to_update + "," + s;
                        }
                    }
                }

                songs_from_playlist.remove(to_delete); //trenutna playlisya

                playlistsViewModel.setSongs_to_show(songs_from_playlist);

                String finalSongs_to_update = songs_to_update;
                //Log.d("lala", "new "+  songs_to_update);
                AsyncTask.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        playlistsViewModel.update_playlist(finalSongs_to_update, playlistsViewModel.getCurrent_playlist(), playlistsViewModel.getLogin_username());
                      //  Log.d("lala", "new2 "+ playlistsViewModel.getSongs_to_show().getValue().get(0) );

                    }
                });

            }
        });

        playlistsViewModel.getSongs_to_show().observe(getViewLifecycleOwner(),
                songs -> {

                                if (songs == null || songs.size() == 0)
                                {
                                    binding.recyclerViewPlaylists.setVisibility(View.INVISIBLE);
                                   binding.emptyPlaylistsFragment.setVisibility(View.VISIBLE);
                                    return;
                                }
                                  binding.recyclerViewPlaylists.setVisibility(View.VISIBLE);
                                  binding.emptyPlaylistsFragment.setVisibility(View.INVISIBLE);

                                  playlistAdapter.setAllSongs(songs);
                                  playlistAdapter.setSlika(playlistsViewModel.getPlus_or_minus());

                }
        );


        binding.recyclerViewPlaylists.setAdapter(playlistAdapter);
        binding.recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(mainActivity));


        //////////////////////////////CREATE DIALOG////////////////////////////////
        binding.buttonCreatePlaylist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playlistDialog[0] = new CreatePlaylistDialog(mainActivity);
                playlistDialog[0].show();
            }
        });


        ////////////////////////////SHOW DIALOG//////////////////////////////////
        binding.buttonShowPlaylists.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d[0] = new ShowPlaylistDialog();
                d[0].show(getActivity().getSupportFragmentManager(), "myTag");
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Log.d("hello", "majn");
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


    }
}