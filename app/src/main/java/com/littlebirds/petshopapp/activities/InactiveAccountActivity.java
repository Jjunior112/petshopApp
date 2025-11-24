package com.littlebirds.petshopapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class InactiveAccountActivity extends AppCompatActivity {

    private TextView textUserName, textUserEmail;
    private Button buttonDeleteAccount, buttonBack;
    private final String USER_URL = "http://10.0.2.2:8080/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inactive_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inactiveAccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textUserName = findViewById(R.id.text_user_name);
        textUserEmail = findViewById(R.id.text_user_email);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        buttonBack = findViewById(R.id.buttonBack);

        fetchUserData();

        buttonDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Deseja realmente excluir sua conta? Esta ação é irreversível.")
                    .setPositiveButton("Excluir", (dialog, which) -> deleteUserAccount())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void fetchUserData() {

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String userId = prefs.getString("user_id", null);
        if (token == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, USER_URL +  userId, null,
                response -> {
                    try {
                        String name = response.getString("fullName");
                        String email = response.getString("email");

                        textUserName.setText( name);
                        textUserEmail.setText(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar dados do usuário.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    private void deleteUserAccount() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, USER_URL, null,
                response -> {
                    Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show();

                    // Limpa token e redireciona para login
                    prefs.edit().remove("jwt_token").apply();
                    Intent intent = new Intent(InactiveAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Erro ao desativar conta.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }
}
