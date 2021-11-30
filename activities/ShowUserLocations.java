package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.parkingassist.R;
import com.example.parkingassist.models.Report;
import com.example.parkingassist.adapters.AddressAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowUserLocations extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference mDbRefLocations, mDbRefUsers;
    String userID;
    RecyclerView recyclerView;
    AddressAdapter addressAdapter;
    ArrayList<Report> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_locations);


        user = FirebaseAuth.getInstance().getCurrentUser();
        mDbRefLocations = FirebaseDatabase.getInstance().getReference("ParkingLocation");
        mDbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        recyclerView = findViewById(R.id.userLocationsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, list);
        recyclerView.setAdapter(addressAdapter);

        mDbRefLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);
                    assert report != null;
                    if(userID.equals(report.getUserId())) {
                        list.add(report);
                    }
                }
                addressAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}