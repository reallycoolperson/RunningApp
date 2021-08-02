package com.example.myapp.di;

import android.content.Context;

import com.example.myapp.data.ChallengeDao;
import com.example.myapp.data.CurrentPositionsDao;
import com.example.myapp.data.PlaylistDao;
import com.example.myapp.data.ReminderDao;
import com.example.myapp.data.RunDatabase;
import com.example.myapp.data.UserDao;
import com.example.myapp.data.WorkoutDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;


@Module
@InstallIn(SingletonComponent.class)
public interface DatabaseModule {

    @Provides
    static WorkoutDao provideWorkoutDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).workoutDao();
    }

    @Provides
    static UserDao provideUserDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).userDao();
    }

    @Provides
    static PlaylistDao providePlaylistDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).playlistDao();
    }

    @Provides
    static CurrentPositionsDao provideCurrentPositionsDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).currentPositionsDao();
    }

    @Provides
    static ChallengeDao provideChallengeDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).challengeDao();
    }

    @Provides
    static ReminderDao provideReminderDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).reminderDao();
    }
}
