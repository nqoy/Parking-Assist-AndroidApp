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

    private String m_UserID;
    private AddressAdapter m_AddressAdapter;
    private ArrayList<Report> m_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_List = new ArrayList<>();
        m_AddressAdapter = new AddressAdapter(this, m_List);

        setContentView(R.layout.activity_show_user_locations);
        DatabaseReference m_DbRefLocations = FirebaseDatabase.getInstance().getReference(
                "ParkingLocation");
        DatabaseReference m_DbRefUsers = FirebaseDatabase.getInstance().getReference(
                "Users");
        RecyclerView m_RecyclerView = findViewById(R.id.userLocationsList);
        m_RecyclerView.setAdapter(m_AddressAdapter);
        m_RecyclerView.setHasFixedSize(true);
        m_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser m_User = FirebaseAuth.getInstance().getCurrentUser();
        assert m_User != null;
        m_UserID = m_User.getUid();
        m_DbRefLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);
                    assert report != null;
                    if (m_UserID.equals(report.UserId())) {
                        m_List.add(report);
                    }
                }
                m_AddressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}