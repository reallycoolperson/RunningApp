package com.example.myapp.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.data.Challenge;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.FragmentStatisticsBinding;
import com.example.myapp.routes.RouteViewModel;
import com.example.myapp.workouts.DateTimeUtil;
import com.example.myapp.workouts.WorkoutViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;




public class StatisticsFragment extends Fragment
{


    private FragmentStatisticsBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private ChallengeAdapter challengeAdapter;
    private ChallengeViewModel challengeViewModel;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences preferences;
    private String username;

    public StatisticsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        challengeViewModel = new ViewModelProvider(mainActivity).get(ChallengeViewModel.class);
        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        username = preferences.getString("username", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_STATISTICS_FRAGMENT;

        mainActivity.getSupportActionBar().setTitle(R.string.statistics_toolbar);

        final List<Challenge>[] challenges = new List[]{new ArrayList<>()};
        challengeAdapter = new ChallengeAdapter(challengeViewModel);

        binding.steps.setText("0");
        binding.distance.setText("0");
        binding.duration.setText("0");

        workoutViewModel.getAllLiveData(username).observe(getViewLifecycleOwner(), list -> {
            Log.d("lala", "average calculation");
            if (list != null && list.size() != 0)
            {
                challengeViewModel.setWorkouts(list);
                double asteps = 0;
                double aduration = 0;
                double adistance = 0;
                int n = 0;
                for (int i = 0; i < list.size(); i++)
                {
                    Log.d("lala", "duration" +  String.format("%s min", DateTimeUtil.realMinutesToString(
                            list.get(i).getDuration())));
                    Log.d("lala", "distance" + list.get(i).getDistance());
                    Log.d("lala", "steps" + list.get(i).getSteps());

                    asteps = asteps + list.get(i).getSteps();
                    aduration = aduration + list.get(i).getDuration();
                    adistance = adistance + list.get(i).getDistance();
                    n++;
                }
                Log.d("lala", "adur: " + aduration);
                aduration = aduration / n;
                Log.d("lala", "adur: " + aduration);

                adistance = adistance / n;
                challengeViewModel.setAverage_steps(asteps);
                challengeViewModel.setAverage_duration(aduration);
                challengeViewModel.setAverage_distance(adistance);
                binding.averageSteps.setProgress((float) ((asteps) / 2000) * 100);
                binding.averageDistance.setProgress((float) ((adistance) / 3) * 100);
                binding.averageDuration.setProgress((float) ((aduration) / 30) * 100);
                binding.steps.setText(String.format("%.2f", asteps));
                binding.distance.setText(String.format("%.2f km", adistance));
                binding.duration.setText(String.format("%s min", DateTimeUtil.realMinutesToString((aduration))));

            }
        });


        Log.d("lala", "prazno, dodajemo");
        String[] names = getResources().getStringArray(R.array.challenges_names);
        String[] descriptions = getResources().getStringArray(R.array.challenges_description);
        String[] types = getResources().getStringArray(R.array.challenges_types);
        int[] numbers = getResources().getIntArray(R.array.challenges_number);

        for (int i = 0; i < names.length; i++)
        {
            Challenge challenge = new Challenge(0, names[i], descriptions[i], types[i], numbers[i]);
            challenges[0].add(challenge);
        }

        Semaphore sem = new Semaphore(1);
        try
        {
            sem.acquire();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        final List<Workout>[] lista = new List[]{new ArrayList<>()};
        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                lista[0] = workoutViewModel.getAllWorkouts(username);
                sem.release();
            }
        });

        try
        {
            sem.acquire(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        sem.release();
        challengeAdapter.setWorkouts(lista[0]);
        challengeAdapter.setChallenges(challenges[0]);
        binding.recyclerViewChallenges.setHasFixedSize(true);
        binding.recyclerViewChallenges.setAdapter(challengeAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewChallenges.setLayoutManager(linearLayoutManager);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}