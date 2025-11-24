package com.littlebirds.petshopapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class DeletePetActivity  extends AppCompatActivity {

    private TextView textName, textRace, textColor, textBorn;
    private Button buttonConfirmDelete;
    private String PETS_URL = "http://10.0.2.2:8080/pets/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_pet);

        long petId = getIntent().getLongExtra("petId", -1);

        if (petId != -1) {
            fetchPetById(petId);
        } else {
            Toast.makeText(this, "ID do pet inválido.", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.deletePet), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textName = findViewById(R.id.delete_text_name);
        textRace = findViewById(R.id.delete_text_race);
        textColor = findViewById(R.id.delete_text_color);
        textBorn = findViewById(R.id.delete_text_born);
        buttonConfirmDelete = findViewById(R.id.buttonConfirmDelete);

        buttonConfirmDelete.setOnClickListener(v -> deletePet(petId));

    }

    private void deletePet(long petId) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                PETS_URL + petId,
                response -> {
                    Toast.makeText(this, "Pet excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao excluir pet: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void fetchPetById(Long id) {
        String url = PETS_URL + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        String race = response.getString("race");
                        String color = response.optString("color", "Não informado");
                        String born = response.getString("born");

                        textName.setText(name);
                        textRace.setText(race);
                        textColor.setText(color);
                        textBorn.setText(born);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar dados do pet.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao buscar pet: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }}
