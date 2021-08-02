package com.example.myapp.workouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.NavGraphDirections;
import com.example.myapp.R;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.FragmentWorkoutListBinding;
import com.example.myapp.routes.RouteViewModel;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class WorkoutListFragment extends Fragment {

    private FragmentWorkoutListBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private DialogFilterWorkouts dialogFilterWorkouts;
    private WorkoutAdapter workoutAdapter;
    private SharedPreferences preferences;
    public WorkoutListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mainActivity = (ActivityWithDrawer) requireActivity();
        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        String user_logged_in = preferences.getString("username", "");

        WorkoutViewModel.username = user_logged_in;
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_WORKOUT_LIST_FRAGMENT;

        mainActivity.getSupportActionBar().setTitle(R.string.workout_create_toolbar_title);

        workoutViewModel.getShow_info().observe(getViewLifecycleOwner(), integer -> {
            if(integer == 1)
            {
                if(workoutViewModel.clicked.equals("") && workoutViewModel.getFilter().equals(""))
                    binding.sortInfo.setVisibility(View.INVISIBLE);
                else if(!workoutViewModel.clicked.equals("") && !workoutViewModel.getFilter().equals(""))
                {
                    binding.sortInfo.setVisibility(View.VISIBLE);
                    binding.sortInfo.setText(mainActivity.getResources().getString(R.string.filter_and_sorted1));
                }
                else if(!workoutViewModel.clicked.equals(""))
                {
                    binding.sortInfo.setVisibility(View.VISIBLE);
                    binding.sortInfo.setText(mainActivity.getResources().getString(R.string.sorted1));
                }
                else if(!workoutViewModel.getFilter().equals(""))
                {
                    binding.sortInfo.setVisibility(View.VISIBLE);
                    binding.sortInfo.setText(mainActivity.getResources().getString(R.string.filtered1));
                }
            }
            else
            {
                binding.sortInfo.setVisibility(View.INVISIBLE);
            }
        });

     /*   binding.toolbar.inflateMenu(R.menu.menu_workout_list_options);
        binding.toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.workout_menu_item_sort:
                    workoutViewModel.invertSorted();
                    return true;
            }
            return false;
        });*/

         workoutAdapter = new WorkoutAdapter(null);

        workoutAdapter.setCallback(new WorkoutAdapter.Callback() {

            @Override
            public void onWorkoutClick(Workout workout) {

                Log.d("lala", "positions on click: " + workout.getPositions());
                workoutViewModel.setPositions_from_chosen_workout(workout.getPositions());
                String info[] = new String[5];
                info[0] = workout.getLabel();
                info[1] = DateTimeUtil.getSimpleDateFormat().format(
                        workout.getDate());
                info[2] =String.format("%.2f km",
                        workout.getDistance());
                info[3] = String.format("%s min", DateTimeUtil.realMinutesToString(
                        workout.getDuration()));
                info[4] = String.format("%s min/km", DateTimeUtil.realMinutesToString(
                        workout.getDuration() / workout.getDistance()));

                workoutViewModel.setWorkout_info(info);
                navController.navigate(NavGraphDirections.actionGlobalWorkoutOnMapFragment2());
            }
        });
        workoutAdapter.setWorkoutViewModel(workoutViewModel);
        workoutViewModel.getWorkoutList().observe(
                getViewLifecycleOwner(),
                lista->{
                    String newText = workoutViewModel.getFilter();
                    String username = WorkoutViewModel.username;
                    //za rotaciju i kad dodamo novi workout da se sortira onako kako je izbrano
                    switch (workoutViewModel.clicked) {
                        case "":
                            workoutViewModel.getAllLiveDataFilterName(newText, username).observe(getViewLifecycleOwner(), searched -> {

                                workoutAdapter.setWorkoutList(searched);
                            });
                            break;

                        case "distance":
                            workoutViewModel.getAllSortedLiveDataDistance(newText, username).observe(getViewLifecycleOwner(), searched -> {
                                workoutAdapter.setWorkoutList(searched);
                            });
                            break;

                        case "duration":
                            workoutViewModel.getAllSortedLiveDataDuration(newText, username).observe(getViewLifecycleOwner(), searched -> {

                                workoutAdapter.setWorkoutList(searched);
                            });
                            break;


                        case "steps":
                            workoutViewModel.getAllSortedLiveDataSteps(newText, username).observe(getViewLifecycleOwner(), searched -> {

                                workoutAdapter.setWorkoutList(searched);
                            });
                            break;

                        case "date":
                            workoutViewModel.getAllSortedLiveDataDate(newText, username).observe(getViewLifecycleOwner(), searched -> {

                                workoutAdapter.setWorkoutList(searched);
                            });
                            break;
                        default: workoutAdapter.setWorkoutList(lista);
                            Log.d("lala", "called");
                    }
                });

        binding.recyclerView.setAdapter(workoutAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.inflate(R.menu.menu_workout_list_fab);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.workout_fab_create:
                    navController.navigate(WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutCreateFragment());
                    return false;
                case R.id.workout_fab_start:
                     navController.navigate(WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutStartFragment());
                    return false;
            }
            return true;
        });

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
    public  void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_workout_list_options, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.workout_menu_item_sort) {
                    //workoutViewModel.invertSorted();
            dialogFilterWorkouts = new DialogFilterWorkouts(workoutAdapter);
            dialogFilterWorkouts.show(getActivity().getSupportFragmentManager(), "myTagFilter");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}