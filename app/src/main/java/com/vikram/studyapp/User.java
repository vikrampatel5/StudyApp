package com.vikram.studyapp;

/**
 * Created by Vikram on 6/13/2017.
 */

public class User {
    public String name;
    public String email;
    public String classYear;

    public User() {
    }

    public User(String name, String email, String classYear) {
        this.name = name;
        this.email = email;
        this.classYear = classYear;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassYear() {
        return classYear;
    }

    public void setClassYear(String classYear) {
        this.classYear = classYear;
    }
}

