package com.example.crashsimulator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private List<Hospital> hospitalList;

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        public TextView hospitalName;
        public TextView hospitalAddress;

        public HospitalViewHolder(View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalAddress = itemView.findViewById(R.id.hospitalAddress);
        }
    }

    public HospitalAdapter(List<Hospital> hospitalList) {
        this.hospitalList = hospitalList;
    }

    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        Hospital hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getName());
        holder.hospitalAddress.setText(hospital.getAddress());
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }
}
