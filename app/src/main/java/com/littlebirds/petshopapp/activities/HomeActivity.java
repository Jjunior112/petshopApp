package com.littlebirds.petshopapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class HomeActivity extends BaseActivity {

    private static final String URL_SERVICES = "http://10.0.2.2:8080/services";

    private RecyclerView recyclerServices;
    private ServicesAdapter adapter;
    private TextView title;

    private String userRole = "CLIENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupBottomNav(); // <<< A barra de navegação agora é carregada aqui

        title = findViewById(R.id.textView9);
        recyclerServices = findViewById(R.id.recyclerServices);
        recyclerServices.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userRole = prefs.getString("user_role", "CLIENT");

        // O título antes era configurado no Home pela role; agora isso está no BaseActivity.
        // Só ajustamos aqui para clientes se quiser personalizar.
        if (userRole.equalsIgnoreCase("CLIENT")) {
            title.setText("Serviços disponíveis");
            loadServices();
        } else {
            title.setText("Bem vindo!");
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

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_SERVICES,
                null,
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
