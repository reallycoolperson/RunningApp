package com.example.myapp.routes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentRouteBrowseBinding;

import java.util.ArrayList;
import java.util.List;


public class RouteBrowseFragment extends Fragment {

    private FragmentRouteBrowseBinding binding;
    private RouteViewModel routeViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private LinearLayoutManager novi;
    private RouteAdapter routeAdapter;

    public RouteBrowseFragment() {
//        getLifecycle().addObserver(new LifecycleAwareLogger(
//                MainActivity.LOG_TAG,
//                RouteBrowseFragment.class.getSimpleName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        routeViewModel = new ViewModelProvider(mainActivity).get(RouteViewModel.class);
        novi = new LinearLayoutManager(mainActivity);


        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            routes.add(Route.createFromResources(getResources(), i));
        }
        routeViewModel.setRoutes(routes);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("is", "novaa");
        binding = FragmentRouteBrowseBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.route_browse_toolbar_title);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_ROUTE_BROWSE_FRAGMENT;
         routeAdapter = new RouteAdapter(
                routeViewModel,
                routeIndex -> {
                    RouteBrowseFragmentDirections.ActionRouteBrowseFragmentToRouteDetailsFragment action =
                            RouteBrowseFragmentDirections.actionRouteBrowseFragmentToRouteDetailsFragment();
                    action.setRouteIndex(routeIndex);
                    navController.navigate(action);
                },
                routeIndex -> {
                    String locationString = routeViewModel.getRoutes().get(routeIndex).getLocation();
                    locationString = locationString.replace(" ", "%20");
                    locationString = locationString.replace(",", "%2C");
                    Uri locationUri = Uri.parse("geo:0,0?q=" + locationString);

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(locationUri);

                    mainActivity.startActivity(intent);
                }
        );

        if (ActivityCompat.checkSelfPermission(
                mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(routeAdapter);
         novi = new LinearLayoutManager(getActivity());
        if( RouteViewModel.state!=null) novi.onRestoreInstanceState( RouteViewModel.state);
        binding.recyclerView.setLayoutManager(novi);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavHostFragment navHostFragment =
                (NavHostFragment) mainActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.my_nav_host_container);
        navController = navHostFragment.getNavController();    }

    @Override
    public void onPause() {
        super.onPause();
        //pamti tacno dje se skrolovalo
        Log.d("is", "tusmodjesmo");
        RouteViewModel.state = novi.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        //pamti tacno dje se skrolovalo
        binding.recyclerView.setAdapter(routeAdapter);
        novi.onRestoreInstanceState( RouteViewModel.state);
    }


    //ODMA TRAZIMO DOZVOLE
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if (isPermissionGranted) {
                        }
                    });

}