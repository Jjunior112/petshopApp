package com.littlebirds.petshopapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil;
    private ImageButton buttonEditProfile, buttonDeleteProfile;
    private TextView textFullName, textEmail, textAddress, textCep, textCity;
    private Button buttonLogout;

    private static final String BASE_URL = "http://10.0.2.2:8080/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa elementos da interface
        buttonInicio = findViewById(R.id.buttonInicio);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonPets = findViewById(R.id.buttonPets);
        buttonAgendamentos = findViewById(R.id.buttonAgendamentos);
        buttonPerfil = findViewById(R.id.buttonPerfil);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonDeleteProfile = findViewById(R.id.buttonDeleteProfile);

        textFullName = findViewById(R.id.textFullName);
        textEmail = findViewById(R.id.textEmail);
        textAddress = findViewById(R.id.textAddress);
        textCep = findViewById(R.id.textCep);
        textCity = findViewById(R.id.textCity);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Navegação inferior
        buttonPerfil.setOnClickListener(v ->
                Toast.makeText(this, "Você já está no seu perfil", Toast.LENGTH_SHORT).show()
        );

        buttonAgendar.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, NewSchedulingActivity.class))
        );

        buttonPets.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, PetsActivity.class))
        );

        buttonAgendamentos.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SchedulingActivity.class))
        );

        buttonInicio.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class))
        );

        // Botão Editar Perfil
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Botão Excluir Conta — agora apenas redireciona
        buttonDeleteProfile.setOnClickListener(v -> confirmDeleteAccount());

        // Logout
        buttonLogout.setOnClickListener(v -> logoutUser());

        // Carrega dados do usuário
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String userId = prefs.getString("user_id", null);

        if (token == null || userId == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + userId,
                response -> {
                    try {
                        JSONObject userJson = new JSONObject(response);
                        String fullName = userJson.getString("fullName");
                        String email = userJson.getString("email");

                        JSONObject addressJson = userJson.getJSONObject("addressListDto");
                        String street = addressJson.getString("street");
                        String number = addressJson.getString("number");
                        String neighborhood = addressJson.getString("neighborhood");
                        String city = addressJson.getString("city");
                        String state = addressJson.getString("state");
                        String zipCode = addressJson.getString("zipCode");

                        textFullName.setText(fullName);
                        textEmail.setText("Email: " + email);
                        textAddress.setText("Endereço: " + street + ", " + number + " - " + neighborhood);
                        textCep.setText("CEP: " + zipCode);
                        textCity.setText("Cidade: " + city + " - " + state);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar dados do usuário.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                String token = prefs.getString("jwt_token", null);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja prosseguir para a exclusão da conta?")
                .setPositiveButton("Continuar", (dialog, which) -> {
                    Intent intent = new Intent(ProfileActivity.this, InactiveAccountActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("jwt_token");
        editor.remove("user_id");
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
