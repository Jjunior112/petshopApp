package com.littlebirds.petshopapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

public class DeletePetActivity extends BaseActivity {

    private TextView textName, textRace, textColor, textBorn;
    private Button buttonConfirmDelete;

    private static final String PETS_URL = "http://10.0.2.2:8080/pets/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pet);

        // Ativa o bottom navigation base
        setupBottomNav();

        textName  = findViewById(R.id.delete_text_name);
        textRace  = findViewById(R.id.delete_text_race);
        textColor = findViewById(R.id.delete_text_color);
        textBorn  = findViewById(R.id.delete_text_born);
        buttonConfirmDelete = findViewById(R.id.buttonConfirmDelete);

        long petId = getIntent().getLongExtra("petId", -1);

        if (petId == -1) {
            Toast.makeText(this, "ID do pet inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchPetById(petId);

        buttonConfirmDelete.setOnClickListener(v -> deletePet(petId));
    }

    // ----------------------------------------------------
    // DELETE PET
    // ----------------------------------------------------
    private void deletePet(long petId) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
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
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ----------------------------------------------------
    // GET PET BY ID
    // ----------------------------------------------------
    private void fetchPetById(long id) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = PETS_URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        textName.setText(response.getString("name"));
                        textRace.setText(response.getString("race"));
                        textColor.setText(response.optString("color", "Não informado"));
                        textBorn.setText(response.getString("born"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar dados do pet.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao buscar pet: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
