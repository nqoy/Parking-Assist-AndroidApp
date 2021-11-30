package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.parkingassist.R;
import com.example.parkingassist.fragments.mapFragment;
import com.example.parkingassist.fragments.profileFragment;
import com.example.parkingassist.fragments.settingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * This class activates the bottom navigation bar interface.
 */
public class navigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation);

        bottomNav = findViewById(R.id.bottomNavigationView);
        //The first fragment shown//
        getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, new profileFragment()).commit();
        bottomNav.setSelectedItemId(R.id.profile);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.map:
                        fragment = new mapFragment();
                        break;

                    case R.id.settings:
                        fragment = new settingsFragment();
                        break;

                    case R.id.profile:
                        fragment = new profileFragment();
                        break;
                }
                assert fragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment, fragment).commit();
                return true;
            }
        });
    }
}