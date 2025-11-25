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

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.ViewHolder> {

    private List<ClientDto> employees;

    public EmployeesAdapter(List<ClientDto> employees) {
        this.employees = employees;
    }

    public void updateList(List<ClientDto> newList) {
        this.employees = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false); // Reutiliza o mesmo layout
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClientDto e = employees.get(position);

        holder.txtName.setText(e.getFullName());
        holder.txtEmail.setText(e.getEmail());
        holder.txtStatus.setText(e.isActive() ? "Ativo" : "Inativo");
    }

    @Override
    public int getItemCount() {
        return employees.size();
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
