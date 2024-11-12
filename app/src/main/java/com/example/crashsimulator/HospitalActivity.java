package com.example.crashsimulator;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HospitalActivity extends AppCompatActivity {

    ExecutorService es;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);


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

                return false;
            }
        });

        HospitalDatabase database = HospitalDatabase.getInstance(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HospitalAdapter hospitalAdapter = new HospitalAdapter(database);
        recyclerView.setAdapter(hospitalAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            if((string_result = msg.getData().getString("text")) != null) {
                Log.d("HospitalActivity", "Received message: " + string_result);
                text.setText(string_result);
            }
        }
    };

    private void loadHospitalData() {
            // Execute the loading task in background:
            LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_JSON);
            es.execute(loadURLContents);
    }
}
