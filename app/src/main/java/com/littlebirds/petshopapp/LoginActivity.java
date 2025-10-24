package com.littlebirds.petshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button buttonRedirectRegister, buttonLogin;
    private EditText editTextEmail, editTextPassword;

    private static final String LOGIN_URL = "http://10.0.2.2:8080/user/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.editTextTextEmailAddressLogin);
        editTextPassword = findViewById(R.id.editTextTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRedirectRegister = findViewById(R.id.buttonRegisterRedirect);

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonRedirectRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    LOGIN_URL,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Exemplo: resposta com token JWT
                                String token = response.getString("token");
                                String role = response.getString("role");
                                Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();

                                // Salvar o token no SharedPreferences

                                saveToken(token);

                                // e redirecionar o usuário:

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("TOKEN", token);
                                intent.putExtra("ROLE", role);
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, "Erro ao fazer login: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void saveToken(String token) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("jwt_token", token)
                .apply();
    }
}
