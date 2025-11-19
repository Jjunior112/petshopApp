package com.littlebirds.petshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    private TextView textErrorLogin;

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
        textErrorLogin = findViewById(R.id.textErrorLogin);

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonRedirectRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        ImageView imageGoogleLogin = findViewById(R.id.imageGoogleLogin);

        imageGoogleLogin.setOnClickListener(v -> {
            // Aqui você implementa a lógica do login com Google
            Toast.makeText(this, "Login com Google clicado!", Toast.LENGTH_SHORT).show();

            // Exemplo: iniciar fluxo de autenticação do Google Sign-In
            // startActivity(new Intent(this, GoogleSignInActivity.class));
        });
    }
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        textErrorLogin.setVisibility(View.GONE); // Oculta erro ao tentar de novo

        if (email.isEmpty() || password.isEmpty()) {
            textErrorLogin.setText("Preencha todos os campos!");
            textErrorLogin.setVisibility(View.VISIBLE);
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
                    response -> {
                        try {
                            String token = response.getString("token");
                            String role = response.getString("role");
                            String userId = response.getString("userId");

                            // Sucesso = Toast permanece
                            Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();

                            saveToken(token, userId);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("TOKEN", token);
                            intent.putExtra("ROLE", role);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            textErrorLogin.setText("Erro ao processar resposta.");
                            textErrorLogin.setVisibility(View.VISIBLE);
                        }
                    },
                    error -> {
                        String errorMessage = "Erro ao fazer login";

                        try {
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorJson = new JSONObject(body);

                                errorMessage = errorJson.optString(
                                        "message",
                                        "Não foi possível fazer login. Verifique os dados."
                                );
                            }
                        } catch (Exception ignored) {}

                        // Exibe a mensagem vinda do servidor
                        textErrorLogin.setText(errorMessage);
                        textErrorLogin.setVisibility(View.VISIBLE);
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveToken(String token,String userId) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("jwt_token", token)
                .putString("user_id", userId)
                .apply();
    }
}
