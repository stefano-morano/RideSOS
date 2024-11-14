package com.example.crashsimulator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private final List<HospitalEntity> hospitalList = new ArrayList<>();

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        public TextView hospitalName;
        public TextView hospitalAddress;

        public HospitalViewHolder(View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalAddress = itemView.findViewById(R.id.hospitalAddress);
        }

        void bindValues(HospitalEntity hospital) {
            hospitalName.setText(hospital.getName());
            hospitalAddress.setText(hospital.getAddress());

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public HospitalAdapter(HospitalDatabase hospitalDatabase, double currentLatitude, double currentLongitude) {
        super();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            List<HospitalEntity> hospitals = hospitalDatabase.hospitalDAO().getAllHospitals();
            hospitals.sort((h1, h2) -> {
                double distanceToH1 = calculateDistance(
                        currentLatitude, currentLongitude,
                        h1.getLatitude(), h1.getLongitude()
                );
                double distanceToH2 = calculateDistance(
                        currentLatitude, currentLongitude,
                        h2.getLatitude(), h2.getLongitude()
                );
                return Double.compare(distanceToH1, distanceToH2);
            });
            hospitalList.addAll(hospitals);
            new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
        });
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        HospitalEntity hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getName());
        holder.hospitalAddress.setText(hospital.getAddress());
        holder.bindValues(hospital);
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of the earth in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // Distance in km
    }
}
