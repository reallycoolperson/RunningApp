package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    long insert(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM Reminder WHERE username=:username")
    List<Reminder> getAll(String username);

    @Query("Select * from Reminder WHERE username=:username order by date")
    LiveData<List<Reminder>> getAllLiveData(String username);

    @Query("Select * from Reminder WHERE username=:username order by date")
    public List<Reminder> getAllByOrder(String username);
}
