package com.littlebirds.petshopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetsActivity extends AppCompatActivity {

    private static final String PETS_URL = "http://10.0.2.2:8080/pets";
    private RecyclerView recyclerViewPets;
    private PetsAdapter petsAdapter;
    private List<Pet> petList = new ArrayList<>();

    private Button buttonNewPet;
    private ImageButton buttonAgendar, buttonInicio, buttonPets, buttonAgendamentos, buttonPerfil;

    private TextView textViewEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pets);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pets), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Usa as variáveis da classe
        recyclerViewPets = findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(this));

        petsAdapter = new PetsAdapter(this, petList); // inicializa o adapter da classe
        recyclerViewPets.setAdapter(petsAdapter);

        buttonInicio = findViewById(R.id.buttonInicio);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonPets = findViewById(R.id.buttonPets);
        buttonAgendamentos = findViewById(R.id.buttonAgendamentos);
        buttonPerfil = findViewById(R.id.buttonPerfil);
        textViewEmpty=findViewById(R.id.textViewEmpty);


        buttonNewPet = findViewById(R.id.buttonNewPet);

        buttonNewPet.setOnClickListener(v-> startActivity(new Intent(PetsActivity.this,NewPetActivity.class)));

        // 🔹 Define as ações de clique

        buttonPets.setOnClickListener(v -> {
            // opcional: apenas fechar menu ou atualizar UI
            Toast.makeText(this, "Você já está em Pets", Toast.LENGTH_SHORT).show();
        });

        //buttonAgendar.setOnClickListener(v -> startActivity(new Intent(PetsActivity.this, AgendarActivity.class)));

        buttonInicio.setOnClickListener(v -> startActivity(new Intent(PetsActivity.this, HomeActivity.class)));

        buttonAgendamentos.setOnClickListener(v -> startActivity(new Intent(PetsActivity.this, SchedulingActivity.class)));

        buttonPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        loadPets();
    }

    private void loadPets() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                PETS_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("content");

                        System.out.println(response);

                        petList.clear(); // limpa lista anterior

                        if (jsonArray.length() == 0) {
                            // Nenhum pet encontrado: mostra mensagem e oculta RecyclerView
                            textViewEmpty.setVisibility(View.VISIBLE);
                            recyclerViewPets.setVisibility(View.GONE);
                            petsAdapter.notifyDataSetChanged();
                            return;
                        } else {
                            // Pets encontrados: mostra RecyclerView e oculta mensagem
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerViewPets.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject petJson = jsonArray.getJSONObject(i);
                            String petType = petJson.getString("petType");

                            if (petType.equalsIgnoreCase("DOG")) {
                                petType = "Cachorro";
                            } else if (petType.equalsIgnoreCase("CAT")) {
                                petType = "Gato";
                            } else {
                                petType = "Não informado";
                            }

                            Pet pet = new Pet(
                                    petJson.getLong("id"),
                                    petJson.getString("name"),
                                    petType,
                                    petJson.getString("race"),
                                    petJson.optString("color", "Não informada")
                            );

                            petList.add(pet);
                        }

                        petsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta JSON.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao carregar pets: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


}
