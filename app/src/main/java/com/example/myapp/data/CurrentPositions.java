package com.example.myapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CurrentPositions {

    @PrimaryKey(autoGenerate = true)
    private long idCurrentPositions;

    public CurrentPositions(long idCurrentPositions, String all_current_positions) {
        this.idCurrentPositions = idCurrentPositions;
        this.all_current_positions = all_current_positions;
    }


    private String all_current_positions;

    public long getIdCurrentPositions() {
        return idCurrentPositions;
    }

    public void setIdCurrentPositions(long idCurrentPositions) {
        this.idCurrentPositions = idCurrentPositions;
    }

    public String getAll_current_positions() {
        return all_current_positions;
    }

    public void setAll_current_positions(String all_current_positions) {
        this.all_current_positions = all_current_positions;
    }
}
