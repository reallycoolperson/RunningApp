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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.FragmentWorkoutCreateBinding;
import com.example.myapp.routes.RouteViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkoutCreateFragment extends Fragment {

    public static final String REQUEST_KEY = "date-picker-request-key";

    private FragmentWorkoutCreateBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;

    private SharedPreferences preferences;
    private String username;

    public WorkoutCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        setHasOptionsMenu(true);

        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutCreateBinding.inflate(inflater, container, false);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_WORKOUT_CREATE_FRAGMENT;
        mainActivity.getSupportActionBar().setTitle(R.string.create_workout_toolbar);

        username = preferences.getString("username", "");
        Log.d("ulogovan", username);

        binding.workoutDateEditText.setOnClickListener(
                view -> new DatePickerFragment().show(getChildFragmentManager(), null));

        getChildFragmentManager().setFragmentResultListener(REQUEST_KEY, this,
                (requestKey, result) -> {
                    Date date = (Date) result.getSerializable(DatePickerFragment.SET_DATE_KEY);
                    String dateForEditText = DateTimeUtil.getSimpleDateFormat().format(date);
                    binding.workoutDate.getEditText().setText(dateForEditText);
                });

        binding.create.setOnClickListener(view -> {
            Date date = (Date) parse(binding.workoutDate, DateTimeUtil.getSimpleDateFormat());
            String label = (String) parse(binding.workoutLabel, null);
            Number distance = (Number) parse(binding.workoutDistance, NumberFormat.getInstance());
            Number duration = (Number) parse(binding.workoutDuration, NumberFormat.getInstance());

            if (!(date == null || label == null || distance == null || duration == null)) {
                workoutViewModel.insertWorkout(new Workout(
                        0,
                        date,
                        label,
                        distance.doubleValue(),
                        duration.doubleValue(),
                        0,
                        username
                ));
                navController.navigateUp();
            }
        });

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

    private Object parse(TextInputLayout textInputLayout, Format format) {
        Object result;
        try {
            String inputString = textInputLayout.getEditText().getText().toString();
            if (!inputString.equals("")) {
                if (format != null) {
                    result = format.parseObject(inputString);
                } else {
                    result = inputString;
                }
                textInputLayout.setError(null);
            } else {
                textInputLayout.setError(mainActivity.getResources()
                        .getString(R.string.workout_create_edit_text_error_empty));
                result = null;
            }
        } catch (ParseException e) {
            textInputLayout.setError(mainActivity.getResources()
                    .getString(R.string.workout_create_edit_text_error_format));
            return null;
        }
        return result;
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