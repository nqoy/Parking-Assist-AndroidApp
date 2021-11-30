package com.example.parkingassist.models;

import android.location.Location;

public class Report {
    private String userId;
    private Location location;
    private int approve;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    private double latitude;
    private double longitude;
    private double accuracy;

    public Report() {
    }


    public Report(String currentUserId, Location currentLocation) {
        this.userId = currentUserId;
        this.location = currentLocation;
        this.approve = 0;
    }

    public Report(String currentUserId, int approve, double latitude, double longitude, double accuracy) {
        this.userId = currentUserId;
        this.approve = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setApprove(int approve) {
        this.approve = approve;
    }

    public String getUserId() {
        return this.userId;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getApprove() {
        return this.approve;
    }

}