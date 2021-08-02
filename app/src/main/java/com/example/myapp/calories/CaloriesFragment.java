package com.example.myapp.calories;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentCaloriesBinding;
import com.example.myapp.routes.RouteViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.text.ParseException;


public class CaloriesFragment extends Fragment {
    private FragmentCaloriesBinding binding;
    private CaloriesViewModel caloriesViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;

    public CaloriesFragment() {
//        getLifecycle().addObserver(new LifecycleAwareLogger(
//                MainActivity.LOG_TAG,
//                CaloriesFragment.class.getSimpleName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (ActivityWithDrawer) requireActivity();
        caloriesViewModel = new ViewModelProvider(this).get(CaloriesViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCaloriesBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.calories_toolbar_title);

        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_CALORIES_FRAGMENT;


        caloriesViewModel.getCaloriesBurned().observe(getViewLifecycleOwner(), caloriesBurned -> {
            if (caloriesBurned != -1) {
                String prefix = getResources().getString(R.string.calories_burned);
                binding.burned.setText(prefix + ": " + caloriesBurned + " kcal");
            }
        });

        caloriesViewModel.getCaloriesNeeded().observe(getViewLifecycleOwner(), caloriesNeeded -> {
            if (caloriesNeeded != -1) {
                String prefix = getResources().getString(R.string.calories_needed);
                binding.needed.setText(prefix + ": " + caloriesNeeded + " kcal");
            }
        });

        String[] metStrings = getResources().getStringArray(R.array.met_strings);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                mainActivity,
                android.R.layout.simple_list_item_1,
                metStrings);
        binding.spinner.setAdapter(arrayAdapter);

        binding.calculate.setOnClickListener(v -> {

            try {
                double weight = fetchNumber(binding.weight).doubleValue();
                double height = fetchNumber(binding.height).doubleValue();
                int age = fetchNumber(binding.age).intValue();

                if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(
                            mainActivity,
                            R.string.calories_toast_text_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isMale = binding.male.isChecked();

                double duration = fetchNumber(binding.duration).doubleValue();

                TypedArray metValues = getResources().obtainTypedArray(R.array.met_values);
                double met = metValues.getFloat(binding.spinner.getSelectedItemPosition(), 0);
                metValues.recycle();

                caloriesViewModel.updateValues(weight, height, age, isMale, duration, met);

            } catch (ParseException ignored) {
                // ignore
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

    private Number fetchNumber(TextInputLayout textInputLayout) throws ParseException {
        Number result;
        try {
            result = NumberFormat.getInstance().parse(
                    textInputLayout.getEditText().getText().toString());
        } catch (ParseException pe) {
            Toast.makeText(
                    mainActivity,
                    R.string.calories_toast_text_error,
                    Toast.LENGTH_SHORT).show();
            textInputLayout.getEditText().requestFocus();
            throw pe;
        }
        return result;
    }
}