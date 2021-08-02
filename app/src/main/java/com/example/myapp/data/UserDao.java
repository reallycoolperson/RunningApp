package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllLiveDataUsers();

    @Query("SELECT * FROM User WHERE username = :usern")
    List<User> getFullname(String usern);

}