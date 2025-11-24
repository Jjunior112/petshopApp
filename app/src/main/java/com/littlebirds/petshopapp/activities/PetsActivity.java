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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;
import com.littlebirds.petshopapp.adapters.PetsAdapter;
import com.littlebirds.petshopapp.models.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetsActivity extends BaseActivity {

    private static final String PETS_URL = "http://10.0.2.2:8080/pets";

    private RecyclerView recyclerViewPets;
    private PetsAdapter petsAdapter;
    private List<Pet> petList = new ArrayList<>();

    private TextView textViewEmpty;
    private Button buttonNewPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pets);

        setupBottomNav(); // <<< herdado do BaseActivity

        recyclerViewPets = findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(this));

        petsAdapter = new PetsAdapter(this, petList);
        recyclerViewPets.setAdapter(petsAdapter);

        textViewEmpty = findViewById(R.id.textViewEmpty);
        buttonNewPet = findViewById(R.id.buttonNewPet);

        buttonNewPet.setOnClickListener(v ->
                startActivity(new Intent(PetsActivity.this, NewPetActivity.class))
        );

        loadPets();
    }

    private void loadPets() {
        String token = getToken();
        if (token == null) return;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                PETS_URL,
                response -> handlePetsResponse(response),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao carregar pets.", Toast.LENGTH_SHORT).show();
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

    private void handlePetsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("content");

            petList.clear();

            if (jsonArray.length() == 0) {
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerViewPets.setVisibility(View.GONE);
                petsAdapter.notifyDataSetChanged();
                return;
            }

            textViewEmpty.setVisibility(View.GONE);
            recyclerViewPets.setVisibility(View.VISIBLE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject petJson = jsonArray.getJSONObject(i);

                String petType;
                switch (petJson.getString("petType")) {
                    case "DOG":
                        petType = "Cachorro";
                        break;
                    case "CAT":
                        petType = "Gato";
                        break;
                    default:
                        petType = "Não informado";
                        break;
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
            Toast.makeText(this, "Erro ao processar dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
        }

        return token;
    }
}
