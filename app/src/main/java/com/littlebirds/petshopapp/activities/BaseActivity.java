package com.littlebirds.petshopapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebirds.petshopapp.R;


public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNav() {

        // ==============================
        //     PEGAR ROLE DO USUÁRIO
        // ==============================
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "CLIENT");

        // ==============================
        //     PEGAR A VIEW DA NAVBAR
        // ==============================
        View nav = findViewById(R.id.bottom_navigation);
        if (nav == null) {
            return; // evita crash se alguma Activity não tiver barra
        }

        // ==============================
        //      PEGAR OS BOTÕES
        // ==============================
        ImageButton buttonInicio        = nav.findViewById(R.id.buttonInicio);
        ImageButton buttonAgendar       = nav.findViewById(R.id.buttonAgendar);
        ImageButton buttonPets          = nav.findViewById(R.id.buttonPets);
        ImageButton buttonAgendamentos  = nav.findViewById(R.id.buttonAgendamentos);
        ImageButton buttonPerfil        = nav.findViewById(R.id.buttonPerfil);
        ImageButton buttonAdd           = nav.findViewById(R.id.buttonAdd);
        ImageButton buttonList          = nav.findViewById(R.id.buttonList);

        // Botões extras que existiam no Home
        Button button  = nav.findViewById(R.id.button);
        Button button1 = nav.findViewById(R.id.button1);

        // Título opcional
        TextView title = findViewById(R.id.textView9);

        // ==============================
        //     CONFIGURAÇÃO POR ROLE
        // ==============================

        switch (userRole.toUpperCase()) {

            case "CLIENT":
                // Cliente usa tudo normal
                if (buttonAdd != null) buttonAdd.setVisibility(View.GONE);
                if (buttonList != null) buttonList.setVisibility(View.GONE);

                if (button != null) button.setVisibility(View.VISIBLE);
                if (button1 != null) button1.setVisibility(View.VISIBLE);
                break;

            case "WORKER":
                if (buttonAgendar != null) buttonAgendar.setVisibility(View.GONE);
                if (buttonPets != null) buttonPets.setVisibility(View.GONE);

                if (button != null) button.setVisibility(View.GONE);
                if (button1 != null) button1.setVisibility(View.VISIBLE);

                if (buttonAdd != null) buttonAdd.setVisibility(View.GONE);
                if (buttonList != null) buttonList.setVisibility(View.GONE);

                if (title != null) title.setText("Bem vindo!");
                break;

            case "ADMIN":
                if (buttonAgendar != null) buttonAgendar.setVisibility(View.GONE);
                if (buttonPets != null) buttonPets.setVisibility(View.GONE);
                if (buttonAgendamentos != null) buttonAgendamentos.setVisibility(View.GONE);
                if (buttonPerfil != null) buttonPerfil.setVisibility(View.GONE);

                if (button != null) button.setVisibility(View.GONE);
                if (button1 != null) button1.setVisibility(View.VISIBLE);

                if (buttonAdd != null) buttonAdd.setVisibility(View.VISIBLE);
                if (buttonList != null) buttonList.setVisibility(View.VISIBLE);

                if (title != null) title.setText("Bem vindo!");
                break;
        }

        // ==============================
        //     AÇÕES DOS BOTÕES
        // ==============================

        if (buttonInicio != null) {
            buttonInicio.setOnClickListener(v ->
                    startActivity(new Intent(this, HomeActivity.class))
            );
        }

        if (buttonAgendar != null) {
            buttonAgendar.setOnClickListener(v ->
                    startActivity(new Intent(this, NewSchedulingActivity.class))
            );
        }

        if (buttonPets != null) {
            buttonPets.setOnClickListener(v ->
                    startActivity(new Intent(this, PetsActivity.class))
            );
        }

        if (buttonAgendamentos != null) {
            buttonAgendamentos.setOnClickListener(v ->
                    startActivity(new Intent(this, SchedulingActivity.class))
            );
        }

        if (buttonPerfil != null) {
            buttonPerfil.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class))
            );
        }

        if (button != null) {
            button.setOnClickListener(v ->
                    startActivity(new Intent(this, NewSchedulingActivity.class))
            );
        }

        if (button1 != null) {
            button1.setOnClickListener(v ->
                    startActivity(new Intent(this, SchedulingActivity.class))
            );
        }

        if (buttonList != null) {
            buttonList.setOnClickListener(v ->
                    startActivity(new Intent(this, ListAdminActivity.class))
            );
        }

        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class))
            );
        }

    }
}
