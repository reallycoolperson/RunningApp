package com.example.myapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Challenge {

    @PrimaryKey(autoGenerate = true)
    private long idChallenge;


    private String challenge_name;
    private String challenge_description;
    private String type; //steps, duration, distance
    private int number;

    public Challenge(long idChallenge, String challenge_name, String challenge_description, String type, int number) {
        this.idChallenge = idChallenge;
        this.challenge_name = challenge_name;
        this.challenge_description = challenge_description;
        this.type = type;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getIdChallenge() {
        return idChallenge;
    }

    public void setIdChallenge(long idChallenge) {
        this.idChallenge = idChallenge;
    }

    public String getChallenge_name() {
        return challenge_name;
    }

    public void setChallenge_name(String challenge_name) {
        this.challenge_name = challenge_name;
    }

    public String getChallenge_description() {
        return challenge_description;
    }

    public void setChallenge_description(String challenge_description) {
        this.challenge_description = challenge_description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}