package com.example.myapp.reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.R;
import com.example.myapp.data.Reminder;
import com.example.myapp.databinding.FragmentReminderBinding;
import com.example.myapp.routes.RouteViewModel;
import com.example.myapp.workouts.WorkoutViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;




public class ReminderFragment extends Fragment
{


    private FragmentReminderBinding binding;
    private WorkoutViewModel workoutViewModel;
    private ReminderViewModel reminderViewModel;
    private NavController navController;
    private ActivityWithDrawer mainActivity;
    private LinearLayoutManager linearLayoutManager;
    private ReminderAdapter reminderAdapter;
    private Dialog dialog;
    private SharedPreferences preferences;
    private String username;
    SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String date = "";

    public ReminderFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = (ActivityWithDrawer) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        reminderViewModel = new ViewModelProvider(mainActivity).get(ReminderViewModel.class);
        preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentReminderBinding.inflate(inflater, container, false);
        mainActivity.getSupportActionBar().setTitle(R.string.reminder_toolbar);
        RouteViewModel.current_fragment = RouteViewModel.CURRENT_PAGE_REMINDER_FRAGMENT;

        username = preferences.getString("username", "");
        binding.floatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                add_reminder();
            }
        });


        reminderAdapter = new ReminderAdapter(reminderViewModel);
        reminderAdapter.setCallback(new ReminderAdapter.Callback()
        {
            @Override
            public void cancelAlarm(long id)
            {
                Log.d("lala", "Cancel" + id);
                AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

                Intent my_intent = new Intent(mainActivity, NotifierAlarm.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, Integer.parseInt(id + ""), my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }
        });
        reminderViewModel.getAllLiveData(username).observe(
                getViewLifecycleOwner(),
                reminders -> {
                    reminderAdapter.setReminders(reminders);
                    if (reminders != null && reminders.size() > 0)
                    {
                        reminderAdapter.setReminders(reminders);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.empty.setVisibility(View.INVISIBLE);
                    } else
                    {
                        binding.recyclerView.setVisibility(View.INVISIBLE);
                        binding.empty.setVisibility(View.VISIBLE);
                    }
                });

        binding.recyclerView.setAdapter(reminderAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    ///////////////////////////METODE///////////////////
    private void add_reminder()
    {

        dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.dialog_reminder_popup);
        final TextView textView = dialog.findViewById(R.id.date);
        Button select_date = dialog.findViewById(R.id.selectDate);
        Button add = dialog.findViewById(R.id.addButton);
        final EditText message = dialog.findViewById(R.id.message);
        final Calendar newCalender = Calendar.getInstance();

        select_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog dialog = new DatePickerDialog(mainActivity, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth)
                    {

                        final Calendar newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();

                        TimePickerDialog time = new TimePickerDialog(mainActivity, new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {

                                newDate.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                Calendar cal = Calendar.getInstance();
                                Log.d("lala", System.currentTimeMillis() + "");
                                if (newDate.getTimeInMillis() - cal.getTimeInMillis() > 0) {
                                    Date d = newDate.getTime();
                                    date = newDate.getTime().toString();
                                    textView.setText(DateFor.format(d));
                                }
                                else
                                    Toast.makeText(mainActivity, "Invalid time", Toast.LENGTH_SHORT).show();

                            }
                        }, newTime.get(Calendar.HOUR_OF_DAY), newTime.get(Calendar.MINUTE), true);
                        time.show();

                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });


        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                TextView date_text = dialog.findViewById(R.id.date);
                if (date_text.getText().toString().equals(""))
                {
                    Log.d("lala", "nijesve unijeto");
                    Toast.makeText(mainActivity, R.string.notgooddate1, Toast.LENGTH_SHORT).show();
                    return;
                }

                String mes = message.getText().toString().trim();
                if (message.getText().toString().equals(""))
                    mes = getResources().getString(R.string.reminder_notification);

                Date remind = new Date(date.trim());

                Reminder reminder = new Reminder(0, remind, mes, username);

                long id = reminderViewModel.insert(reminder);

                NotifierAlarm.activity = mainActivity;

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                calendar.setTime(remind);
                calendar.set(Calendar.SECOND, 0);

                Intent intent = new Intent(mainActivity, NotifierAlarm.class);
                intent.putExtra("Message", reminder.getMessage());
                intent.putExtra("RemindDate", reminder.getDate().toString());
                intent.putExtra("id", id);
                Log.d("lala", "put id " + id);

                PendingIntent intent1 = PendingIntent.getBroadcast(mainActivity, Integer.parseInt(id + ""), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent1);

                date = "";
                // Toast.makeText(MainPage.this,"Inserted Successfully",Toast.LENGTH_SHORT).show();
                //setItemsInRecyclerView();
                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}