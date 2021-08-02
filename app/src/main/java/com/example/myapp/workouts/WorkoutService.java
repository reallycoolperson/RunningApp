package com.example.myapp.workouts;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkoutService extends LifecycleService {

    public static final String INTENT_ACTION_START = "rs.ac.bg.etf.running.workouts.START";
    public static final String INTENT_ACTION_POWER = "rs.ac.bg.etf.running.workouts.POWER";

    private GoogleMap  mMap;
    private static final String NOTIFICATION_CHANNEL_ID = "workout-notification-channel";
    private static final int NOTIFICATION_ID = 1;
    private FusedLocationProviderClient locationProviderClient;
    private boolean serviceStarted = false;
    private String positions = "";
    public static ActivityWithDrawer activity;
    private static int AVERAGE_STEP_SIZE = 60; //60CM JEDAN KORAK

    private LatLng previous_locaton = null;
    private LatLng current_location;
    private double stepCount;
    @Inject
    public LifecycleAwareLocator locator;
    static WorkoutViewModel workoutViewModel;


    @Override
    public void onCreate() {

        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();
        getLifecycle().addObserver(locator);
    }

    Context context = null;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");


        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());
        context = this;
        serviceStarted = true;
        getLocation(this);


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        broadcastNotification(getBaseContext(), positions);
        serviceStarted = false;
        locationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.workout_notification_channel_name))
                .build();
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
    }

    private Notification getNotification() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setAction(MainActivity.INTENT_ACTION_WORKOUT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_directions_run_black_24)
                .setContentTitle(getString(R.string.workout_notification_content_title))
                .setContentText(getString(R.string.workout_notification_content_text))
                .setContentIntent(pendingIntent)
                .setColorized(true)
                .setColor(ContextCompat.getColor(this, R.color.teal_200))
                .build();
    }






    //////////////////////////////////LOCATION///////////////////////////////////////////////////////

    public void getLocation(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("lala", "permission not granted");
            return;
        }

         locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // na 3s
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());

    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

                super.onLocationResult(locationResult);
                List<Location> locationList = locationResult.getLocations();
                for (Location l : locationList) {
                    String position = l.getLatitude() + "," + l.getLongitude();
                    positions = positions + position + ";";
                    Log.i("lala", "Location: " + l.getLatitude() + " " + l.getLongitude());

                    if(previous_locaton==null)
                    {
                        previous_locaton=new LatLng(l.getLatitude(), l.getLongitude());
                    }
                    else
                    {
                        current_location=new LatLng(l.getLatitude(), l.getLongitude());
                       double dis =  distance_two_locations(previous_locaton, current_location);
                        Log.d("lala", "distance= " + dis);

                        if(dis<13)
                        {
                            //Log.d("lala", "steps= " + (dis*100)/60);
                            int uk = workoutViewModel.getStepCount().getValue() + (int)Math.round((dis*100)/AVERAGE_STEP_SIZE);;
                            workoutViewModel.setStepCount(uk);
                         }

                        previous_locaton = current_location;
                    }

                //    Location currentLocation = locationResult.getLastLocation();

            }
        }
    };

    //SLANJE POSITIONS
    private void broadcastNotification(Context context, String positions){
        Log.d("lala", "called" + positions);
        Intent intent = new Intent("NOTIFICATION_POSITIONS");
        intent.putExtra("MESSAGE_POSITIONS", positions);
        context.sendBroadcast(intent);
    }
    double pi =  Math.PI;



    private double distance_two_locations(LatLng l1, LatLng l2) {
        double lat1 = l1.latitude;
        double lon1 = l1.longitude;
        double lat2 = l2.latitude;
        double lon2 = l2.longitude;
        double φ1 = lat1 * pi / 180;
        double φ2 = lat2 * pi/ 180;
        double Δλ = (lon2 - lon1) * pi / 180;
        double R = 6371e3;
        double d = Math.acos(Math.sin(φ1) * Math.sin(φ2) + Math.cos(φ1) * Math.cos(φ2) * Math.cos(Δλ)) * R;

        return d;
    }

}
