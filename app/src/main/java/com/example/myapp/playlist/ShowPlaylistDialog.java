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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;



public class ShowPlaylistDialog extends DialogFragment {

    public static ActivityWithDrawer myactivity;
    public Dialog d;
    public Button  no;
    private static PlaylistsViewModel playlistsViewModel;
    private RecyclerView recyclerView;
    private ActivityWithDrawer mainActivity;

    public ShowPlaylistDialog() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer)requireActivity();
        playlistsViewModel = new ViewModelProvider(mainActivity).get(PlaylistsViewModel.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_playlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_playlist);

        ///////////////////////////////KLIK DUGME - DA SE PRIKAZU NESORTIRANE PJESME//////////////////////////
        Button button_unsorted_songs = (Button) view.findViewById(R.id.choose_newsongs);
        button_unsorted_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistsViewModel.setPlus_or_minus("plus"); //slika plus da se prikaze
                playlistsViewModel.setCurrent_playlist("");
                mainActivity.getMusic();
                dismiss();
            }
        });

        ///////////////////////////////KLIK DUGME - DA SE SVE PJESME////////////////////////////////////////////
        Button button_all_songs = (Button) view.findViewById(R.id.all_songs_button);
        button_all_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistsViewModel.setPlus_or_minus(""); //nista ne prikazuj
                String niz[] = playlistsViewModel.getCurrent_songs_on_phone().split(",");
                List<String> all_songs_on_phone = Arrays.asList(niz);
                playlistsViewModel.setSongs_to_show(all_songs_on_phone);
                dismiss();
            }
        });


        ///////////////////////////////KLIKNUTO NA SHOW DUGME - PRIKAZ PJESAMA NEKE PLEJLISTE//////////////////////////
        PlaylistDialogAdapter playlistDialogAdapter = new PlaylistDialogAdapter(null, "playlist_fragment");
        playlistDialogAdapter.setCallback(new PlaylistDialogAdapter.CallbackShow() {
            Semaphore semaphore = new Semaphore(1);
            @Override
            public void onShowClick(String plejlista) {
                playlistsViewModel.setCurrent_playlist(plejlista);
                playlistsViewModel.setPlus_or_minus("minus"); //treba da se prikaze plejlista sa slikom minusa
                final int[] i = {0};
                final List<String>[] songs_from_chosen_playlist = new List[]{new ArrayList<>()};
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        List <Playlist> playlists =  playlistsViewModel.getSpecificPlaylist(plejlista, playlistsViewModel.getLogin_username());
                         String[] songs = playlists.get(0).getSongs().split(",");

                         for (int i=0; i<songs.length; i++)
                         {
                             songs_from_chosen_playlist[0].add(songs[i]);
                         }
                        if(songs[0]=="")  songs_from_chosen_playlist[0] = null;
                        //else  songs_from_chosen_playlist[0] = Arrays.asList(songs);
                        semaphore.release();
                    }
                });

                //tek kad smo sigurni da je zarseno dohvatanje iz baze postavljamo pjesme
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                   playlistsViewModel.setSongs_to_show(songs_from_chosen_playlist[0]);
                  dismiss();
                    semaphore.release();
            }

            @Override
            public void onDeleteClick(String plejlista) {


                        //da se prikazu nesortirane pjesme, to radi metoda iz fragmentdrawera
                        if(playlistsViewModel.getCurrent_playlist().equals("")) {
                            playlistsViewModel.setCurrent_playlist("delete"); //da bi se pozvala metoda koja ce da azurira listu pjesama
                            // mainActivity.getMusic();
                        }

                        //akosmo trenutno na nekoj plejlisti da bude prazna
                        else
                        {
                            String username = playlistsViewModel.getLogin_username();
                            playlistsViewModel.setSongs_to_show(null);

                        }
                 AsyncTask.execute(new Runnable() {
                     @Override
                     public void run() {
                         playlistsViewModel.delete_playlist_allsongs(plejlista, playlistsViewModel.getLogin_username());
                         dismiss();

                     }
                 });


            }
        });
        playlistsViewModel.getAllLiveDataForUser(playlistsViewModel.getLogin_username()).observe(
            getViewLifecycleOwner(),
                playlists -> {
                    {
                        if (playlists == null || playlists.size() == 0)
                        {
                            recyclerView.setVisibility(View.INVISIBLE);
                            view.findViewById(R.id.empty_dialog_show_playlists0).setVisibility(View.VISIBLE);
                            return;
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.empty_dialog_show_playlists0).setVisibility(View.INVISIBLE);
                        playlistDialogAdapter.setAllPlaylists(playlists);

                    }

                });

        recyclerView.setAdapter(playlistDialogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));




        Button button_close = (Button) view.findViewById(R.id.button_close);

        ///////////////////////////////KLIK DUGME - DA SE ZATVORI SHOW DIALOG//////////////////////////
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }




    }











