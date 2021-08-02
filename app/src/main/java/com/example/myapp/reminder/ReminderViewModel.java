package com.example.myapp.reminder;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.myapp.data.Reminder;
import com.example.myapp.data.ReminderRepository;

import java.util.List;



public class ReminderViewModel extends ViewModel
{
    private ReminderRepository reminderRepository;
    private SavedStateHandle savedStateHandle;
    private String weather;

    @ViewModelInject
    public ReminderViewModel(
            ReminderRepository reminderRepository,
            @Assisted SavedStateHandle savedStateHandle)
    {
        this.reminderRepository = reminderRepository;
        this.savedStateHandle = savedStateHandle;
    }


    public String getWeather()
    {
        return weather;
    }

    public void setWeather(String weather)
    {
        this.weather = weather;
    }

    public long insert(Reminder reminder)
    {
        return reminderRepository.insert(reminder);
    }

    public List<Reminder> getAll(String username)
    {
        return reminderRepository.getAll(username);
    }

    public LiveData<List<Reminder>> getAllLiveData(String username)
    {
        return reminderRepository.getAllLiveData(username);
    }

    public List<Reminder> getAllByOrder(String username)
    {
        return reminderRepository.getAllByOrder(username);
    }

    public void delete(Reminder reminder)
    {
        reminderRepository.delete(reminder);
    }

}
