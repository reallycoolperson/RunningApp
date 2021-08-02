package com.example.myapp.playlist;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.myapp.R;
import com.example.myapp.data.Playlist;
import com.example.myapp.databinding.ViewHolderPlaylistBinding;


public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistViewHolder>
{

    private CallbackShow callback;
    private String calledby = "";

    public interface CallbackShow
    {
        void onShowClick(String plejlista);

        void onDeleteClick(String plejlista);
    }


    private List<Playlist> allPlaylists = new ArrayList<>();

    public PlaylistDialogAdapter(CallbackShow callback, String calledby)
    {
        this.calledby = calledby;
        this.callback = callback;
    }

    public void setCallback(CallbackShow callback)
    {
        this.callback = callback;
    }

    public void setAllPlaylists(List<Playlist> playlists)
    {
        this.allPlaylists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderPlaylistBinding viewHolderPlaylistBinding = ViewHolderPlaylistBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlaylistViewHolder(viewHolderPlaylistBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position)
    {
        holder.bind(allPlaylists.get(position).getPlaylist_name());
        holder.setCallbackShow(callback);
        //Log.d("play", "uslo1");

    }

    @Override
    public int getItemCount()
    {
        return allPlaylists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolderPlaylistBinding binding;
        protected CallbackShow callbackShow;


        public PlaylistViewHolder(@NonNull ViewHolderPlaylistBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;

            binding.choosePlaylist.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int index = getAdapterPosition();
                    String plejlista = allPlaylists.get(index).getPlaylist_name();
                    callbackShow.onShowClick(plejlista);
                }
            });

            binding.slikaObrisiPlaylistu.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int index = getAdapterPosition();
                    String plejlista = allPlaylists.get(index).getPlaylist_name();
                    callbackShow.onDeleteClick(plejlista);
                }
            });

        }

        public void bind(String playlist_name)
        {
            binding.nameOfPlaylist.setText(playlist_name);
            if (calledby.equals("workout_fragment"))
                binding.choosePlaylist.setText(R.string.play); //mijenja se izgled dialoga u zavisnosti dje je pozvan
            if (calledby.equals("workout_fragment"))
                binding.slikaObrisiPlaylistu.setImageBitmap(null);
        }


        public void setCallbackShow(CallbackShow callbackShow)
        {
            this.callbackShow = callbackShow;
        }
    }

}
