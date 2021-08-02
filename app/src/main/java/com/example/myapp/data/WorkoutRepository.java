package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Query;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class WorkoutRepository
{

    private final ExecutorService executorService;

    private final WorkoutDao workoutDao;

    @Inject
    public WorkoutRepository(
            ExecutorService executorService,
            WorkoutDao workoutDao)
    {
        this.executorService = executorService;
        this.workoutDao = workoutDao;
    }

    public void insert(Workout workout)
    {
        executorService.submit(() ->
        {
            workoutDao.insert(workout);
        });
    }

    public void update_workout_with_positions(String positions, long id, String username)
    {
        workoutDao.update_workout_with_positions(positions, id, username);
    }


    public long insert2(Workout workout)
    {
        return workoutDao.insert2(workout);
    }

    public List<Workout> getAll(String username)
    {
        return workoutDao.getAll(username);
    }

    public LiveData<List<Workout>> getAllLiveData(String username)
    {
        return workoutDao.getAllLiveData(username);
    }

    public LiveData<List<Workout>> getAllSortedLiveData(String username)
    {
        return workoutDao.getAllSortedLiveData(username);
    }

    public long getLastWorkoutId()
    {
        return workoutDao.getLastWorkoutId();
    }

    public LiveData<List<Workout>> getAllLiveDataFilterName(String label, String username)
    {
        return workoutDao.getAllLiveDataFilterName(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDistance(String label, String username)
    {
     return workoutDao.getAllSortedLiveDataDistance(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDuration(String label, String username)
    {
        return workoutDao.getAllSortedLiveDataDuration(label, username);

    }

    public LiveData<List<Workout>> getAllSortedLiveDataSteps(String label, String username)
    {
        return workoutDao.getAllSortedLiveDataSteps(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDate(String label, String username)
    {
        return workoutDao.getAllSortedLiveDataDate(label, username);
    }


}
