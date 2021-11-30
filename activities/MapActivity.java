package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static final int LOCATION_REQUEST_CODE = 10001;
    private static final String TAG = "MapActivity";

    TextView lat, lon, alt, acc, speed, address;
    Button btnNewPoint, btnShowMapPoints;

    Location currentLocation;
    List<Location> savedLocations;
    //Config file for all settings related to FusedLocation//
    LocationRequest locationRequest;
    //Whenever the GPS interval is met, get the location result//
    LocationCallback locationCallBack;
    //API for location services//
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lat = (TextView) findViewById(R.id.Latitude);
        lon = (TextView) findViewById(R.id.Longitude);
        alt = (TextView) findViewById(R.id.Altitude);
        acc = (TextView) findViewById(R.id.Accuracy);
        speed = (TextView) findViewById(R.id.Speed);
        address = (TextView) findViewById(R.id.Address);

        btnNewPoint = (Button) findViewById(R.id.btnNewPoint);
        btnNewPoint.setOnClickListener(this);
        btnShowMapPoints = (Button) findViewById(R.id.btnPointShowMap);
        btnShowMapPoints.setOnClickListener(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setInterval(3000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(6000);
        //Gets the current location
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: " + location.toString());
                    currentLocation = location;
                    updateUIValues(currentLocation);
                }
            }
        };
    }// End onCreate //

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNewPoint:
                //add new parking location to DB
                if (currentLocation != null) {
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
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    /**
     * Checks the device settings according to the locationRequest and if it the settings are compatible
     * starts the location updates.
     */
    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //settings of device are compatible.
                startLocationUpdates();
            }
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper());
    }

    /**
     * Tells the program to trigger a method after permission is granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
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
        if (location == null) {
            lat.setText("Not Available");
            lon.setText("Not Available");
            acc.setText("Not Available");
            alt.setText("Not Available");
            speed.setText("Not Available");
        } else {
            lat.setText(String.valueOf((location.getLatitude())));
            lon.setText(String.valueOf((location.getLongitude())));
            acc.setText(String.valueOf((location.getAccuracy())));

            if (location.hasAltitude()) {
                alt.setText(String.valueOf((location.getAltitude())));
            } else {
                alt.setText("Not Available");
            }

            if (location.hasSpeed()) {
                speed.setText(String.valueOf((location.getSpeed())));
            } else {
                speed.setText("Not Available");
            }
        }

        //Class that provides address according to the location//
        Geocoder geocoder = new Geocoder(MapActivity.this);
        try {
            assert location != null;
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            address.setText(addresses.get(0).getAddressLine(0));

        } catch (Exception e) {
            address.setText("Unable to get address");
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
        Report report = new Report(currentUser, 0, currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAccuracy());
        assert keyLocation != null;
        mDatabase.child(keyLocation).setValue(report);
    }

}