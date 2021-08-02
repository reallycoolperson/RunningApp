package com.example.myapp.workouts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("lala", "Service is stooped");
        //context.startService(new Intent(context, WorkoutService.class));;
    }
}
