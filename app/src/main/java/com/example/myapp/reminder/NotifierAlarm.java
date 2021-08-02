package com.example.myapp.reminder;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.data.Reminder;
import com.example.myapp.weather.OpenWeatherMapService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.concurrent.Semaphore;


public class NotifierAlarm extends BroadcastReceiver
{


    public static ActivityWithDrawer activity;
    private OpenWeatherMapService weatherMapService;
    private String weather = "weather information";
    private Semaphore wait_for_weather;
    private Context context;
    private PendingIntent intent2;
    private FusedLocationProviderClient locationProviderClient;
    private NotificationChannel channel;
    private String mes;
    private Uri alarmsound;
    private ReminderViewModel reminderViewModel;
    private NotificationCompat.Builder builder;
    private static String CHANNEL_ID_REMINDER = "channel_reminder_notification";
    private static String CHANNEL_NAME = "chanel_for_reminder";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        reminderViewModel = new ViewModelProvider(activity).get(ReminderViewModel.class);
        SharedPreferences preferences = activity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);

       String username =  preferences.getString("username", "");
        this.context = context;
        wait_for_weather = new Semaphore(0);
        weatherMapService = new OpenWeatherMapService(activity);
        weatherMapService.callback = new OpenWeatherMapService.Callback2()
        {
            @Override
            public void message_sent(String mes, double lat, double lon)
            {
                weather_message_received(mes, lat, lon);
            }
        };
        boolean flag = true;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("lala", "Nemate dozvolu za lokaciju");
            flag = false; //nemamo dozvolu
        } else
        {
            locationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context);

       /* Task<Location> task = locationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken());*/

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(3000); // na 3s
            mLocationRequest.setSmallestDisplacement(1);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
        }
        Log.d("lala", "alarmm ");
        ReminderViewModel reminderViewModel = new ViewModelProvider(activity).get(ReminderViewModel.class);

        String message = intent.getStringExtra("Message");
        mes = message;
        Date date = new Date(intent.getStringExtra("RemindDate"));
        long id = intent.getLongExtra("id", 3);

        Reminder reminder = new Reminder(id, date, message, username);

        Log.d("lala", "alarmm " + reminder.getIdReminder() + reminder.getMessage() + reminder.getDate().toString());

        reminderViewModel.delete(reminder);

        alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent1 = new Intent(context, ReminderFragment.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent1);

        intent2 = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);



         alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

         intent1 = new Intent(context,ReminderFragment.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent1);

         intent2 = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(context);

         channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01","hello", NotificationManager.IMPORTANCE_HIGH);
        }

        if(flag==false)
        {
            Notification notification = builder.setContentTitle(activity.getResources().getString(R.string.reminder_title))
                    .setContentText(mes).setAutoCancel(true)
                    .setSubText(activity.getResources().getString(R.string.no_permission_notification))
                    .setSound(alarmsound).setSmallIcon(R.drawable.runing_logo)
                    .setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(1, notification);
        }

    }

    LocationCallback mLocationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            super.onLocationResult(locationResult);
            Location l = locationResult.getLastLocation();
            weather = reminderViewModel.getWeather();
            weatherMapService.getCurrentWeather(l.getLatitude(), l.getLongitude());

        }
    };

    public void weather_message_received(String mes2, double lat, double lon)
    {
       // wait_for_weather.release();
        locationProviderClient.removeLocationUpdates(mLocationCallback);
        Log.i("lala", "Location: " + lat + " " + lon + mes);

        Notification notification = builder.setContentTitle(activity.getResources().getString(R.string.reminder_title))
                .setContentText(mes).setAutoCancel(true)
                .setSubText(mes2)
                .setSound(alarmsound).setSmallIcon(R.drawable.runing_logo)
                .setContentIntent(intent2)
                .setChannelId("my_channel_01")
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);

    }

}

