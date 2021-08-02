package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChallengeDao {

    @Insert
    long insert(Challenge challenge);

    @Query("SELECT * FROM Challenge")
    List<Challenge> getAll();

    @Query("SELECT * FROM Challenge")
    LiveData<List<Challenge>> getAllLiveData();

}
