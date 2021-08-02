package com.example.myapp.workouts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.NavGraphDirections;
import com.example.myapp.R;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.FragmentWorkoutStartBinding;
import com.example.myapp.playlist.PlaylistsViewModel;
import com.example.myapp.playlist.ShowPlaylistDialogWorkout;
import com.example.myapp.routes.RouteViewModel;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    public static final String START_TIMESTAMP_KEY = "start-timestamp-key";
    private static final String START_STEPS_KEY = "start-steps-key";
    private static int FINE_ACCESS_LOCATION_CODE = 1001;

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;

    private PlaylistsViewModel playlistsViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private SensorManager sensorManager = null;
    final ShowPlaylistDialogWorkout[] d = new ShowPlaylistDialogWorkout[1];
    private double MagnitudePrevious;
    private double stepCount = 0;

    private Timer timer;
    private SharedPreferences sharedPreferences;
    private Semaphore mutex_insert = new Semaphore(1);

    ////////steps
    private boolean running = false;
    private float totalSteps = 0;
    private float previousSteps = 0;
    private String positions = "";
    private long id_current_workout = 0;

    ////////////player
    private MediaPlayer  mMediaPlayer = null;
    private  List<String> songs_from_album = null;
    private int duration = 0;

    /////////////username
    String username;
    SharedPreferences preferences;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if (isPermissionGranted) {
                            startWorkout(new Date().getTime(), 0);

                        }
                    });

    public WorkoutStartFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        mainActivity.registerReceiver(mMessageReceiver, new IntentFilter("NOTIFICATION_POSITIONS"));

        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

        playlistsViewModel = new ViewModelProvider(mainActivity).get(PlaylistsViewModel.class);

        WorkoutService.activity = mainActivity;

        WorkoutService.workoutViewModel = workoutViewModel;

        sharedPreferences = mainActivity
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
    }



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutStartBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.start_workout_toolbar);
Log.d("lala", "jaa");
       Handler handler_counter = new Handler(Looper.getMainLooper());

        handler_counter.post(() ->
                {
                    workoutViewModel.getStepCount().observe(mainActivity, steps ->{
                        binding.stepCountt.setText(steps + "");

                    });
                });

         username = preferences.getString("username", "");

        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_WORKOUT_START_FRAGMENT;

            binding.lin1.setVisibility(View.INVISIBLE);
            binding.lin2.setVisibility(View.INVISIBLE);

        playlistsViewModel.getSongs_to_show_workout().observe(getViewLifecycleOwner(),
                songs ->{
                  if(songs==null)
                  {
                    return;
                  }
                    //nema pjesama u plejlisti
                    if(songs.size() == 0)
                  {
                      Toast.makeText(mainActivity, R.string.playlist_empty, Toast.LENGTH_SHORT).show();
                      return;
                  }

                    //izbran neki album, prikazati media player
                    binding.lin1.setVisibility(View.VISIBLE);
                    binding.lin2.setVisibility(View.VISIBLE);

                    //pjeva, a izabran novi album
                   songs_from_album = songs;
                   if(mMediaPlayer!=null && mMediaPlayer.isPlaying())
                   {
                       mMediaPlayer.stop();
                   }

                    String niz[] = songs.get(0).split("\n"); //pjesma + trajanje, ovako micemo trajanje
                    String  song_name = niz[0];
                    playlistsViewModel.setCurrent_song_duration(Integer.parseInt(niz[2]));
                    duration = Integer.parseInt(niz[2]);
                    Log.d("lala", niz[2] + " Duration ");

                    String song = song_name + ".mp3";

                    //create mediaPlayer sa prvom pjesmom iz albuma
                    String path = mainActivity.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + song;
                    Uri myUri = Uri.parse(path);
                    mMediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), myUri);
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            try {

                                next_song();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //azuriranje songTitle
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() ->   binding.songTitle.setText(song_name));
                    handler.post(() ->   binding.albumTitle.setText("Album: " +  playlistsViewModel.getCurrent_playlist_workout() + ""));

                    playlistsViewModel.setCurrent_index_song_playing(0);
                    playlistsViewModel.setCurrent_song_playing(song_name);
                });

        binding.chooseAlbum.setEnabled(true);
        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        timer = new Timer();

        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()), sharedPreferences.getInt(START_STEPS_KEY, 0));
        }

        if(sharedPreferences.contains(START_STEPS_KEY))
        {
           workoutViewModel.setStepCount(sharedPreferences.getInt(START_STEPS_KEY, 666));
        }

        binding.start.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(
                    mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                startWorkout(new Date().getTime(), 0);
            }
        });
        binding.finish.setOnClickListener(view -> finishWorkout());
        binding.cancel.setOnClickListener(view -> cancelWorkout());
        mainActivity.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        stopWorkout();
                    }
                });




        ////////////////////////////CHOOSE ALBUM DIALOG//////////////////////////////////
        binding.chooseAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d[0] = new ShowPlaylistDialogWorkout();
                d[0].show(getActivity().getSupportFragmentManager(), "myTag");
            }
        });



        ////////////////////////////PLAY SONGS/////////////////////////////////////
        binding.play.setOnClickListener(v -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() ->  binding.seekBarTime.setMax(playlistsViewModel.getCurrent_song_duration()));
            if(mMediaPlayer!=null && mMediaPlayer.isPlaying())
            {
                mMediaPlayer.pause();
                handler.post(() ->   binding.play.setImageResource(R.drawable.play_btn));
            }
            else {
                mMediaPlayer.start();
                handler.post(() ->   binding.play.setImageResource(R.drawable.pause_btn));
            }

            loadSong(playlistsViewModel.getCurrent_song_playing());
        });


        ///////////////////////NEXT SONG////////////////////////
        binding.next.setOnClickListener(v->{next_song();});


        ///////////////////////PREVIOUS SONG////////////////////////
        binding.prev.setOnClickListener(v->{ previous_song(); });

        return binding.getRoot();
    }


    /////////////////////////////////////////MEDIA PLAYER////////////////////////////////////////
    private void next_song()
    {
        if (mMediaPlayer != null) {
            binding.play.setImageResource(R.drawable.pause_btn);
        }

        int currentIndex = playlistsViewModel.getCurrent_index_song_playing();
        int  playlist_size = playlistsViewModel.getSongs_to_show_workout().getValue().size();
        if (currentIndex < playlist_size - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
       new_song_to_play(currentIndex);
    }

    private void previous_song()
    {
        if (mMediaPlayer != null) {
            binding.play.setImageResource(R.drawable.pause_btn);
        }

        int currentIndex = playlistsViewModel.getCurrent_index_song_playing();
        int  playlist_size = playlistsViewModel.getSongs_to_show_workout().getValue().size();
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = playlist_size-1;
        }
        new_song_to_play(currentIndex);
    }

    private void new_song_to_play(int currentIndex)
    {
        playlistsViewModel.setCurrent_index_song_playing(currentIndex);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        String niz[] = playlistsViewModel.getSongs_to_show_workout().getValue().get(currentIndex).split("\n");
        String song_name = niz[0];
        playlistsViewModel.setCurrent_song_duration(Integer.parseInt(niz[2]));
        duration = Integer.parseInt(niz[2]);

        playlistsViewModel.setCurrent_song_playing(song_name);
        String song = song_name + ".mp3";
        Log.d("lala", "songgg = " + song);

        String path = mainActivity.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + song;
        Uri myUri = Uri.parse(path);
        mMediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), myUri);
        /*uslov dodat mozda nema pjesme*/
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {

                    next_song();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mMediaPlayer.start();
        loadSong(song_name);
    }



    private void loadSong(String song_name) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> binding.songTitle.setText(song_name));
        String album_name = "Album: " + playlistsViewModel.getCurrent_playlist_workout();
        handler.post(() -> binding.songTitle.setText(song_name));
        handler.post(() -> binding.albumTitle.setText(album_name));

        handler.post(() -> binding.seekBarTime.setMax(playlistsViewModel.getCurrent_song_duration()));


        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });


        binding.seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    binding.seekBarTime.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                 duration = playlistsViewModel.getCurrent_song_duration();
                while (mMediaPlayer != null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler2.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
        @SuppressLint("Handler Leak") Handler handler2 = new Handler () {
            @Override
            public void handleMessage  (Message msg) {
                binding.seekBarTime.setProgress(msg.what);
            }
        };


    /////////////////////////////////////////END MEDIA PLAYER////////////////////////////////////////





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("lala", "ja");
        timer.cancel();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(START_STEPS_KEY, workoutViewModel.getStepCount().getValue());
        editor.commit();

    }

    private void startWorkout(long startTimestamp, int steps) {


        String prov = sharedPreferences.getString(ActivityWithDrawer.WORKOUT_IN_PROGESS, "FALSE");
        Log.d("lala", "start" + prov);

        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
        binding.cancel.setEnabled(true);
        binding.chooseAlbum.setEnabled(false);
       // binding.power.setEnabled(true);


        /////////////////////////////////////////////////////////////////////////////////////////
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(START_TIMESTAMP_KEY, startTimestamp);
        editor.commit();

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = new Date().getTime() - startTimestamp;

                int miliseconds = (int) ((elapsed % 1000) / 10);
                int seconds = (int) ((elapsed / 1000) % 60);
                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

                StringBuilder workoutDuration = new StringBuilder();
                workoutDuration.append(String.format("%02d", hours)).append(":");
                workoutDuration.append(String.format("%02d", minutes)).append(":");
                workoutDuration.append(String.format("%02d", seconds)).append(".");
                workoutDuration.append(String.format("%02d", miliseconds));

                handler.post(() -> binding.workoutDuration.setText(workoutDuration));
            }
        }, 0, 10);



        if(prov.equals("FALSE")) {
            SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor2.putString(ActivityWithDrawer.WORKOUT_IN_PROGESS, "TRUE");
            editor2.commit();
            Intent intent = new Intent();
            intent.setClass(mainActivity, WorkoutService.class);
            intent.setAction(WorkoutService.INTENT_ACTION_START);
            mainActivity.startService(intent);
        }


    }

    private void finishWorkout()  {
        workoutViewModel.workout_start = 0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ActivityWithDrawer.WORKOUT_IN_PROGESS, "FALSE");
        editor.commit();
        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        Workout w = new Workout(
                0,
                new Date(),
                getText(R.string.workout_label).toString(),
                0.2 * minutes,
                minutes,
                workoutViewModel.getStepCount().getValue(),
                username
        );
        Log.d("lala", "position before insert: " +  positions);
        try {
            mutex_insert.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                id_current_workout=  workoutViewModel.insert2(w);
                mutex_insert.release();
            }
        });


        stopWorkout();

    }

    private void cancelWorkout() {
        stopWorkout();
    }

    private void stopWorkout() {
        workoutViewModel.setStepCount(0);

        if(mMediaPlayer!= null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
        binding.lin1.setVisibility(View.INVISIBLE);
        binding.lin2.setVisibility(View.INVISIBLE);
        playlistsViewModel.setSongs_to_show_workout(null);
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        sharedPreferences.edit().remove(START_STEPS_KEY).commit();

        navController.navigate(NavGraphDirections.actionGlobalWorkoutListFragment());
    }



    ////////////////////////////////STEP COUNTER///////////////////////////////////////////




    //////////////////////////////////////////MAPA///////////////////////
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            positions = intent.getExtras().get("MESSAGE_POSITIONS").toString();
            int stepss = workoutViewModel.getStepCount().getValue();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(START_STEPS_KEY, stepss);
            editor.commit();

            try {
                mutex_insert.acquire(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                   long id =  workoutViewModel.getLastWorkoutId();
                   Log.d("lala", "last_id = " + id);
                    workoutViewModel.update_workout_with_positions(positions, id, username);
                    mutex_insert.release();
                }
            });


            Log.d("lala", "dobili poruku = " + positions + " id = " + id_current_workout);

        }
    };



    @Override
    public void onResume() {
        super.onResume();

        Log.d("lala", "onresume");
    }


}
