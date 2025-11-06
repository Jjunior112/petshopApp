package com.littlebirds.petshopapp;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SchedulingAdapter extends RecyclerView.Adapter<SchedulingAdapter.SchedulingViewHolder> {

    private final List<Scheduling> schedulingList;
    private final Context context;

    public SchedulingAdapter(Context context, List<Scheduling> schedulingList) {
        this.context = context;
        this.schedulingList = schedulingList;
    }

    @NonNull
    @Override
    public SchedulingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scheduling_card, parent, false);
        return new SchedulingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulingViewHolder holder, int position) {
        Scheduling scheduling = schedulingList.get(position);

        holder.textPetName.setText( scheduling.getPetName());

        holder.textWorkerName.setText("Funcionário: " + scheduling.getWorkerName());

        String formattedService = scheduling.getServiceType()
                .replace("_", " ") // troca "_" por espaço
                .toLowerCase(Locale.getDefault()); // coloca tudo em minúsculas

        // deixa a primeira letra maiúscula
        formattedService = formattedService.substring(0, 1).toUpperCase() + formattedService.substring(1);

        holder.textServiceType.setText("Serviço: " + formattedService);

        // 🔹 Traduz o status antes de exibir
        String status = scheduling.getStatus();
        String statusTraduzido;

        switch (status.toUpperCase()) {
            case "PENDING":
                statusTraduzido = "Pendente";
                break;
            case "CANCELED":
                statusTraduzido = "Cancelado";
                break;
            case "COMPLETED":
                statusTraduzido = "Concluído";
                break;
            default:
                statusTraduzido = "Desconhecido";
                break;
        }
        holder.textStatus.setText("Status: " + statusTraduzido);

// 🔹 Formata a data (exemplo: "2025-11-15T17:30:00Z" → "15/11/2025 - 14:30")
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());

            Date parsedDate = inputFormat.parse(scheduling.getDate());
            String formattedDate = outputFormat.format(parsedDate);

            holder.textDate.setText("Data: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.textDate.setText("Data: inválida");
        }

        try {
            // 🔹 Formato da data recebido do backend (com 'Z' indicando UTC)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // interpreta como UTC

            // 🔹 Formato que será exibido ao usuário
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault()); // converte para o horário local

            Date parsedDate = inputFormat.parse(scheduling.getDate());
            String formattedDate = outputFormat.format(parsedDate);

            holder.textDate.setText("Data: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.textDate.setText("Data: inválida");
        }

        holder.buttonEditScheduling.setOnClickListener(v -> {
             Intent intent = new Intent(context, EditSchedulingActivity.class);
            intent.putExtra("schedulingId", scheduling.getId());
             context.startActivity(intent);
         });

        //holder.buttonDeleteScheduling.setOnClickListener(v -> {
        //    Intent intent = new Intent(context, DeleteSchedulingActivity.class);
        //    intent.putExtra("schedulingId", scheduling.getId());
        //    context.startActivity(intent);
        //});
    }

    @Override
    public int getItemCount() {
        return schedulingList.size();
    }

    public static class SchedulingViewHolder extends RecyclerView.ViewHolder {
        TextView textPetName,textWorkerName, textServiceType, textDate, textStatus;
        ImageButton buttonEditScheduling, buttonDeleteScheduling;

        public SchedulingViewHolder(@NonNull View itemView) {
            super(itemView);
            textPetName = itemView.findViewById(R.id.textPetName);
            textWorkerName = itemView.findViewById(R.id.textWorkerName);
            textServiceType = itemView.findViewById(R.id.textServiceType);
            textDate = itemView.findViewById(R.id.textDate);
            textStatus = itemView.findViewById(R.id.textStatus);
            buttonEditScheduling = itemView.findViewById(R.id.buttonEditScheduling);
            buttonDeleteScheduling = itemView.findViewById(R.id.buttonDeleteScheduling);
        }
    }
}
