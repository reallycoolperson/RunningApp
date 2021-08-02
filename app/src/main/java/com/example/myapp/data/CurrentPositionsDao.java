package com.example.myapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CurrentPositionsDao {

    @Insert
    long insert(CurrentPositions currentPositions);

    @Query("UPDATE currentPositions SET all_current_positions=:positions WHERE idCurrentPositions=:id")
    void update_current_positions(String positions, int id);

    @Query("DELETE FROM CurrentPositions WHERE idCurrentPositions=:id")
    void delete_current_positions(int id);

    @Query("SELECT * FROM CurrentPositions")
    List<CurrentPositions> getAll();





}
