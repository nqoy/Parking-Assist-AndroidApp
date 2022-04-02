package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * This class provides location request, GPS updates and saving a location.
 */
public class MapActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int s_LOCATION_REQUEST_CODE = 10001;
    private static final String s_TAG = "MapActivity";
    private static Location s_CurrentLocation;
   // private List<Location> savedLocations;
    private LocationRequest m_LocationRequest; //Config file settings related to FusedLocation//
    private LocationCallback m_LocationCallBack; //Updates location result on GPS interval//
    private FusedLocationProviderClient m_FusedLocationProviderClient; //API for location services//
    private TextView m_LatTxtView, m_LonTxtView, m_AltTxtView,
            m_AccTxtView, m_SpeedTxtView, m_AddressTxtView;

 //   public Location CurrentLocation() {
 //       return s_CurrentLocation;
  //  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Button btnNewPoint = findViewById(R.id.btnNewPoint);
        Button btnShowMapPoints = findViewById(R.id.btnPointShowMap);
        btnNewPoint.setOnClickListener(this);
        btnShowMapPoints.setOnClickListener(this);
        m_LatTxtView = findViewById(R.id.Latitude);
        m_LonTxtView = findViewById(R.id.Longitude);
        m_AltTxtView = findViewById(R.id.Altitude);
        m_AccTxtView = findViewById(R.id.Accuracy);
        m_SpeedTxtView = findViewById(R.id.Speed);
        m_AddressTxtView = findViewById(R.id.Address);
        m_FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        m_LocationRequest = LocationRequest.create()
                .setInterval(3000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(6000);
        m_LocationCallBack = new LocationCallback() {//Gets the current location//
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Log.d(s_TAG, "onLocationResult: " + location.toString());
                    s_CurrentLocation = location;
                    updateUIValues(s_CurrentLocation);
                }
            }
        };
    }// End onCreate //

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNewPoint:
                //add new parking location to DB
                if (s_CurrentLocation != null) {
                    addNewParkingLocationToDB();
                    System.out.println("Location was added to firebase");
                } else {
                    System.out.println("Location is null");
                }
                break;
            case R.id.btnPointShowMap:
                Intent intent = new Intent(MapActivity.this, GraphicMapActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * on start, start GPS updates or request GPS permission.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingsAndStartLocationUpdates();
        } else {
            askLocationPermission();
        }
    }

    /**
     * Request GPS permission if permission is not granted
     */
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(s_TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, s_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * Checks the device settings according to the locationRequest and if it the settings are compatible
     * starts the location updates.
     */
    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder().addLocationRequest(m_LocationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            //settings of device are compatible.
            startLocationUpdates();
        });
    }

    /**
     * Allows constant GPS updates according to the locationRequest config.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
            return;
        }
        m_FusedLocationProviderClient.requestLocationUpdates(m_LocationRequest, m_LocationCallBack, Looper.getMainLooper());
    }

    /**
     * Tells the program to trigger a method after permission is granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == s_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                checkSettingsAndStartLocationUpdates();
            } else {
                //Permission not granted
                Toast.makeText(
                        this, "This app need GPS permission to work properly", Toast.LENGTH_LONG).show();
                askLocationPermission();
                finish();
            }
        }
    }

    /**
     * updates all UI properties according to location
     *
     * @param location provides the UI values of current location
     */
    private void updateUIValues(Location location) {
        String emptyValueText = "Not Available";
        if (location == null) {
            m_LatTxtView.setText(emptyValueText);
            m_LonTxtView.setText(emptyValueText);
            m_AccTxtView.setText(emptyValueText);
            m_AltTxtView.setText(emptyValueText);
            m_SpeedTxtView.setText(emptyValueText);
        } else {
            m_LatTxtView.setText(String.valueOf((location.getLatitude())));
            m_LonTxtView.setText(String.valueOf((location.getLongitude())));
            m_AccTxtView.setText(String.valueOf((location.getAccuracy())));

            if (location.hasAltitude()) {
                m_AltTxtView.setText(String.valueOf((location.getAltitude())));
            } else {
                m_AltTxtView.setText(emptyValueText);
            }

            if (location.hasSpeed()) {
                m_SpeedTxtView.setText(String.valueOf((location.getSpeed())));
            } else {
                m_SpeedTxtView.setText(emptyValueText);
            }
        }

        //Class that provides address according to the location//
        Geocoder geocoder = new Geocoder(MapActivity.this);
        try {
            assert location != null;
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            m_AddressTxtView.setText(addresses.get(0).getAddressLine(0));

        } catch (Exception e) {
            m_AddressTxtView.setText(emptyValueText);
        }
    }// End updateUIValues//

    public void addNewParkingLocationToDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://parking-assist-7b442-default-rtdb.firebaseio.com/");
        DatabaseReference mDatabase = database.getReference("ParkingLocation");
        //setting firebase unique key for Hashmap list
        String keyLocation = mDatabase.push().getKey();
        //extract current usr id
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //creating location post objects
        Report report = new Report(currentUser, s_CurrentLocation.getLatitude(), s_CurrentLocation.getLongitude(), s_CurrentLocation.getAccuracy());
        assert keyLocation != null;
        mDatabase.child(keyLocation).setValue(report);
    }

}