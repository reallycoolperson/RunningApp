package com.example.myapp.workouts;

import android.app.Dialog;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;


public class DialogFilterWorkouts extends DialogFragment
{

    public static ActivityWithDrawer myactivity;
    public Dialog d;
    private static WorkoutViewModel workoutViewModel;
    private RecyclerView recyclerView;
    private ActivityWithDrawer mainActivity;
    private String login_username;
    private WorkoutAdapter workoutAdapter;
    private SharedPreferences preferences;
    private String username;


    public DialogFilterWorkouts(WorkoutAdapter workoutAdapter)
    {
        this.workoutAdapter = workoutAdapter;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_filter_workouts, container, false);

        RadioButton r;
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radio_group_sort);

        username = preferences.getString("username", "");
        switch (workoutViewModel.clicked)
        {
            case "steps":
                r = (RadioButton) view.findViewById(R.id.radio_steps);
                r.setChecked(true);
                break;

            case "distance":
                r = (RadioButton) view.findViewById(R.id.radio_distance);
                r.setChecked(true);
                break;

            case "duration":
                r = (RadioButton) view.findViewById(R.id.radio_duration);
                r.setChecked(true);
                break;

            case "date":
                r = (RadioButton) view.findViewById(R.id.radio_date);
                r.setChecked(true);
                break;
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radio_steps:
                        Log.d("lala", "steps clicked");
                        workoutViewModel.clicked = "steps";


                        sort_recycler_view(workoutViewModel.getFilter());
                        // TODO Something
                        break;
                    case R.id.radio_distance:
                        workoutViewModel.clicked = "distance";
                        sort_recycler_view(workoutViewModel.getFilter());

                        break;
                    case R.id.radio_duration:
                        workoutViewModel.clicked = "duration";
                        sort_recycler_view(workoutViewModel.getFilter());

                        break;
                    case R.id.radio_date:
                        workoutViewModel.clicked = "date";
                        sort_recycler_view(workoutViewModel.getFilter());
                        break;

                    case R.id.radio_all:
                    {
                        workoutViewModel.clicked = "";
                        workoutViewModel.setFilter("");
                        sort_recycler_view("");
                        workoutViewModel.setShow_info(0);

                    }
                }
            }
        });


        final androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search_workout);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                Log.d("lala", "s fitlering" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {

                Log.d("lala", "s fitlering2" + newText);
                workoutViewModel.setFilter(newText);
                if (!newText.equals("")) workoutViewModel.setShow_info(1);
                sort_recycler_view(newText);


                return false;
            }
        });


        //////////////////////////CLOSE ADD DIALOG//////////////////////////////////////
        Button button_close = (Button) view.findViewById(R.id.button_close_filter_dialog);
        button_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //sort_recycler_view(workoutViewModel.getFilter());

                dismiss();
                //Log.d("lala", "filter: " + workoutViewModel.getFilter());
            }
        });


        return view;
    }

    public void sort_recycler_view(String newText)
    {
        if (!workoutViewModel.clicked.equals("")) workoutViewModel.setShow_info(1);
        switch (workoutViewModel.clicked)
        {
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

            case "date":
                workoutViewModel.getAllSortedLiveDataDate(newText, username).observe(getViewLifecycleOwner(), searched -> {
                    workoutAdapter.setWorkoutList(searched);
                });
                break;


            case "steps":
                workoutViewModel.getAllSortedLiveDataSteps(newText, username).observe(getViewLifecycleOwner(), searched -> {

                    workoutAdapter.setWorkoutList(searched);
                });
                break;
        }
    }

}











