package com.example.myapp.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class UserRepository {

    private final ExecutorService executorService;

    private final UserDao userDao;

    @Inject
    public UserRepository(
            ExecutorService executorService,
            UserDao userDao) {
        this.executorService = executorService;
        this.userDao = userDao;
    }

    public void insert(User user) {
        executorService.submit(() -> userDao.insert(user));
    }


    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllLiveDataUsers() {
        return userDao.getAllLiveDataUsers();
    }

    public List<User> getFullname(String username) { return userDao.getFullname(username); }


}