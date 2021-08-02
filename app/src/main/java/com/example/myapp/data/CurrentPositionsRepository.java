package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class CurrentPositionsRepository {

    private final ExecutorService executorService;

    private final CurrentPositionsDao currentPositionsDao;

    @Inject
    public CurrentPositionsRepository(
            ExecutorService executorService,
            CurrentPositionsDao currentPositionsDao) {
        this.executorService = executorService;
        this.currentPositionsDao = currentPositionsDao;
    }


    public long insert(CurrentPositions currentPositions)
    {
        return currentPositionsDao.insert(currentPositions);
    }

    public void update_current_positions(String positions, int id) {
        currentPositionsDao.update_current_positions(positions, id);
    }

    public void delete_current_positions(int id)
    {
        currentPositionsDao.delete_current_positions(id);
    }

    public List<CurrentPositions> getAll()
    {
        return  currentPositionsDao.getAll();
    }

}


