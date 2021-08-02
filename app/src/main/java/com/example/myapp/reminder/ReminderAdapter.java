package com.example.myapp.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.data.Reminder;
import com.example.myapp.databinding.ViewHolderReminderBinding;
import com.example.myapp.workouts.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>
{

    private Callback callback;

    private List<Reminder> reminders = new ArrayList<>();
    private ReminderViewModel reminderViewModel;


    public interface Callback
    {
        void cancelAlarm(long id);
    }

    public Callback getCallback()
    {
        return callback;
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public ReminderAdapter(ReminderViewModel reminderViewModel)
    {
        this.reminderViewModel = reminderViewModel;
    }


    public List<Reminder> getReminders()
    {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders)
    {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderReminderBinding viewHolderReminderBinding = ViewHolderReminderBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new ReminderViewHolder(viewHolderReminderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position)
    {
        holder.bind(reminders.get(position));
    }

    @Override
    public int getItemCount()
    {
        return reminders.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolderReminderBinding binding;

        public ReminderViewHolder(@NonNull ViewHolderReminderBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.deleteReminder.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Reminder r = reminders.get(pos);
                reminderViewModel.delete(r);
                notifyDataSetChanged();
                Log.d("lala", "callback" + r.getIdReminder());
                callback.cancelAlarm(r.getIdReminder());


            });
        }

        public void bind(Reminder reminder)
        {

            binding.textView1.setText(DateTimeUtil.getSimpleDateTimeFormat().format(
                    reminder.getDate()));
            binding.textView2.setText(reminder.getMessage());
        }
    }
}
