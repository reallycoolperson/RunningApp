package com.example.myapp.playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.data.Playlist;
import com.google.android.material.textfield.TextInputEditText;



public class CreatePlaylistDialog extends Dialog implements
        View.OnClickListener
{

    public ActivityWithDrawer activity;
    public Dialog d;
    public Button yes, no;
    private PlaylistsViewModel playlistsViewModel;

    public CreatePlaylistDialog(ActivityWithDrawer activity)
    {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(600, 400); //Controlling width and height.

        playlistsViewModel = new ViewModelProvider(activity).get(PlaylistsViewModel.class);

        setContentView(R.layout.dialog_create_playlist);
        yes = (Button) findViewById(R.id.ok);
        no = (Button) findViewById(R.id.cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    ///////////////////////////////////KREIRANJE NOVE PLEJLISTE/////////////////////////////////////////////
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ok:
                // activity.finish();
                TextInputEditText playlist = (TextInputEditText) findViewById(R.id.playlist_name_input);
                String my_playlist = playlist.getText().toString();
               // Log.d("playlist", "playlist : " + my_playlist);

                SharedPreferences preferences = activity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                String username = preferences.getString("username", "");

                AsyncTask.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        playlistsViewModel.insert(new Playlist(0, my_playlist, "", username));
                    }
                });
                dismiss();
                break;

            case R.id.cancel:
                dismiss();
                break;

            default:
                break;
        }
        dismiss();
    }
}