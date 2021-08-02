package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao
{

    @Insert
    long insert(Workout workout);

    @Insert
    long insert2(Workout workout);


    @Query("UPDATE Workout SET positions=:positions WHERE id=:id AND username=:username")
    void update_workout_with_positions(String positions, long id, String username);

    @Query("SELECT * FROM Workout WHERE username=:username")
    List<Workout> getAll(String username);

    @Query("SELECT * FROM Workout WHERE username=:username")
    LiveData<List<Workout>> getAllLiveData(String username);


    @Query("SELECT * FROM Workout WHERE username=:username AND label LIKE '%' || :label || '%' ")
    LiveData<List<Workout>> getAllLiveDataFilterName(String label, String username);

    @Query("SELECT * FROM Workout WHERE username=:username  ORDER BY distance DESC")
    LiveData<List<Workout>> getAllSortedLiveData(String username);



    @Query("SELECT * FROM Workout WHERE username=:username AND label LIKE '%' || :label || '%' ORDER BY distance DESC")
    LiveData<List<Workout>> getAllSortedLiveDataDistance(String label, String username);

    @Query("SELECT * FROM Workout WHERE username=:username AND label LIKE '%' || :label || '%' ORDER BY duration DESC")
    LiveData<List<Workout>> getAllSortedLiveDataDuration(String label, String username);

    @Query("SELECT * FROM Workout WHERE username=:username AND label LIKE '%' || :label || '%' ORDER BY steps DESC")
    LiveData<List<Workout>> getAllSortedLiveDataSteps(String label, String username);

    @Query("SELECT * FROM Workout WHERE username=:username AND label LIKE '%' || :label || '%' ORDER BY date DESC")
    LiveData<List<Workout>> getAllSortedLiveDataDate(String label, String username);

    @Query("SELECT id FROM Workout ORDER BY id DESC LIMIT 1")
    long getLastWorkoutId();
}
