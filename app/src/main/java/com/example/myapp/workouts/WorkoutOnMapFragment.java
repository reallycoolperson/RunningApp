package com.example.myapp.workouts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentWorkoutOnMapBinding;
import com.example.myapp.playlist.PlaylistsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;


public class WorkoutOnMapFragment extends Fragment implements OnMapReadyCallback {


    private FragmentWorkoutOnMapBinding binding;
    private WorkoutViewModel workoutViewModel;
    private PlaylistsViewModel playlistsViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private GoogleMap mGoogleMap;


    public WorkoutOnMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        playlistsViewModel = new ViewModelProvider(mainActivity).get(PlaylistsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutOnMapBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.workout_info_toolbar_title);

        SupportMapFragment supportMapFragment = (SupportMapFragment)  (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(WorkoutOnMapFragment.this);
        String info[] = workoutViewModel.getWorkout_info();
        binding.nameWorkout.setText(info[0]);
        binding.dateWorkout.setText(info[1]);
        binding.distanceWorkout.setText(info[2]);
        binding.durationWorkout.setText(info[3]);
        binding.paceWorkout.setText(info[4]);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       String niz[] =  workoutViewModel.getPositions_from_chosen_workout().split(";");

       if(niz.length==0 || niz.length<=3 || niz[0].equals(""))
       {
           binding.conInfo2.setVisibility(View.INVISIBLE);
           binding.conInfo3.setVisibility(View.VISIBLE);
           return;
       }
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(false));

        List<LatLng> lista = new ArrayList<>();

        int c = 0;


        for(int i=c; i < niz.length-1; i++)
        {
            if(niz[i].equals("")) break;
            Log.d("lala", "loc:" + niz[i]);
            String x_y[] = niz[i].split(",");
           double lat1 = Double.parseDouble(x_y[0]);
           double lon1 = Double.parseDouble(x_y[1]);

            LatLng l = new LatLng(lat1, lon1);
            if(niz[i+1]!= null && !niz[i+1].equals("")) {
                String x_y2[] = niz[i+1].split(",");
                double lat2 = Double.parseDouble(x_y2[0]);
                double lon2 = Double.parseDouble(x_y2[1]);
                double φ1 = lat1 * Math.PI/180;
                double φ2 = lat2 * Math.PI/180;
                double Δλ = (lon2-lon1) * Math.PI/180;
                double R = 6371e3;
                double d = Math.acos( Math.sin(φ1)*Math.sin(φ2) + Math.cos(φ1)*Math.cos(φ2) * Math.cos(Δλ) ) * R;

                Log.d("lala", "distanca izmedju 2 tacke = " + x_y[0] + "-" +x_y2[0] + "="+ d );
                if(d>13)
                {
                    lista.clear();
                    Log.d("lala", "prevelika distanca");
                }
                else
                {
                    lista.add(l);
                }
            }
            else
            {
                lista.add(l);
            }
        }

        polyline1.setPoints(lista);

        if(lista != null && lista.size()>=2) //makar 2 tacke da se napravi linija
        {
        LatLng first = lista.get(0);
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        polyline1.setEndCap(new RoundCap());
        polyline1.setWidth(20);
        polyline1.setColor(R.color.yellow);
        polyline1.setJointType(JointType.ROUND);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 16));

        lista.clear();
        }
        else
        {
            binding.conInfo2.setVisibility(View.INVISIBLE);
            binding.conInfo3.setVisibility(View.VISIBLE);
            return;
        }
        // Set listeners for click events.


    }
}