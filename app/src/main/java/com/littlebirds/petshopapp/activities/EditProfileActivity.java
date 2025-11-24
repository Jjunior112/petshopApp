package com.littlebirds.petshopapp.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFullName, editPhone, editStreet, editNeighborhood, editZipCode, editCity, editState, editComplement, editNumber;
    private Button buttonSave, buttonCancel;

    private static final String BASE_URL = "http://10.0.2.2:8080/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa os campos
        editFullName = findViewById(R.id.editFullName);
        editPhone = findViewById(R.id.editPhone);
        editStreet = findViewById(R.id.editStreet);
        editNeighborhood = findViewById(R.id.editNeighborhood);
        editZipCode = findViewById(R.id.editZipCode);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editComplement = findViewById(R.id.editComplement);
        editNumber = findViewById(R.id.editNumber);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Botão Cancelar
        buttonCancel.setOnClickListener(v -> finish());

        // Carrega os dados atuais do usuário
        loadUserProfile();

        // Botão Salvar
        buttonSave.setOnClickListener(v -> updateUserProfile());
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String userId = prefs.getString("user_id", null);

        if (token == null || userId == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String url = BASE_URL + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        editFullName.setText(response.getString("fullName"));
                        editPhone.setText(response.optString("phone", ""));

                        JSONObject address = response.getJSONObject("addressListDto");
                        editStreet.setText(address.getString("street"));
                        editNeighborhood.setText(address.getString("neighborhood"));
                        editZipCode.setText(address.getString("zipCode"));
                        editCity.setText(address.getString("city"));
                        editState.setText(address.getString("state"));
                        editComplement.setText(address.optString("complement", ""));
                        editNumber.setText(address.optString("number", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro na requisição: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateUserProfile() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String userId = prefs.getString("user_id", null);

        if (token == null || userId == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + userId;

        JSONObject body = new JSONObject();
        JSONObject address = new JSONObject();

        try {
            address.put("street", editStreet.getText().toString());
            address.put("neighborhood", editNeighborhood.getText().toString());
            address.put("zipCode", editZipCode.getText().toString());
            address.put("city", editCity.getText().toString());
            address.put("state", editState.getText().toString());
            address.put("complement", editComplement.getText().toString());
            address.put("number", editNumber.getText().toString());

            body.put("fullName", editFullName.getText().toString());
            body.put("phone", editPhone.getText().toString());
            body.put("addressRegisterDto", address);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao atualizar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
