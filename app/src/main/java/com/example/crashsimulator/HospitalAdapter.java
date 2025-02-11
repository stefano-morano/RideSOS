package com.example.crashsimulator;
import static android.content.Intent.ACTION_VIEW;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private List<HospitalEntity> fullHospitalList = new ArrayList<>();
    private List<HospitalEntity> hospitalList = new ArrayList<>();

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public View divider;
        public TextView hospitalName;
        public TextView hospitalAddress;
        public TextView distanceToHospital;

        public HospitalViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.hospitalInfoLayout);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalAddress = itemView.findViewById(R.id.hospitalAddress);
            distanceToHospital = itemView.findViewById(R.id.distance);
            divider = itemView.findViewById(R.id.divider);
        }

        void bindValues(HospitalEntity hospital) {
            hospitalName.setText(hospital.getName());
            hospitalAddress.setText(hospital.getAddress());
            distanceToHospital.setText(hospital.getDistance() + "km");

            container.setOnClickListener(view -> {
                view.getContext().startActivity(new Intent(ACTION_VIEW, Uri.parse(hospital.getWebsite())));
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public HospitalAdapter(HospitalDatabase hospitalDatabase, double currentLatitude, double currentLongitude, OnInitializationCompleteListener listener) {
        super();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<HospitalEntity> hospitals = hospitalDatabase.hospitalDAO().getAllHospitals();
            Log.d("HospitalAdapter", String.valueOf(hospitals.size()));

            // Means that the user hasn't granted permissions to the app to access the location
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                hospitals.forEach(hospital -> {
                    Log.d("CalculateDistance", "currentLat: " + currentLatitude +
                            ", currentLon: " + currentLongitude +
                            ", hospitalLat: " + hospital.getLatitude() +
                            ", hospitalLon: " + hospital.getLongitude());
                    double distance = calculateDistance(
                            currentLatitude, currentLongitude,
                            hospital.getLatitude(), hospital.getLongitude()
                    );
                    DecimalFormat df = new DecimalFormat("#.##");
                    distance = Double.parseDouble(df.format(distance));
                    hospital.setDistance(distance);
                });
                hospitals.sort(Comparator.comparingDouble(HospitalEntity::getDistance));
            }

            fullHospitalList.addAll(hospitals);
            hospitalList.addAll(hospitals);
            new Handler(Looper.getMainLooper()).post(() -> {
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onInitializationComplete();
                }
            });
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(String text) {
        // Clear hospitalList
        hospitalList.clear();

        if (text.isEmpty()) {
            hospitalList.addAll(fullHospitalList);
        } else {
            for (HospitalEntity item : fullHospitalList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    hospitalList.add(item);
                }
            }
        }

        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        if (position == 0 && position == getItemCount()-1) {
            holder.container.setBackgroundResource(R.drawable.rounded_item_hospitals);
            holder.divider.setVisibility(View.GONE);
        } else {
            if (position == 0) {
                holder.container.setBackgroundResource(R.drawable.rounded_item_hospitals_top);
            } else if (position == getItemCount() - 1) {
                holder.container.setBackgroundResource(R.drawable.rounded_item_hospitals_bottom);
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.container.setBackgroundResource(R.drawable.item_hospitals);
            }
        }

        HospitalEntity hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getName());
        holder.hospitalAddress.setText(hospital.getAddress());
        holder.distanceToHospital.setText(hospital.getDistance() + " km");
        holder.bindValues(hospital);
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
