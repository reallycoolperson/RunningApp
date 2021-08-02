package com.example.myapp.playlist;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.databinding.ViewHolderSongBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>
{

    private Callback callback;
    public int br = 0;

    private String slika = "";

    public String getSlika()
    {
        return slika;
    }

    public void setSlika(String slika)
    {
        this.slika = slika;
    }

    public interface Callback
    {
        void onAddClick(int position);

        void onMinusClick(String song);
    }

    private List<String> allSongs = new ArrayList<>();

    public PlaylistAdapter(Callback callback)
    {
        this.callback = callback;
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public void setAllSongs(List<String> songs)
    {
        br = 0;
        this.allSongs = songs;
        notifyDataSetChanged();
    }

    public void change_happened()
    {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderSongBinding viewHolderSongBinding = ViewHolderSongBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlaylistViewHolder(viewHolderSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position)
    {
        if (allSongs != null && allSongs.size() != 0)
        {

            holder.bind(allSongs.get(position));
           // Log.d("problem", allSongs.get(position));
            holder.callback = callback;
        }
    }

    @Override
    public int getItemCount()
    {
        if (allSongs != null)
            return allSongs.size();
        return 0;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolderSongBinding binding;
        protected Callback callback;


        public PlaylistViewHolder(@NonNull ViewHolderSongBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;

            //kad se klikne na plusic da se prikaze dijalog u gdje da ode pjesma
            binding.imageView4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int index = getAdapterPosition();
                    //Log.d("klik", "klikkk " + index);
                    if (slika.equals("plus"))
                        callback.onAddClick(index); //zvace se metoda za otvaranje add prozora
                    if (slika.equals("minus"))
                    {
                        String song = allSongs.get(index);
                        callback.onMinusClick(song);
                    }

                }
            });
        }


        public void bind(String string)
        {
            String[] data = string.split("\n");
            binding.nameSong.setText(data[0]);
            binding.nameArtist.setText(data[1]);
            br = br % 7 + 1;
            Uri path = Uri.parse("android.resource://com.example.myapp/drawable/music" + br);
            binding.note.setImageURI(path);


            if (slika.equals("minus"))
            {
                binding.imageView4.setImageResource(R.drawable.negative);
            } else if (slika.equals("plus"))
            {
                binding.imageView4.setImageResource(R.drawable.add);
            } else
            {
                binding.imageView4.setImageResource(R.color.white);
            }
        }


    }
}
