package com.example.myapp.statistics;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.myapp.data.Challenge;
import com.example.myapp.data.ChallengeRepository;
import com.example.myapp.data.Workout;

import java.util.ArrayList;
import java.util.List;


public class ChallengeViewModel extends ViewModel {
   private ChallengeRepository challengeRepository;
   private SavedStateHandle savedStateHandle;

    private double average_steps;
    private double average_duration;
    private double average_distance;
    private List<Workout> workouts = new ArrayList<>();


    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public double getAverage_steps() {
        return average_steps;
    }

    public void setAverage_steps(double average_steps) {
        this.average_steps = average_steps;
    }

    public double getAverage_duration() {
        return average_duration;
    }

    public void setAverage_duration(double average_duration) {
        this.average_duration = average_duration;
    }

    public double getAverage_distance() {
        return average_distance;
    }

    public void setAverage_distance(double average_distance) {
        this.average_distance = average_distance;
    }

    @ViewModelInject
    public ChallengeViewModel(
            ChallengeRepository challengeRepository,
            @Assisted SavedStateHandle savedStateHandle)
    {
        this.challengeRepository = challengeRepository;
        this.savedStateHandle = savedStateHandle;
    }

   public long insert(Challenge challenge)
    {
       return challengeRepository.insert(challenge);
    }

    public List<Challenge> getAll()
    {
        return  challengeRepository.getAll();
    }

    public LiveData<List<Challenge>> getAllLiveData()
    {
        return  challengeRepository.getAllLiveData();
    }


}
