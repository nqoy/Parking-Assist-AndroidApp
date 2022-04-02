package com.example.parkingassist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.parkingassist.R;
import com.example.parkingassist.activities.MapActivity;


public class mapFragment extends Fragment {
    Button openMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_map, container, false);

        openMap = currentView.findViewById(R.id.btnOpenMap);
        openMap.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });
        return currentView;
    }

}