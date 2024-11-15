package com.example.crashsimulator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.Manifest;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HospitalActivity extends AppCompatActivity {

    HospitalDatabase database;
    HospitalAdapter hospitalAdapter;
    ExecutorService es;

    SearchView searchView;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        database = HospitalDatabase.getInstance(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Try to get locations
        if (AppHelper.CheckLocationPermissionsGuaranteed(this)) {
            final double[] currentLatitude = new double[1];
            final double[] currentLongitude = new double[1];
            AppHelper.GetCurrentLocation(this, location -> {
                if (location != null) {
                    currentLatitude[0] = location.getLatitude();
                    currentLongitude[0] = location.getLongitude();
                    Log.d("Location", "Lat: " + currentLatitude[0] +
                            ", Lon: " + currentLongitude[0]);
                    // Create adapter
                    hospitalAdapter = new HospitalAdapter(database, currentLatitude[0], currentLongitude[0]);
                    recyclerView.setAdapter(hospitalAdapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("Location", "Failed to get location");
                }
            });
        } else {
            Log.d("Location", "can't read location");
            // Create adapter
            hospitalAdapter = new HospitalAdapter(database, 0.0, 0.0);
            recyclerView.setAdapter(hospitalAdapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Search View
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hospitalAdapter.setFilteredList(newText);
                return false;
            }
        });


        // Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_hospital);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            final int x = item.getItemId();

            if (x == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (x == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return x == R.id.navigation_hospital;
        });
    }
}
