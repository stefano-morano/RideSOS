package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeActivity extends AppCompatActivity {
    MQTTClient client;
    BottomNavigationView bottomNavigationView;
    TextView noRideTitle, noRideText, rideTitle, rideText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch rideSwitch;
    private static final String TAG = "HomeActivity";

    private static final String CONTENT_TYPE_HOSPITALS_JSON = "application/json";
    private static final String HOSPITALS_URL_JSON = "https://datos.madrid.es/portal/site/egob/menuitem.ac61933d6ee3c31cae77ae7784f1a5a0/?vgnextoid=00149033f2201410VgnVCM100000171f5a0aRCRD&format=json&file=0&filename=212769-0-atencion-medica&mgmtid=da7437ac37efb410VgnVCM2000000c205a0aRCRD&preview=full";
    HospitalDatabase hospitalDatabase;

    SharedPreferences sharedPref;
    ExecutorService es;

    BroadcastReceiver switch_receiver, MQTT_receiver, close_receiver;
    MediaPlayer switch_on_sound;
    MediaPlayer switch_off_sound;

    private static final String SWITCH_STATE_KEY = "switch_state";

    private boolean start_accelerometer = true;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        switch_off_sound = MediaPlayer.create(this, R.raw.switch_off);
        switch_on_sound = MediaPlayer.create(this, R.raw.switch_on);

        if (!AppHelper.HasLocationPermission(this))
            AppHelper.RequestLocationPermission(this);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Check if it is first time signup
        // TODO: Maybe it would be better to put this in a separate helperActivity
        // https://stackoverflow.com/questions/8703065/showing-the-setup-screen-only-on-first-launch-in-android
        if(sharedPref.getBoolean(getString(R.string.first_start_key), true)){
            // Update sharedPref - another start won't be the first
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.first_start_key), false);
            editor.apply(); // apply changes

            // first start, show your dialog
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            finish();
        }

        hospitalDatabase = HospitalDatabase.getInstance(this);
        es = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.hospitals_status_key), true);
                editor.apply();
            }
        };

        // check if the hospitals list is already downloaded and stored in DB
        if (!sharedPref.getBoolean(getString(R.string.hospitals_status_key), false)) {
            Log.d(TAG, "downloading hospitals");
            es.execute(new LoadURLContents(handler, hospitalDatabase, HOSPITALS_URL_JSON, CONTENT_TYPE_HOSPITALS_JSON));
        }

        // Bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            final int x = item.getItemId();

            if (x == R.id.navigation_hospital) {
                startActivity(new Intent(getApplicationContext(), HospitalActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (x == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return x == R.id.navigation_home;
        });


        // Create MQTT Client
        client = new MQTTClient(this);

        // Main content
        noRideTitle = findViewById(R.id.textViewTitleOff);
        noRideText = findViewById(R.id.textViewMessageOff);
        rideTitle = findViewById(R.id.textViewTitleActive);
        rideText = findViewById(R.id.textViewMessageActive);

        rideSwitch = findViewById(R.id.switchRide);

        if (savedInstanceState != null) {
            boolean switchState = savedInstanceState.getBoolean(SWITCH_STATE_KEY, false);
            rideSwitch.setChecked(switchState);
        }

        rideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "switch on");
                switch_on_sound.start();
                noRideTitle.setVisibility(View.INVISIBLE);
                noRideText.setVisibility(View.INVISIBLE);
                rideTitle.setVisibility(View.VISIBLE);
                rideText.setVisibility(View.VISIBLE);
                if (start_accelerometer) {
                    new Thread(() -> {
                        runOnUiThread(this::startAccelerometerService);
                    }).start();
                }
                start_accelerometer = true;
            } else {
                stopAccelerometerService();
                start_accelerometer = true;
                switch_off_sound.start();
                noRideTitle.setVisibility(View.VISIBLE);
                noRideText.setVisibility(View.VISIBLE);
                rideTitle.setVisibility(View.INVISIBLE);
                rideText.setVisibility(View.INVISIBLE);
            }
        });

        // Setup BroadcastReceiver
        switch_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.SWITCH_OFF".equals(intent.getAction())) {
                    rideSwitch.setChecked(false);
                }
            }
        };

        // Setup MQTT
        MQTT_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.MQTT_MESSAGE".equals(intent.getAction())) {
                    client.publishMessage(createMessage());
                }
            }
        };

        close_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.CLOSE_APP".equals(intent.getAction())) {
                    finish();
                }
            }
        };

        // Register the receiver
        IntentFilter switch_filter = new IntentFilter("com.example.SWITCH_OFF");
        registerReceiver(switch_receiver, switch_filter, Context.RECEIVER_NOT_EXPORTED);
        IntentFilter mqtt_filter = new IntentFilter("com.example.MQTT_MESSAGE");
        registerReceiver(MQTT_receiver, mqtt_filter, Context.RECEIVER_NOT_EXPORTED);
        IntentFilter close_filter = new IntentFilter("com.example.CLOSE_APP");
        registerReceiver(close_receiver, close_filter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SWITCH_STATE_KEY, rideSwitch.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean switchState = savedInstanceState.getBoolean(SWITCH_STATE_KEY, false);
        if (switchState)
            start_accelerometer = false;
        rideSwitch.setChecked(switchState);
    }

    private void startAccelerometerService() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);
    }

    private void stopAccelerometerService() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect client to broker
        // TODO: Think where it is better to put this, when we should connect to the broker?
        client.connectToBroker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        switch_off_sound.release();
        switch_on_sound.release();
        // Disconnect client from the broker
        // TODO: Think where it is better to put this, when we should disconnect to the broker?
        client.disconnectFromBroker();
        // Unregister the receiver
        unregisterReceiver(switch_receiver);
        unregisterReceiver(MQTT_receiver);
        unregisterReceiver(close_receiver);
    }

    private String createMessage() {
        // Get user info from shared preferences
        String nameValue = sharedPref.getString(getString(R.string.name_label), getString(R.string.name_value));
        String surnameValue = sharedPref.getString(getString(R.string.surname_label), getString(R.string.surname_value));
        String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), getString(R.string.phone_number_value));
        String genderValue = sharedPref.getString(getString(R.string.gender_label), getString(R.string.gender_value));
        String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), getString(R.string.blood_type_value));
        String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), getString(R.string.birthdate_value));

        // TODO: Get actual latitude and longitude
        float latitude = 12.35f;
        float longitude = 1.45f;

        return AppHelper.CreateEmergencyMessage(
                nameValue,
                surnameValue,
                phoneNumberValue,
                genderValue,
                bloodTypeValue,
                birthdateValue,
                latitude,
                longitude
        );
    }

}
