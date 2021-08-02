package com.example.myapp.geofences;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.workouts.WorkoutStartFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";
    int id = 2;
    private NotificationCompat.Builder builder;
    private PendingIntent intent2;
    public static ActivityWithDrawer activity;
    private String entered_in = "";



    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d("lala", "onReceive: Error receiving geofence event...");
            return;
        }

        SharedPreferences sharedPreferences = activity
                .getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String prov = "FALSE";
        if (sharedPreferences != null) {
            prov = sharedPreferences.getString(ActivityWithDrawer.WORKOUT_IN_PROGESS, "FALSE");
        }

        Log.d("lala", "zvonce " + prov);
        if (prov.equals("TRUE")) {
            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : geofenceList) {
                Log.d("lala", "onReceive: " + geofence.getRequestId());
                entered_in = geofence.getRequestId();
                if (entered_in.equals("GEOFENCE_ID1"))
                    entered_in = activity.getResources().getString(R.string.vukov_spomenik);
                else if (entered_in.equals("GEOFENCE_ID0"))
                    entered_in = activity.getResources().getString(R.string.bulevar_kralja_aleksandra);
                else entered_in = activity.getResources().getString(R.string.pravni_fakultet);
            }
//        Location location = geofencingEvent.getTriggeringLocation();
            int transitionType = geofencingEvent.getGeofenceTransition();

            NotificationChannel channel;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel("my_channel_02", "hello2", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);

            }


            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    // Toast.makeText(context, "ENTERED", Toast.LENGTH_SHORT).show();
                    Log.d("lala", "entered yeeyyy");

                    PendingIntent pendingIntent = PendingIntent.getActivity(activity, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    Notification notification = new NotificationCompat.Builder(activity, "my_channel_02").setContentTitle(activity.getResources().getString(R.string.geofence_title))
                            .setContentText(entered_in).setAutoCancel(true)
                            .setSmallIcon(R.drawable.runing_logo)
                            .setSound(alarmsound)
                            .setContentIntent(pendingIntent)
                            .build();
                    id++;
                    notificationManager.notify(id, notification);
                    break;

            }

        }
    }


}
