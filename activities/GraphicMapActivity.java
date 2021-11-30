package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.Report;
import com.example.parkingassist.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parkingassist.databinding.ActivityGraphicMapBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class shows the map and markers.
 */
public class GraphicMapActivity<behavior> extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    static private GoogleMap mMap;
    private ActivityGraphicMapBinding binding;

    FusedLocationProviderClient fusedLocationProviderClient;
    static LatLng currentLocationLatlng;

    DatabaseReference mDbRefLocations, mDbRefUsers;
    private FirebaseUser user;

    private String userID;
    static float distancePreference;
    static boolean likesPreference;

    static float lastDistancePreference;
    static boolean lastLikesPreference;

    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphicMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        mDbRefLocations = FirebaseDatabase.getInstance().getReference("ParkingLocation");
        mDbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userID = user.getUid();
        preferenceDbReadAndInitialize();

    }// End onCreate

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        zoomToUserLocation();
        createMarkersFromDb();
    }

    /**
     * enables the zoom to current location button.
     * Zoom to current user location
     */
    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocationLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatlng, 10));
            }
        });
    }


    /**
     * Creates the dialog option when clicking the info window - like/remove.
     *
     * @param marker gets the current marker.
     */
    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GraphicMapActivity.this);
        builder.setMessage(marker.getSnippet()).setCancelable(true)
                .setPositiveButton("Like", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateMarker(marker, false);

                    }
                }).setNegativeButton("remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateMarker(marker, true);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Removes the clicked marker from the map & firebase or rises the likes accordingly.
     *
     * @param marker clicked marker
     */
    public void updateMarker(Marker marker, boolean remove) {
        mDbRefLocations.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng markerPosition = marker.getPosition();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String snapshotKey = dataSnapshot.getKey();
                    Report report = dataSnapshot.getValue(Report.class);
                    assert report != null;

                    if (report.getLongitude() == markerPosition.longitude && report.getLatitude() ==
                            markerPosition.latitude) {
                        assert snapshotKey != null;

                        if (remove) {
                            marker.remove();
                            //remove from DB
                            mDbRefLocations.child(snapshotKey).removeValue();

                        } else {
                            //rise likes
                            mDbRefLocations.child(snapshotKey).child("approve").setValue(report.getApprove() + 1);
                        }
                        refreshMap();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(GraphicMapActivity.this,
                        "Error creating markers from DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Calculating the distance between 2 locations.
     *
     * @param latA                  latitude of marker
     * @param lonA                  longitude of marker
     * @param currentPositionLatlng current position
     * @return Double distance
     */
    double calculateDistance(double latA, double lonA, LatLng currentPositionLatlng) {
        LatLng latLngA = new LatLng(latA, lonA);

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        while (currentPositionLatlng == null) {
            zoomToUserLocation();
        }
        try {
            locationB.setLatitude(currentPositionLatlng.latitude);
            locationB.setLongitude(currentPositionLatlng.longitude);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(GraphicMapActivity.this,
                    "Error finding current location, please wait a little and open the map again", Toast.LENGTH_SHORT).show();
        }
        float distance = locationA.distanceTo(locationB);
        return distance / 1000;
    }

    /**
     * Creating markers on the map according to the firebase database
     * Giving markers the title and address
     */
    public void createMarkersFromDb() {
        mDbRefLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);

                    //Setting preference view(skip creation)
                    assert report != null;
                    if ((calculateDistance(report.getLatitude(), report.getLongitude(),
                            currentLocationLatlng) >= distancePreference) ||
                            likesPreference && (report.getApprove() == 0)) {
                        continue;
                    }
                    LatLng latLng = new LatLng(report.getLatitude(), report.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions.position(latLng);

                    // Address provider
                    Geocoder geocoder = new Geocoder(GraphicMapActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                latLng.latitude, latLng.longitude, 1);
                        String address = "" + (addresses.get(0).getAddressLine(0));
                        markerOptions.title("Touch for info and edit");
                        markerOptions.snippet(address + "\n\nlikes: " + report.getApprove());
                    } catch (Exception e) {
                        String address = ("Unable to get address");
                        markerOptions.title(address);
                    }
                    mMap.addMarker(markerOptions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GraphicMapActivity.this,
                        "error reading firebase data", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Reading user DB to get and set preference values
     * Refreshes map if there is a change from last preference
     */
    public void preferenceDbReadAndInitialize() {
        mDbRefUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User profile = snapshot.getValue(User.class);
                if (profile != null) {
                    distancePreference = profile.maxDistance.floatValue();
                    likesPreference = profile.likesOnly;
                    if(lastLikesPreference != likesPreference || lastDistancePreference != distancePreference){
                        if(mMap != null) {
                            refreshMap();
                        }
                        lastDistancePreference = distancePreference;
                        lastLikesPreference = likesPreference;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(GraphicMapActivity.this,
                        "error reading firebase data", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Updates the markers on the map after a change by the UI
     */
    public void refreshMap() {
        if (mMap != null) {
            mMap.clear();
            createMarkersFromDb();
        }
    }
}