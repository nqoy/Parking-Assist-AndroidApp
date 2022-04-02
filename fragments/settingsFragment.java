package com.example.parkingassist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.FirebaseDB;
import com.example.parkingassist.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class settingsFragment extends Fragment {
    private TextView tv;
    private SeekBar seek_bar;
    private SwitchCompat sw_likes;
    static int distancePreference;
    static boolean likesPreference;
    private static final String m_tvText = "Set max distance to : " + distancePreference + " km";

  //  public settingsFragment() {
        // Required empty public constructor
   // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View CurrentView= inflater.inflate(R.layout.fragment_settings, container, false);
        tv = CurrentView.findViewById(R.id.tv);
        seek_bar = CurrentView.findViewById(R.id.seerbarid);
        seek_bar.incrementProgressBy(1);
        seek_bar.setMax(30);
        sw_likes = CurrentView.findViewById(R.id.switchLikes);

        //reading the database and initializing in the app//
        FirebaseDB.UserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    distancePreference =  user.MaxDistancePreference().intValue();
                    likesPreference = user.LikesOnlyPreference();
                    tv.setText(m_tvText);
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
                tv.setText(m_tvText);
                FirebaseDB.UserRef().child("maxDistance").setValue(distancePreference);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sw_likes.setOnClickListener(v1 ->
                FirebaseDB.UserRef().child("likesOnly").setValue(sw_likes.isChecked()));
        return CurrentView;
    }
}