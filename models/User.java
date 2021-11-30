package com.example.parkingassist.models;

/**
 * This class represent a user in the app.
 **/
public class User {

    public String fullName;
    public String age;
    public String email;
    public Double maxDistance;
    public Boolean likesOnly;

    public User() {
    }

    public User(String fullName, String age, String email, Double maxDistance, Boolean likesOnly) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.maxDistance = maxDistance;
        this.likesOnly = likesOnly;
    }
}
