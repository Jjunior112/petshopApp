package com.littlebirds.petshopapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.activities.DeletePetActivity;
import com.littlebirds.petshopapp.activities.EditPetActivity;
import com.littlebirds.petshopapp.models.Pet;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetViewHolder> {

    private final List<Pet> petList;
    private final Context context;

    public PetsAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);

        holder.textPetName.setText(pet.getName());
        holder.textPetType.setText("Tipo: " + pet.getPetType());
        holder.textPetRace.setText("Raça: " + pet.getRace());
        holder.textPetColor.setText("Cor: " + pet.getColor());

        // 🔹 Configura botões dentro do item
        holder.buttonEditPet.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPetActivity.class);
            intent.putExtra("petId", pet.getId()); // passe o ID do pet se necessário
            context.startActivity(intent);
        });

        holder.buttonDeletePet.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeletePetActivity.class);
            intent.putExtra("petId", pet.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView textPetName, textPetType, textPetRace, textPetColor;
        ImageButton buttonEditPet, buttonDeletePet;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            textPetName = itemView.findViewById(R.id.textPetName);
            textPetType = itemView.findViewById(R.id.textPetType);
            textPetRace = itemView.findViewById(R.id.textPetRace);
            textPetColor = itemView.findViewById(R.id.textPetColor);
            buttonEditPet = itemView.findViewById(R.id.buttonEditPet);
            buttonDeletePet = itemView.findViewById(R.id.buttonDeletePet);
        }
    }
}
