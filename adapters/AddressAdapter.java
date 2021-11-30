package com.example.parkingassist.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingassist.R;
import com.example.parkingassist.models.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    ArrayList<Report> list;

    public AddressAdapter(Context context, ArrayList<Report> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = list.get(position);

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(report.getLatitude(), report.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                holder.address.setText(address  + "\n\nNumber of like: " + report.getApprove());
            }

        } catch (IOException e) {
            e.printStackTrace();
            holder.address.setText("Address unavialable.");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder{

        TextView address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.tv_address);
        }
    }
}