package com.example.myapp.statistics;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.data.Challenge;
import com.example.myapp.data.Workout;
import com.example.myapp.databinding.ViewHolderChallengeBinding;

import java.util.List;



public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private final ChallengeViewModel challengeViewModel;
    private List<Challenge> challenges;
    private List<Workout> workouts;

    private double average_steps;
    private double average_distance;
    private double average_duration;


    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public ChallengeAdapter(ChallengeViewModel challengeViewModel)
    {
        this.challengeViewModel = challengeViewModel;
    }

    public double getAverage_steps() {
        return average_steps;
    }

    public void setAverage_steps(double average_steps) {
        this.average_steps = average_steps;
    }

    public double getAverage_distance() {
        return average_distance;
    }

    public void setAverage_distance(double average_distance) {
        this.average_distance = average_distance;
    }

    public double getAverage_duration() {
        return average_duration;
    }

    public void setAverage_duration(double average_duration) {
        this.average_duration = average_duration;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderChallengeBinding viewHolderChallengeBinding = ViewHolderChallengeBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new ChallengeViewHolder(viewHolderChallengeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {

                Challenge challenge = challenges.get(position);
                ViewHolderChallengeBinding binding = holder.binding;
       // holder.setIsRecyclable(false);
        holder.bind(challenges.get(position));



    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
         return  challenges.size();
        }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderChallengeBinding binding;

        public ChallengeViewHolder(@NonNull ViewHolderChallengeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Challenge challenge)
        {
            binding.nameChallenge.setText(challenge.getChallenge_name());
            binding.descriptionChallenge.setText(challenge.getChallenge_description());
         //   Log.d("1998", "tip"  + challenge.getType() + challenge.getNumber());

            for(int i=0; i<workouts.size(); i++)
            {

                if(challenge.getType().equals("steps") && challenge.getNumber()<=workouts.get(i).getSteps())
                {
                    binding.nameChallenge.setText(R.string.completed);
                    binding.trophey.setImageResource(R.drawable.trophey);
                   // Log.d("1998", workouts.get(i).getSteps() + "");
                }

               else if(challenge.getType().equals("duration") && challenge.getNumber()<=workouts.get(i).getDuration())
                {
                    binding.nameChallenge.setText(R.string.completed);
                    binding.trophey.setImageResource(R.drawable.trophey);
                   // Log.d("1998", workouts.get(i).getDuration() + "");

                }
                else if(challenge.getType().equals("distance") && challenge.getNumber()<=workouts.get(i).getDistance())
                {
                    binding.nameChallenge.setText(R.string.completed);
                    binding.trophey.setImageResource(R.drawable.trophey);
                   // Log.d("1998", workouts.get(i).getDistance() + "");

                }
            }
        }
    }
}
