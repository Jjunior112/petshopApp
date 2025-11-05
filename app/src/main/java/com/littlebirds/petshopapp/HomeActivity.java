package com.littlebirds.petshopapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        buttonInicio = findViewById(R.id.buttonInicio);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonPets = findViewById(R.id.buttonPets);
        buttonAgendamentos = findViewById(R.id.buttonAgendamentos);
        buttonPerfil = findViewById(R.id.buttonPerfil);

        // 🔹 Define as ações de clique

        buttonInicio.setOnClickListener(v -> {
            // opcional: apenas fechar menu ou atualizar UI
            Toast.makeText(this, "Você já está na Home", Toast.LENGTH_SHORT).show();
        });

        //buttonAgendar.setOnClickListener(v -> startActivity(new Intent(this, AgendarActivity.class)));

        buttonPets.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PetsActivity.class)));

        buttonAgendamentos.setOnClickListener(v -> startActivity(new Intent(this, SchedulingActivity.class)));

        buttonPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}