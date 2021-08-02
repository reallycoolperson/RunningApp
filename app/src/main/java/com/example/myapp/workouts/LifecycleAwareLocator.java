package com.example.myapp.workouts;

import androidx.lifecycle.DefaultLifecycleObserver;

import javax.inject.Inject;

public class LifecycleAwareLocator implements DefaultLifecycleObserver {

    @Inject
    public LifecycleAwareLocator() {

    }


}
