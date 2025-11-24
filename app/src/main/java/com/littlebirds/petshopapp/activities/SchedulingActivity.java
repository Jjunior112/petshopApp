package com.littlebirds.petshopapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.adapters.SchedulingAdapter;
import com.littlebirds.petshopapp.models.Scheduling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulingActivity extends BaseActivity {

    private static final String SCHEDULINGS_URL = "http://10.0.2.2:8080/schedulings";

    private RecyclerView recyclerViewSchedulings;
    private SchedulingAdapter schedulingAdapter;
    private List<Scheduling> schedulingList = new ArrayList<>();
    private TextView textViewEmpty;

    private Button buttonNewScheduling;
    private String userRole = "CLIENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduling);

        // habilita navegação da BaseActivity
        setupBottomNav();

        // --- UI ---
        recyclerViewSchedulings = findViewById(R.id.recyclerViewSchedulings);
        recyclerViewSchedulings.setLayoutManager(new LinearLayoutManager(this));

        schedulingAdapter = new SchedulingAdapter(this, schedulingList);
        recyclerViewSchedulings.setAdapter(schedulingAdapter);

        textViewEmpty = findViewById(R.id.textViewEmpty);
        buttonNewScheduling = findViewById(R.id.buttonNewScheduling);

        // Obtém a ROLE salva
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userRole = prefs.getString("user_role", "CLIENT");

        // Esconder botão de novo agendamento para WORKER
        if (userRole.equalsIgnoreCase("WORKER")) {
            buttonNewScheduling.setVisibility(View.GONE);
        }

        buttonNewScheduling.setOnClickListener(v ->
                startActivity(new Intent(SchedulingActivity.this, NewSchedulingActivity.class))
        );

        loadSchedulings();
    }

    private void loadSchedulings() {

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.GET,
                SCHEDULINGS_URL,
                response -> handleResponse(response),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao carregar agendamentos.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("content");

            schedulingList.clear();

            if (jsonArray.length() == 0) {
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerViewSchedulings.setVisibility(View.GONE);
                schedulingAdapter.notifyDataSetChanged();
                return;
            }

            textViewEmpty.setVisibility(View.GONE);
            recyclerViewSchedulings.setVisibility(View.VISIBLE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject schJson = jsonArray.getJSONObject(i);

                String status = schJson.optString("status", "Não informado");
                boolean allow = filterByUserRole(userRole, status);

                if (!allow) continue;

                Scheduling scheduling = new Scheduling(
                        schJson.getLong("id"),
                        schJson.getString("petName"),
                        schJson.getString("workerName"),
                        schJson.getString("serviceName"),
                        schJson.getString("date"),
                        status
                );

                schedulingList.add(scheduling);
            }

            schedulingAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao processar dados.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean filterByUserRole(String role, String status) {
        role = role.toUpperCase();

        switch (role) {
            case "CLIENT":
                return status.equalsIgnoreCase("PENDING")
                        || status.equalsIgnoreCase("COMPLETED");

            case "WORKER":
                return status.equalsIgnoreCase("PENDING")
                        || status.equalsIgnoreCase("COMPLETED")
                        || status.equalsIgnoreCase("CANCELED");

            case "ADMIN":
                return true;

            default:
                return false;
        }
    }
}
