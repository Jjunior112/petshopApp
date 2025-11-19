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

public class SchedulingActivity extends AppCompatActivity {

    private static final String SCHEDULINGS_URL = "http://10.0.2.2:8080/schedulings";
    private RecyclerView recyclerViewSchedulings;
    private SchedulingAdapter schedulingAdapter;
    private List<Scheduling> schedulingList = new ArrayList<>();
    private TextView textViewEmpty;

    private Button buttonNewScheduling;
    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduling);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.schedulingRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewSchedulings = findViewById(R.id.recyclerViewSchedulings);
        recyclerViewSchedulings.setLayoutManager(new LinearLayoutManager(this));

        schedulingAdapter = new SchedulingAdapter(this, schedulingList);
        recyclerViewSchedulings.setAdapter(schedulingAdapter);

        textViewEmpty = findViewById(R.id.textViewEmpty);

        buttonInicio = findViewById(R.id.buttonInicio);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonPets = findViewById(R.id.buttonPets);
        buttonAgendamentos = findViewById(R.id.buttonAgendamentos);
        buttonPerfil = findViewById(R.id.buttonPerfil);
        buttonNewScheduling = findViewById(R.id.buttonNewScheduling);

        // Navegação
        buttonInicio.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        buttonPets.setOnClickListener(v -> startActivity(new Intent(this, PetsActivity.class)));
        buttonAgendar.setOnClickListener(v -> startActivity(new Intent(this, NewSchedulingActivity.class)));
        buttonAgendamentos.setOnClickListener(v ->
                Toast.makeText(this, "Você já está em Agendamentos", Toast.LENGTH_SHORT).show()
        );
        buttonPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        buttonNewScheduling.setOnClickListener(v -> startActivity(new Intent(this, NewSchedulingActivity.class)));

        loadSchedulings();
    }

    private void loadSchedulings() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                SCHEDULINGS_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("content");

                        schedulingList.clear();

                        if (jsonArray.length() == 0) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            recyclerViewSchedulings.setVisibility(View.GONE);
                            schedulingAdapter.notifyDataSetChanged();
                            return;
                        } else {
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerViewSchedulings.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject schJson = jsonArray.getJSONObject(i);

                            Scheduling scheduling = new Scheduling(
                                    Long.parseLong(schJson.getString("id")),
                                    schJson.getString("petName"),
                                    schJson.getString("workerName"),
                                    schJson.getString("serviceName"),
                                    schJson.getString("date"),
                                    schJson.optString("status", "Não informado")
                            );

                            schedulingList.add(scheduling);
                        }

                        schedulingAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta JSON.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao carregar agendamentos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
