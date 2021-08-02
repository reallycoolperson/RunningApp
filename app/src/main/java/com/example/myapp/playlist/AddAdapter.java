package com.example.myapp.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.data.Playlist;
import com.example.myapp.databinding.ViewHolderAddBinding;

import java.util.ArrayList;
import java.util.List;



public class AddAdapter extends RecyclerView.Adapter<AddAdapter.PlaylistViewHolder> {

    private CallbackAdd callback;

    public interface CallbackAdd {
        void onAddClick(int position);
    }
    private List<Playlist> allplaylists = new ArrayList<>();

    public AddAdapter(CallbackAdd callback) {
        this.callback = callback;
    }

    public void setCallback(CallbackAdd callback) {
        this.callback = callback;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.allplaylists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderAddBinding viewHolderAddBinding = ViewHolderAddBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlaylistViewHolder(viewHolderAddBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bind(allplaylists.get(position).getPlaylist_name());
        holder.setCallbackAdd(callback);
    }

    @Override
    public int getItemCount() {
        return allplaylists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderAddBinding binding;
        protected  CallbackAdd callbackAdd;

        public PlaylistViewHolder(@NonNull ViewHolderAddBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.choosePlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    callback.onAddClick(index);
                }
            });
        }

        public void setCallbackAdd(CallbackAdd callbackAdd) {
            this.callbackAdd = callbackAdd;
        }

        public void bind(String string) {
          binding.nameOfPlaylistAdd.setText(string);
        }


    }
}
