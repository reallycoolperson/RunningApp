package com.example.myapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private long idUser;

    private String fullname;
    private String email;
    private String password;
    private String username;
    private String filename;

    public User(long idUser, String fullname, String email, String password, String username, String filename)
    {
        this.idUser = idUser;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.username = username;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
