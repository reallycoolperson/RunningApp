package com.example.myapp.routes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentRouteDetailsBinding;

public class RouteDetailsFragment extends Fragment {

    private FragmentRouteDetailsBinding binding;
    private RouteViewModel routeViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;

    public RouteDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        routeViewModel = new ViewModelProvider(mainActivity).get(RouteViewModel.class);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_ROUTE_DETAILS_FRAGMENT;



       Route selectedRoute = routeViewModel.getRoutes().get(
                RouteDetailsFragmentArgs.fromBundle(requireArguments()).getRouteIndex());

        mainActivity.getSupportActionBar().setTitle(selectedRoute.getLabel());


        binding.routeImage.setImageDrawable(selectedRoute.getImage());
        binding.routeLabel.setText(selectedRoute.getLabel());
        binding.routeName.setText(selectedRoute.getName());
        binding.routeLength.setText(selectedRoute.getLength() + "km");
        binding.routeDifficulty.setText(selectedRoute.getDifficulty());
        binding.routeDescription.setText(selectedRoute.getDescription());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public  void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_route_details_back, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.route_details_back) {
            navController.navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}