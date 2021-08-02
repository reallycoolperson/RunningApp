package com.example.myapp.workouts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.data.Workout;
import com.example.myapp.databinding.ViewHolderWorkoutBinding;

import java.util.ArrayList;
import java.util.List;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private Callback callback;

    private List<Workout> workoutList = new ArrayList<>();

    private WorkoutViewModel workoutViewModel;

    public interface Callback {
        void onWorkoutClick(Workout workout);
    }
    public WorkoutAdapter(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setWorkoutList(List<Workout> workoutList) {
        this.workoutList = workoutList;
        notifyDataSetChanged();
    }

    public WorkoutViewModel getWorkoutViewModel() {
        return workoutViewModel;
    }

    public void setWorkoutViewModel(WorkoutViewModel workoutViewModel) {
        this.workoutViewModel = workoutViewModel;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderWorkoutBinding viewHolderWorkoutBinding = ViewHolderWorkoutBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new WorkoutViewHolder(viewHolderWorkoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        holder.bind(workoutList.get(position));
        holder.callback = callback;
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderWorkoutBinding binding;

        protected Callback callback;


        public WorkoutViewHolder(@NonNull ViewHolderWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.workoutCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   Log.d("lala", "klik na workout kard");

                    int index = getAdapterPosition();
                    Workout workout =  workoutList.get(index);
                    callback.onWorkoutClick(workout);
                    }
            });
        }

        public void bind(Workout workout) {
            binding.workoutDate.setText(DateTimeUtil.getSimpleDateFormat().format(
                    workout.getDate()));
            binding.workoutLabel.setText(
                    workout.getLabel());
            binding.workoutDistance.setText(String.format("%.2f km",
                    workout.getDistance()));
            binding.workoutPace.setText(String.format("%s min/km", DateTimeUtil.realMinutesToString(
                    workout.getDuration() / workout.getDistance())));
            binding.workoutDuration.setText(String.format("%s min", DateTimeUtil.realMinutesToString(
                    workout.getDuration())));
            binding.workoutSteps.setText(String.format("%d steps", workout.getSteps()));

        }
    }
}
