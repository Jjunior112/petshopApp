package com.littlebirds.petshopapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

        // Recupera ROLE do usuário
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "CLIENT");

        // Nome do pet
        holder.textPetName.setText(scheduling.getPetName());

        // Funcionário
        holder.textWorkerName.setText("Funcionário: " + scheduling.getWorkerName());

        // Serviço formatado
        String formattedService = scheduling.getServiceType()
                .replace("_", " ")
                .toLowerCase(Locale.getDefault());

        formattedService = formattedService.substring(0, 1).toUpperCase() + formattedService.substring(1);
        holder.textServiceName.setText("Serviço: " + formattedService);

        // Status traduzido
        String status = scheduling.getStatus();
        String statusTraduzido;

        switch (status.toUpperCase()) {
            case "PENDING": statusTraduzido = "Pendente"; break;
            case "CANCELED": statusTraduzido = "Cancelado"; break;
            case "COMPLETED": statusTraduzido = "Concluído"; break;
            default: statusTraduzido = "Desconhecido"; break;
        }

        holder.textStatus.setText("Status: " + statusTraduzido);

        // Conversão de data/hora
        try {
            DateTimeFormatter inputFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

            ZonedDateTime zonedDateTime = ZonedDateTime.parse(scheduling.getDate(), inputFormatter);

            ZonedDateTime brazilTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));

            DateTimeFormatter outputFormatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

            String formattedDate = brazilTime.format(outputFormatter);

            holder.textDate.setText("Data: " + formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            holder.textDate.setText("Data: inválida");
        }

        // ---------------------------------------------------------
        // 🔥 VISIBILIDADE DOS BOTÕES POR ROLE
        // ---------------------------------------------------------
        if (userRole.equalsIgnoreCase("CLIENT")) {
            holder.buttonEndScheduling.setVisibility(View.GONE);
            if (!status.equalsIgnoreCase("CANCELED") &&
                    !status.equalsIgnoreCase("COMPLETED")) {
                holder.buttonEditScheduling.setVisibility(View.VISIBLE);
                holder.buttonDeleteScheduling.setVisibility(View.VISIBLE);
            } else {
                holder.buttonEditScheduling.setVisibility(View.GONE);
                holder.buttonDeleteScheduling.setVisibility(View.GONE);
            }
        }
        else if (userRole.equalsIgnoreCase("WORKER")) {
            // Exibe apenas se NÃO estiver cancelado e NÃO estiver concluído
            if (!status.equalsIgnoreCase("CANCELED") &&
                    !status.equalsIgnoreCase("COMPLETED")) {
                holder.buttonEndScheduling.setVisibility(View.VISIBLE);
            } else {
                holder.buttonEndScheduling.setVisibility(View.GONE);
                holder.buttonEditScheduling.setVisibility(View.GONE);
                holder.buttonDeleteScheduling.setVisibility(View.GONE);
            }
        }
        else if (userRole.equalsIgnoreCase("ADMIN")) {
            holder.buttonEndScheduling.setVisibility(View.VISIBLE);
            holder.buttonEditScheduling.setVisibility(View.VISIBLE);
            holder.buttonDeleteScheduling.setVisibility(View.VISIBLE);
        }

        // ---------------------------------------------------------
        // 🔥 AÇÕES DOS BOTÕES
        // ---------------------------------------------------------

        // Editar agendamento
        holder.buttonEditScheduling.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditSchedulingActivity.class);
            intent.putExtra("schedulingId", scheduling.getId());
            context.startActivity(intent);
        });

        // Excluir agendamento
        holder.buttonDeleteScheduling.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeleteSchedulingActivity.class);
            intent.putExtra("schedulingId", scheduling.getId());
            context.startActivity(intent);
        });

        // Finalizar agendamento (WORKER ou ADMIN)
        holder.buttonEndScheduling.setOnClickListener(v -> {
            Intent intent = new Intent(context, EndSchedulingActivity.class);
            intent.putExtra("schedulingId", scheduling.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return schedulingList.size();
    }

    // ---------------------------------------------------------
    // ViewHolder
    // ---------------------------------------------------------
    public static class SchedulingViewHolder extends RecyclerView.ViewHolder {

        TextView textPetName, textWorkerName, textServiceName, textDate, textStatus;
        ImageButton buttonEditScheduling, buttonDeleteScheduling, buttonEndScheduling;

        public SchedulingViewHolder(@NonNull View itemView) {
            super(itemView);

            textPetName = itemView.findViewById(R.id.textPetName);
            textWorkerName = itemView.findViewById(R.id.textWorkerName);
            textServiceName = itemView.findViewById(R.id.textServiceName);
            textDate = itemView.findViewById(R.id.textDate);
            textStatus = itemView.findViewById(R.id.textStatus);

            buttonEditScheduling = itemView.findViewById(R.id.buttonEditScheduling);
            buttonDeleteScheduling = itemView.findViewById(R.id.buttonDeleteScheduling);
            buttonEndScheduling = itemView.findViewById(R.id.buttonEndScheduling);
        }
    }
}
