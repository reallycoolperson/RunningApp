package com.example.myapp.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

public class ReminderRepository {
    private final ExecutorService executorService;

    private final ReminderDao reminderDao;

    @Inject
    public ReminderRepository(
            ExecutorService executorService,
            ReminderDao reminderDaoDao) {
        this.executorService = executorService;
        this.reminderDao = reminderDaoDao;
    }

    public long insert(Reminder reminder)
    {
        Callable<Long> insertCallable = () -> reminderDao.insert(reminder);
        long rowId = 0;
        Future<Long> future = executorService.submit(insertCallable);

        try {
            rowId = future.get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return rowId;
    }

    public void delete(Reminder reminder)
    {
        executorService.submit(() ->
        {
            reminderDao.delete(reminder);
        });
    }

    public List<Reminder> getAll(String username)
    {
        return  reminderDao.getAll(username);
    }

    public LiveData<List<Reminder>> getAllLiveData(String username)
    {
        return  reminderDao.getAllLiveData(username);
    }

    public List<Reminder> getAllByOrder(String username) {
        return  reminderDao.getAllByOrder(username);
    }

}
