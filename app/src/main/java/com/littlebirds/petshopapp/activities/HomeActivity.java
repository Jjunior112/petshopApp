package com.littlebirds.petshopapp.activities;


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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.adapters.ServicesAdapter;
import com.littlebirds.petshopapp.models.Service;

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

    private TextView title;

    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil,buttonAdd,buttonList;

    private Button button,button1;
    private String userRole = "CLIENT";
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
        buttonList = findViewById(R.id.buttonList);
        buttonAdd = findViewById(R.id.buttonAdd);
        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        title = findViewById(R.id.textView9);
        recyclerServices = findViewById(R.id.recyclerServices);
        recyclerServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userRole = prefs.getString("user_role", "CLIENT");

        // -----------------------------
        // OCULTAR BOTÕES PARA WORKER
        // -----------------------------
        if (userRole.equalsIgnoreCase("WORKER")) {
            buttonAgendar.setVisibility(View.GONE);
            buttonPets.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);

            title.setText("Bem vindo!");
        }

        // -----------------------------
        // OCULTAR BOTÕES PARA ADMIN
        // -----------------------------
        if (userRole.equalsIgnoreCase("ADMIN")) {
            buttonAgendar.setVisibility(View.GONE);
            buttonPets.setVisibility(View.GONE);
            buttonAgendamentos.setVisibility(View.GONE);
            buttonPerfil.setVisibility(View.GONE);
            buttonAdd.setVisibility(View.VISIBLE);
            buttonList.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);

            title.setText("Bem vindo!");

        }

        // AÇÕES DOS BOTÕES
        buttonInicio.setOnClickListener(v ->
                Toast.makeText(this, "Você já está na Home", Toast.LENGTH_SHORT).show()
        );

        button.setOnClickListener(v ->
                startActivity(new Intent(this, NewSchedulingActivity.class))
        );

        button1.setOnClickListener(v ->
                startActivity(new Intent(this, SchedulingActivity.class))
        );

        buttonAgendar.setOnClickListener(v ->
                startActivity(new Intent(this, NewSchedulingActivity.class))
        );

        buttonPets.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, PetsActivity.class))
        );

        buttonAgendamentos.setOnClickListener(v ->
                startActivity(new Intent(this, SchedulingActivity.class))
        );

        buttonPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        buttonList.setOnClickListener(v ->
                startActivity(new Intent(this, ListAdminActivity.class))
        );
        buttonAdd.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        if(userRole.equalsIgnoreCase("CLIENT")) {
            loadServices();
        }
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