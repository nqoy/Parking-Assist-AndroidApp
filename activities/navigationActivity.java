package com.example.parkingassist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.parkingassist.R;
import com.example.parkingassist.fragments.mapFragment;
import com.example.parkingassist.fragments.profileFragment;
import com.example.parkingassist.fragments.settingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This class activates the bottom navigation bar interface.
 */
public class navigationActivity extends AppCompatActivity {
    BottomNavigationView m_BottomNav;
    Fragment m_MapFragment = new mapFragment();
    Fragment m_settingsFragment = new settingsFragment();
    Fragment m_profileFragment = new profileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation);
        //The first fragment shown//
        getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, new profileFragment()).commit();
        m_BottomNav = findViewById(R.id.bottomNavigationView);
        m_BottomNav.setSelectedItemId(R.id.profile);
        m_BottomNav.setOnItemSelectedListener(item -> {
            Fragment navigationFragment = null;

            switch (item.getItemId()) {
                case R.id.map:
                    navigationFragment = m_MapFragment;
                    break;

                case R.id.settings:
                    navigationFragment = m_settingsFragment;
                    break;

                case R.id.profile:
                    navigationFragment = m_profileFragment;
                    break;
            }
            assert m_MapFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, navigationFragment).commit();
            return true;
        });
    }
}