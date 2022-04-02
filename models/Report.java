package com.example.parkingassist.models;

import android.location.Location;

public class Report {
    private String userId;
    private int approve;
    private double latitude;
    private double longitude;

  //  public Report() {
   // }

   // public Report(String currentUserId, Location currentLocation) {
  //      this.userId = currentUserId;
  //      this.approve = 0;
  //  }

    public Report(String currentUserId, double latitude, double longitude, double accuracy) {
        this.userId = currentUserId;
        this.approve = 0;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double Latitude() {
        return latitude;
    }
    public double Longitude() {
        return longitude;
    }
    public String UserId() {
        return this.userId;
    }
    public int Approves() {
        return this.approve;
    }

}