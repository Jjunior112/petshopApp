package com.littlebirds.petshopapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String URL_SERVICES = "http://10.0.2.2:8080/services";
    private RecyclerView recyclerServices;
    private ServicesAdapter adapter;

    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil;

    private Button button;

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
        button = findViewById(R.id.button);
        recyclerServices = findViewById(R.id.recyclerServices);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 🔹 Define as ações de clique

        buttonInicio.setOnClickListener(v -> {
            // opcional: apenas fechar menu ou atualizar UI
            Toast.makeText(this, "Você já está na Home", Toast.LENGTH_SHORT).show();
        });

        button.setOnClickListener(v -> startActivity(new Intent(this, NewSchedulingActivity.class)));

        buttonAgendar.setOnClickListener(v -> startActivity(new Intent(this, NewSchedulingActivity.class)));

        buttonPets.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PetsActivity.class)));

        buttonAgendamentos.setOnClickListener(v -> startActivity(new Intent(this, SchedulingActivity.class)));

        buttonPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        loadServices();
    }

    private void loadServices() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_SERVICES, null,
                response -> {
                    List<Service> services = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            services.add(new Service(
                                    obj.getLong("id"),
                                    obj.getString("name"),
                                    obj.getDouble("price"),
                                    obj.getString("serviceType")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter = new ServicesAdapter(services);
                    recyclerServices.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Erro ao carregar serviços", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}