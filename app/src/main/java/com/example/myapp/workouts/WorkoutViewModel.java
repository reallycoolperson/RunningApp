package com.example.myapp.workouts;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myapp.data.CurrentPositions;
import com.example.myapp.data.CurrentPositionsRepository;
import com.example.myapp.data.Workout;
import com.example.myapp.data.WorkoutRepository;

import java.util.Date;
import java.util.List;


public class WorkoutViewModel extends ViewModel {

    private final WorkoutRepository workoutRepository;
    private final CurrentPositionsRepository currentPositionsRepository;

    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";
    private boolean sorted = false;

    private  LiveData<List<Workout>> workouts;
    private String positions_from_chosen_workout = "";
    private String workout_info[] = new String[5];
    private String filter ="";
    public String clicked ="";
    public MutableLiveData<Integer> show_info = new MutableLiveData<>(0);

    public int workout_start = 0;
    private MutableLiveData<Integer> stepCount = new MutableLiveData<Integer>(0);


    public static String username;
    @ViewModelInject
    public WorkoutViewModel(
            WorkoutRepository workoutRepository,
            CurrentPositionsRepository currentPositionsRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.workoutRepository = workoutRepository;
        this.currentPositionsRepository = currentPositionsRepository;
        this.savedStateHandle = savedStateHandle;


        workouts = Transformations.switchMap(
                savedStateHandle.getLiveData(SORTED_KEY, false),
                sorted -> {
                    if (!sorted) {
                        return workoutRepository.getAllLiveData(username);
                    } else {
                        return workoutRepository.getAllSortedLiveData(username);
                    }
                }
        );
    }


    public MutableLiveData<Integer> getStepCount() {
        return stepCount;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount.setValue(stepCount);
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public String getClicked()
    {
        return clicked;
    }

    public void setClicked(String clicked)
    {
        this.clicked = clicked;
    }

    public void invertSorted() {
        savedStateHandle.set(SORTED_KEY, sorted = !sorted);
    }

    public void insertWorkout(Workout workout) {
        workoutRepository.insert(workout);
    }

    public LiveData<List<Workout>> getWorkoutList() {
        return workouts;
    }

    public MutableLiveData<Integer> getShow_info()
    {
        return show_info;
    }

    public void setShow_info(Integer show_info)
    {
        this.show_info.setValue(show_info);
    }

    public long insert(CurrentPositions currentPositions)
    {
        return currentPositionsRepository.insert(currentPositions);
    }

    public void update_current_positions(String positions, int id) {
        currentPositionsRepository.update_current_positions(positions, id);
    }

    public void delete_current_positions(int id)
    {
        currentPositionsRepository.delete_current_positions(id);
    }

    public List<CurrentPositions> getAll()
    {
        return  currentPositionsRepository.getAll();
    }

    public List<Workout> getAllWorkouts(String username) { return workoutRepository.getAll(username); }
    public long insert2(Workout workout) {
        return workoutRepository.insert2(workout);
    }

    public void update_workout_with_positions(String positions, long id, String username)
    {
        workoutRepository.update_workout_with_positions(positions, id, username);
    }

    public String getPositions_from_chosen_workout() {
        return positions_from_chosen_workout;
    }

    public void setPositions_from_chosen_workout(String positions_from_chosen_workout) {
        this.positions_from_chosen_workout = positions_from_chosen_workout;
    }

    public String[] getWorkout_info() {
        return workout_info;
    }

    public void setWorkout_info(String[] workout_info) {
        this.workout_info = workout_info;
    }

    public long getLastWorkoutId()
    {
        return workoutRepository.getLastWorkoutId();
    }

    public LiveData<List<Workout>> getAllLiveData(String username)
    {
        return workoutRepository.getAllLiveData(username);
    }

    public LiveData<List<Workout>> getAllLiveDataFilterName(String label, String username) {
        return workoutRepository.getAllLiveDataFilterName(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDistance(String label, String username)
    {

        return workoutRepository.getAllSortedLiveDataDistance(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDuration(String label, String username)
    {
        return workoutRepository.getAllSortedLiveDataDuration(label, username);

    }

    public LiveData<List<Workout>> getAllSortedLiveDataSteps(String label, String username)
    {
        return workoutRepository.getAllSortedLiveDataSteps(label, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveDataDate(String label, String username)
    {
        return workoutRepository.getAllSortedLiveDataDate(label, username);
    }
}
