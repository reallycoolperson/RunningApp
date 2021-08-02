package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class ChallengeRepository {

    private final ExecutorService executorService;

    private final ChallengeDao challengeDao;

    @Inject
    public ChallengeRepository(
            ExecutorService executorService,
            ChallengeDao challengeDao) {
        this.executorService = executorService;
        this.challengeDao = challengeDao;
    }

    public long insert(Challenge challenge)
    {
        executorService.submit(() ->
        {
            challengeDao.insert(challenge);
        });
        return  0;
    }

    public List<Challenge> getAll()
    {
         return  challengeDao.getAll();
    }

    public LiveData<List<Challenge>> getAllLiveData()
    {
        return  challengeDao.getAllLiveData();
    }

}
