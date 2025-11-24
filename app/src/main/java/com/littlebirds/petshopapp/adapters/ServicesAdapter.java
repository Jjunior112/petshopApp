package com.littlebirds.petshopapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.models.Service;

import java.util.List;
public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private final List<Service> services;

    public ServicesAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);

        holder.textServiceName.setText(service.getName());
        holder.textServicePrice.setText(String.format("R$ %.2f", service.getPrice()));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView textServiceName, textServicePrice;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textServiceName = itemView.findViewById(R.id.textServiceName);
            textServicePrice = itemView.findViewById(R.id.textServicePrice);
        }
    }
}

