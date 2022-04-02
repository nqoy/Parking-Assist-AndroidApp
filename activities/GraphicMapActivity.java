package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.AppSetting;
import com.example.parkingassist.models.FirebaseDB;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class shows the map and markers.
 */
public class GraphicMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static GoogleMap s_Map;
    private static float s_CurrentDistancePreference;
    private static boolean s_CurrentLikesPreference;
    private static LatLng s_CurrentLocationLatLng;
    private FusedLocationProviderClient m_FusedLocationProviderClient;

    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ActivityGraphicMapBinding mapBinding =
                ActivityGraphicMapBinding.inflate(getLayoutInflater());

        assert mapFragment != null;

        setContentView(mapBinding.getRoot());
        mapFragment.getMapAsync(this);
        m_FusedLocationProviderClient = new FusedLocationProviderClient(this);
        preferenceDbReadAndInitialize();
    }// End onCreate

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        s_Map = googleMap;
        s_Map.setOnInfoWindowClickListener(this);
        updateUserLocationAndZoom();
        createMarkersFromDb();
    }

    /**
     * enables the zoom to current location button.
     * Zoom to current user location
     */
    private void updateUserLocationAndZoom() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) { return;
        }
        if (!s_Map.isMyLocationEnabled()) {
            s_Map.setMyLocationEnabled(true);
        }
        Task<Location> locationTask = m_FusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            s_CurrentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            s_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(s_CurrentLocationLatLng, 10));
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
                .setPositiveButton("Like", (dialog, which) ->
                        updateMarker(marker, false)).setNegativeButton(
                "remove", (dialog, which) -> updateMarker(marker, true));
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Removes the clicked marker from the map & firebase or rises the likes accordingly.
     *
     * @param marker clicked marker
     */
    public void updateMarker(Marker marker, boolean remove) {
        FirebaseDB.DbRefLocations().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng markerPosition = marker.getPosition();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String snapshotKey = dataSnapshot.getKey();
                    Report report = dataSnapshot.getValue(Report.class);
                    assert report != null;

                    if (report.Longitude() == markerPosition.longitude && report.Latitude() ==
                            markerPosition.latitude) {
                        assert snapshotKey != null;

                        if (remove) {
                            marker.remove();
                            //remove from DB
                            FirebaseDB.DbRefLocations().child(snapshotKey).removeValue();

                        } else {
                            //rise likes
                            FirebaseDB.DbRefLocations().child(snapshotKey).child("approve").setValue(report.Approves() + 1);
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
     * @param currentPositionLatLng current position
     * @return Double distance
     */
    double calculateDistance(double latA, double lonA, LatLng currentPositionLatLng) {
        LatLng latLngA = new LatLng(latA, lonA);

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        while (currentPositionLatLng == null) {
            updateUserLocationAndZoom();
        }
        try {
            locationB.setLatitude(currentPositionLatLng.latitude);
            locationB.setLongitude(currentPositionLatLng.longitude);
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
        FirebaseDB.DbRefLocations().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);

                    //Setting preference view(skip creation)
                    assert report != null;
                    if ((calculateDistance(report.Latitude(), report.Longitude(),
                            s_CurrentLocationLatLng) >= s_CurrentDistancePreference) ||
                            s_CurrentLikesPreference && (report.Approves() == 0)) {
                        continue;
                    }
                    LatLng latLng = new LatLng(report.Latitude(), report.Longitude());
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
                        markerOptions.snippet(address + "\n\nlikes: " + report.Approves());
                    } catch (Exception e) {
                        String address = ("Unable to get address");
                        markerOptions.title(address);
                    }
                    s_Map.addMarker(markerOptions);
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
        FirebaseDB.DbRefUsers().child(FirebaseDB.UserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    s_CurrentDistancePreference = user.MaxDistancePreference().floatValue();
                    s_CurrentLikesPreference = user.LikesOnlyPreference();
                    if (AppSetting.getLikesPreference() != s_CurrentLikesPreference ||
                            AppSetting.getDistancePreference() != s_CurrentDistancePreference) {
                        if (s_Map != null) {
                            refreshMap();
                        }
                        AppSetting.setDistancePreference(s_CurrentDistancePreference);
                        AppSetting.setLikesPreference(s_CurrentLikesPreference);
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
        if (s_Map != null) {
            s_Map.clear();
            createMarkersFromDb();
        }
    }
}