package com.example.crashsimulator;
import android.annotation.SuppressLint;
import android.graphics.Color;
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
    public HospitalAdapter(HospitalDatabase hospitalDatabase) {
        super();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            List<HospitalEntity> hospitals = hospitalDatabase.hospitalDAO().getAllHospitals();
            hospitalList.addAll(hospitals);
            notifyDataSetChanged();
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
}
