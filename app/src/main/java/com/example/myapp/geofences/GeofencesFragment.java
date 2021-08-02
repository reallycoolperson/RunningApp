package com.example.myapp.geofences;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentGeofencesBinding;
import com.example.myapp.routes.RouteViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class GeofencesFragment extends Fragment implements OnMapReadyCallback {

    private static final String ID = "GEOFENCE_ID";
    private FragmentGeofencesBinding binding;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private LinearLayoutManager novi;
    private RouteViewModel routeViewModel;
    private GoogleMap mGoogleMap;
    private GeofencingClient geofencingClient;
    private FusedLocationProviderClient fusedLocationClient;
    private PendingIntent pendingIntent;

    private static int RADIOUS_FOR_GEOFENCE = 190;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if (isPermissionGranted) {

                            SupportMapFragment supportMapFragment = (SupportMapFragment) (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.google_map_geofence);
                            supportMapFragment.getMapAsync(GeofencesFragment.this);
                        }
                    }
            );


    public GeofencesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        routeViewModel = new ViewModelProvider(mainActivity).get(RouteViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);
        geofencingClient = LocationServices.getGeofencingClient(mainActivity);
        GeofenceBroadcastReceiver.activity = mainActivity;

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("is", "novaa");
        binding = FragmentGeofencesBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.geofences_toolbar_title);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_MY_GEOFENCES;

        //DOZVOLA ZA LOKACIJU
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            SupportMapFragment supportMapFragment = (SupportMapFragment) (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.google_map_geofence);
            supportMapFragment.getMapAsync(GeofencesFragment.this);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavHostFragment navHostFragment =
                (NavHostFragment) mainActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.my_nav_host_container);
        navController = navHostFragment.getNavController();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("lala", "nema dozvozole geofences");

        } else {
            mGoogleMap = googleMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.setMyLocationEnabled(true);
            SharedPreferences preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);

               tag_locations();

        }


    }


    @SuppressLint("MissingPermission")
    public void tag_locations() {
        // CRTAMO NA MAPU LOKACIJU I KRUG
        //44.7880, 20.5169 bulevar
        //44.8045, 20.4858 dim tuc
        //44.8132, 20.5075

        LatLng niz[] = new LatLng[]{new LatLng(44.7880, 20.5169), new LatLng(44.8045, 20.4858), new LatLng(44.8132, 20.5075)};
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(niz[0].latitude, niz[1].longitude), 14));

        Intent intent = new Intent(mainActivity, GeofenceBroadcastReceiver.class);
        if(mainActivity ==null) {Log.d("lala", "NULLLLLLLLL");}
        pendingIntent = PendingIntent.getBroadcast(mainActivity, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        for (int i = 0; i < niz.length; i++) {
            MarkerOptions markerOptions = new MarkerOptions().position(niz[i]);
            mGoogleMap.addMarker(markerOptions);

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(niz[i]);
            circleOptions.radius(RADIOUS_FOR_GEOFENCE);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(64, 255, 0, 0));
            circleOptions.strokeWidth(4);
            mGoogleMap.addCircle(circleOptions);

            if (routeViewModel.show_once == 1) {
                if (Build.VERSION.SDK_INT >= 29) {
                    if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        add_geofence(niz[i], i);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            //We show a dialog and ask for permission
                            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 10002);
                        } else {
                            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 10002);
                        }
                    }

                } else {
                    add_geofence(niz[i], i);
                }
            }

        }
        routeViewModel.show_once = 0;

    }

    private void add_geofence(LatLng latLng, int i) {
        //ADD GEOFENCE

        Geofence new_geofence = new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, RADIOUS_FOR_GEOFENCE)
                .setRequestId(ID + i)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(new_geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();


        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("lala", "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("lala", "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 10002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(mainActivity, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(mainActivity, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }


}