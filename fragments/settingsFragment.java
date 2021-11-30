package com.example.parkingassist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class settingsFragment extends Fragment {
    private TextView tv;
    private SeekBar seek_bar;
    private Switch sw_likes;

    private FirebaseUser user;
    private DatabaseReference mDbRefUsers;

    private String userID;
    static int distancePreference;
    static boolean likesPreference;

    public settingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userID = user.getUid();

        mDbRefUsers = FirebaseDatabase.getInstance().getReference("Users");


        tv = v.findViewById(R.id.tv);
        seek_bar = v.findViewById(R.id.seerbarid);
        seek_bar.incrementProgressBy(1);
        seek_bar.setMax(30);
        sw_likes = v.findViewById(R.id.switchLikes);

        //reading the database and initializing in the app//
        mDbRefUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User profile = snapshot.getValue(User.class);
                if (profile != null) {
                    distancePreference =  profile.maxDistance.intValue();
                    likesPreference = profile.likesOnly;

                    tv.setText("Set max distance to : " + distancePreference + " km");
                    seek_bar.setProgress(distancePreference);
                    sw_likes.setChecked(likesPreference);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distancePreference = progress;
                tv.setText("Set max distance to: " + distancePreference + " km");
                mDbRefUsers.child(userID).child("maxDistance").setValue(distancePreference);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sw_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbRefUsers.child(userID).child("likesOnly").setValue(sw_likes.isChecked());
            }
        });
        return v;
    }
}