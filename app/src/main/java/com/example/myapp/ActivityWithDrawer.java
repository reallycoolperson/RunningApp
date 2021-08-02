package com.example.myapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.data.Playlist;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.ActivityWithDrawerBinding;
import com.example.myapp.login.UserViewModel;
import com.example.myapp.playlist.PlaylistsViewModel;
import com.example.myapp.routes.RouteBrowseFragment;
import com.example.myapp.routes.RouteViewModel;
import com.example.myapp.workouts.WorkoutListFragmentDirections;
import com.example.myapp.workouts.WorkoutStartFragment;
import com.google.android.material.navigation.NavigationView;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class ActivityWithDrawer extends AppCompatActivity
{

    public static final String LOG_TAG = "running-app-example";
    private NavController navController;

    private RouteViewModel routeViewModel;
    private PlaylistsViewModel playlistsViewModel;
    private UserViewModel userViewModel;

    private ActivityWithDrawerBinding binding;
    private NavigationView nav;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private Fragment pocetni;
    private String login_username;

    public static String WORKOUT_IN_PROGESS = "FALSE";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        Log.d("lala", "oncrate" + RouteViewModel.current_fragment);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        playlistsViewModel = new ViewModelProvider(this).get(PlaylistsViewModel.class);
        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        sharedPreferences = this
                .getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        binding = ActivityWithDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.my_nav_host_container);
        navController = navHostFragment.getNavController();

        SharedPreferences preferences = getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        String fullname = preferences.getString("fullname", "");
        String username = preferences.getString("username", "");
        String photo_path = preferences.getString("filename", "");
        login_username = username;
        playlistsViewModel.setLogin_username(login_username);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
        } else
        {
            getMusic();
        }


        String[] field = new String[1];
        field[0] = "profile_photo";

        String[] data = new String[1];
        data[0] = photo_path;


        NavigationView navigationView = (NavigationView) binding.navmenu;
        View headerView = navigationView.getHeaderView(0);

        ////////////////////////////////welcome username////////////////////////////////
        TextView navFullname = (TextView) headerView.findViewById(R.id.imeiprezimee);
        navFullname.setText(fullname);

        TextView navUsername = (TextView) headerView.findViewById(R.id.username);
        navUsername.setText(username);

        ////////////////////////////////profilna slika////////////////////////////////
        if (photo_path != "")  //samo ako ima sliku dovlacimo je
        {
            PutData putData = new PutData("http://192.168.1.50:80/loginRegister/retrieve_photo.php", "POST", field, data);
            if (putData.startPut())
            {
                if (putData.onComplete())
                {
                    String result = putData.getResult();
                    byte[] imageBytes = Base64.decode(result, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    ImageView imageView = (ImageView) headerView.findViewById(R.id.profile_photo);
                    imageView.setImageBitmap(decodedImage);
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.navmenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            Fragment temp = new RouteBrowseFragment();

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.favorite:
                        navController.navigate(NavGraphDirections.actionGlobalGeofencesFragment());
                        break;

                    case  R.id.reminder:
                        navController.navigate(NavGraphDirections.actionGlobalReminderFragment());
                        break;

                    case  R.id.statitics:
                        navController.navigate(NavGraphDirections.actionGlobalStatisticsFragment());
                        break;

                    case  R.id.selected_workouts:


                            if(sharedPreferences.contains(WORKOUT_IN_PROGESS) && sharedPreferences.getString(WORKOUT_IN_PROGESS, "FALSE").equals("TRUE")){
                            navController.navigate(NavGraphDirections.actionGlobalStartWorkout());
                        }
                        else
                        {
                            navController.navigate(NavGraphDirections.actionGlobalWorkoutListFragment());
                        }


                            break;
                    case R.id.selected_calories:
                        navController.navigate(NavGraphDirections.actionGlobalCaloriesFragment());
                        break;
                    case R.id.selected_rouetes:
                        if (routeViewModel.current_fragment == 0) break;
                        navController.navigate(NavGraphDirections.actionGlobalRouteBrowseFragment());
                        break;
                    case R.id.logout:
                        SharedPreferences preferences = getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.putString("fullname", "");
                        editor.putString("username", "");
                        editor.putString("filename", "");
                        editor.putString("show_once", "1");

                        editor.apply();
                        finish();
                        break;

                    case R.id.selected_playlists:
                        navController.navigate(NavGraphDirections.actionGlobalPlaylistsFragment());
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        int current_page = RouteViewModel.current_fragment;
        if (current_page == 0)
        {
            moveTaskToBack(true);

        } else if (current_page == RouteViewModel.CURRENT_PAGE_CALORIES_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_WORKOUT_LIST_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_ROUTE_DETAILS_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_REMINDER_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_STATISTICS_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_PLAYLISTS_FRAGMENT ||
                current_page == RouteViewModel.CURRENT_PAGE_MY_GEOFENCES)
        {
            binding.navmenu.getMenu().getItem(0).setChecked(true);
            RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_ROUTE_BROWSE_FRAGMENT;
            navController.navigate(NavGraphDirections.actionGlobalRouteBrowseFragment());
        } else if (current_page == RouteViewModel.CURRENT_PAGE_WORKOUT_CREATE_FRAGMENT || current_page == RouteViewModel.CURRENT_PAGE_WORKOUT_START_FRAGMENT)
        {
            RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_ROUTE_BROWSE_FRAGMENT;
            navController.navigate(NavGraphDirections.actionGlobalWorkoutListFragment());
        }

    }


    //////////////////////////////////////UCITAVANJE MUZIKE IZ TELEFONA////////////////////////////////
    ArrayList<String> arrayList = new ArrayList<>();

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Log.d("music", "dobili_dozvolu");
            getMusic();
        }
    }

    public void getMusic()
    {
        playlistsViewModel.getPlaylistsForUserLive(login_username).observe(this, new Observer<List<Playlist>>()
        {
            @Override
            public void onChanged(List<Playlist> playlists)
            {
                if (playlistsViewModel.getCurrent_playlist().equals("") || playlistsViewModel.getCurrent_playlist().equals("delete"))
                { //samo ako smo kliknuli da se pikazu sve pjesme
                    // Log.d("uns", "usli");
                    arrayList = new ArrayList<>();
                    List<Playlist> user_playlists = playlists;
                    String user_songs_on_playlists = "";
                    //sve pjesme iz njegovih plejlisti u jedan string, da bismo prikazvali samo one pjesme
                    //koje nisu u plejlistama
                    for (int i = 0; i < user_playlists.size(); i++)
                    {
                        String songs = user_playlists.get(i).getSongs();
                        if (user_songs_on_playlists.equals(""))
                        {
                            user_songs_on_playlists = songs;
                        } else
                        {
                            user_songs_on_playlists = user_songs_on_playlists + "," + songs;
                        }
                    }
                    //Log.d("userr", user_songs_on_playlists);
                    ContentResolver contentResolver = ActivityWithDrawer.this.getContentResolver();
                    Uri songUti = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
                    String user_all_songs = "";
                    Cursor songCursor = contentResolver.query(songUti, null, selection, null, null);
                    if (songCursor != null)
                    {
                        while (songCursor.moveToNext())
                        {

                            int song_title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                            int song_duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                            String title = songCursor.getString(song_title);
                            String song_duration_ms = songCursor.getString(song_duration);
                            Integer song_duration_min = (Integer.parseInt(song_duration_ms) / 1000) / 60;
                            Integer song_duration_sek = (Integer.parseInt(song_duration_ms) / 1000) % 60;
                            String song_duration_ukupno = song_duration_min + ":" + String.format("%02d", song_duration_sek);
                            //  Log.d("music", title);
                            // Log.d("music", song_duration_ukupno);

                            String song = title + "\n" + song_duration_ukupno + "\n" + song_duration_ms;
                            if (!user_songs_on_playlists.contains(song))
                            {
                                //  Log.d("ispis2", song + " nije na plejlisti");
                                arrayList.add(song);
                            }
                            if (user_all_songs.equals("")) user_all_songs = song;
                            else user_all_songs = user_all_songs + "," + song;
                        }


                        playlistsViewModel.setCurrent_songs_on_phone(user_all_songs); //sve pjesme koje postoje na telefonu
                        playlistsViewModel.setSongs_to_show(arrayList);  //sve unsorted pjesme (nisu ni na jednoj plejlisti)
                    }
                }
            }
        });
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d("lala", "rotatiiiooooooooon");
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
          //  rotation(RouteViewModel.current_fragment);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           // rotation(RouteViewModel.current_fragment);
        }
    }
}