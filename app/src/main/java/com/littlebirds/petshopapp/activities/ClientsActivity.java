package com.littlebirds.petshopapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.adapters.ClientsAdapter;
import com.littlebirds.petshopapp.models.ClientDto;
import com.littlebirds.petshopapp.models.SimpleItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsActivity extends BaseActivity {

    private static final String URL_CLIENTS = "http://10.0.2.2:8080/user?role=CLIENT";

    private RecyclerView recyclerClients;
    private ClientsAdapter adapter;

    private List<ClientDto> clients = new ArrayList<>();
    private List<ClientDto> filtered = new ArrayList<>();

    private EditText inputSearch;
    private Spinner spinnerStatus, spinnerSort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        // Inicializa bottom navigation herdado
        setupBottomNav();

        recyclerClients = findViewById(R.id.recyclerClients);
        recyclerClients.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClientsAdapter(filtered);
        recyclerClients.setAdapter(adapter);

        inputSearch = findViewById(R.id.inputSearch);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerSort = findViewById(R.id.spinnerSort);

        setupSpinners();
        fetchClients();
        setupFilters();
    }

    private void setupSpinners() {
        spinnerStatus.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todos", "Ativo", "Inativo"}
        ));

        spinnerSort.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"A-Z", "Z-A"}
        ));
    }

    private void fetchClients() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_CLIENTS,
                null,
                response -> {
                    clients.clear();
                    try {
                        JSONArray array = response.getJSONArray("content");
                        parseClients(array);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    applyFilters();
                },
                error -> {
                    Toast.makeText(this, "Erro ao buscar clientes: " + error.toString(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void parseClients(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                if (!obj.optString("role", "").equalsIgnoreCase("CLIENT")) continue;

                ClientDto c = new ClientDto();
                c.setId(obj.optString("id"));
                c.setFullName(obj.optString("fullName"));
                c.setEmail(obj.optString("email"));
                c.setPhone(obj.optString("phone"));
                c.setActive(obj.optBoolean("isActive"));

                JSONObject addr = obj.optJSONObject("addressListDto");
                if (addr != null) {
                    String fullAddress =
                            addr.optString("street", "") + ", " +
                                    addr.optString("number", "") + " - " +
                                    addr.optString("neighborhood", "") + ", " +
                                    addr.optString("city", "") + " - " +
                                    addr.optString("state", "");

                    c.setAddress(fullAddress);
                } else {
                    c.setAddress("Sem endereço cadastrado");
                }

                clients.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFilters() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
        });

        spinnerStatus.setOnItemSelectedListener(new SimpleItemSelectedListener(this::applyFilters));
        spinnerSort.setOnItemSelectedListener(new SimpleItemSelectedListener(this::applyFilters));
    }

    private void applyFilters() {
        String query = inputSearch.getText().toString().trim().toLowerCase();
        String status = spinnerStatus.getSelectedItem().toString();
        String sort = spinnerSort.getSelectedItem().toString();

        filtered.clear();

        for (ClientDto c : clients) {
            boolean matchName = c.getFullName().toLowerCase().contains(query);
            boolean matchStatus =
                    status.equals("Todos")
                            || (status.equals("Ativo") && c.isActive())
                            || (status.equals("Inativo") && !c.isActive());

            if (matchName && matchStatus) {
                filtered.add(c);
            }
        }

        if (sort.equals("A-Z")) {
            filtered.sort(Comparator.comparing(ClientDto::getFullName));
        } else {
            filtered.sort((a, b) -> b.getFullName().compareTo(a.getFullName()));
        }

        adapter.updateList(filtered);
    }
}
