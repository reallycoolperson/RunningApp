package com.example.myapp.workouts;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

public class LifecycleAwarePlayer implements DefaultLifecycleObserver {

    private MediaPlayer mediaPlayer = null;

    @Inject
    public LifecycleAwarePlayer() {

    }

    public void start(Context context) {
      /*  if (mediaPlayer == null) {
            try {
                Log.d("lala", "uslo");
                String song = "Magalenha.mp3";
                String path = context.getFilesDir().getAbsolutePath() + File.separator + song;
                Log.d("lala", path);

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepareAsync();
                Log.d("lala", "uslo2");


            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
