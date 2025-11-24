package com.littlebirds.petshopapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.models.ClientDto;

import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> {

    private List<ClientDto> clients;

    public ClientsAdapter(List<ClientDto> clients) {
        this.clients = clients;
    }

    public void updateList(List<ClientDto> newList) {
        this.clients = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClientDto c = clients.get(position);

        holder.txtName.setText(c.getFullName());
        holder.txtEmail.setText(c.getEmail());
        holder.txtStatus.setText(c.isActive() ? "Ativo" : "Inativo");
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
