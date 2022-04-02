package com.example.parkingassist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.FirebaseDB;
import com.example.parkingassist.models.User;
import com.example.parkingassist.activities.MainActivity;
import com.example.parkingassist.activities.ShowUserLocations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class profileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logout = currentView.findViewById(R.id.btnLogout);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
        });

        Button userLocations =  currentView.findViewById(R.id.btnShowReports);
        userLocations.setOnClickListener(v1 -> startActivity(
                new Intent(getActivity(), ShowUserLocations.class)));
        final TextView fullNameTextView = currentView.findViewById(R.id.TextProfileName);
        final TextView emailTextView = currentView.findViewById(R.id.TextProfileEmail);
        final TextView ageTextView = currentView.findViewById(R.id.TextProfileAge);

        FirebaseDB.UserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    String fullName = user.FullName();
                    String email = user.Email();
                    String age = user.Age();

                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    ageTextView.setText(age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
            }
        });
        return currentView;
    }
}