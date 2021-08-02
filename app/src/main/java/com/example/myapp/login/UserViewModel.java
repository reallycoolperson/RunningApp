package com.example.myapp.login;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.myapp.data.User;
import com.example.myapp.data.UserRepository;

import java.util.List;


public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";
    private boolean sorted = false;

    private final LiveData<List<User>> users;

    @ViewModelInject
    public UserViewModel(
            UserRepository userRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.userRepository = userRepository;
        this.savedStateHandle = savedStateHandle;

        users = userRepository.getAllLiveDataUsers();


    }



    public List<User> getFullname(String username) { return userRepository.getFullname(username); }

    public void insertUser(User user) {
        userRepository.insert(user);
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }
}
