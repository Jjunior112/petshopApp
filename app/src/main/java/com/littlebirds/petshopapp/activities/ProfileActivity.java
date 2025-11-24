package com.littlebirds.petshopapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private ImageButton buttonEditProfile, buttonDeleteProfile;
    private TextView textFullName, textEmail, textAddress, textCep, textCity;
    private Button buttonLogout;

    private String userRole = "CLIENT";
    private static final String BASE_URL = "http://10.0.2.2:8080/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        setupBottomNav(); // <<< herdado do BaseActivity

        initViews();
        setupRoleVisibility();
        setupListeners();

        loadUserProfile();
    }

    private void initViews() {
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonDeleteProfile = findViewById(R.id.buttonDeleteProfile);
        textFullName = findViewById(R.id.textFullName);
        textEmail = findViewById(R.id.textEmail);
        textAddress = findViewById(R.id.textAddress);
        textCep = findViewById(R.id.textCep);
        textCity = findViewById(R.id.textCity);
        buttonLogout = findViewById(R.id.buttonLogout);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userRole = prefs.getString("user_role", "CLIENT");
    }

    private void setupRoleVisibility() {
        // Usuário WORKER não pode editar, deletar, nem usar Pets e Agendar
        if (userRole.equalsIgnoreCase("WORKER")) {
            findViewById(R.id.buttonAgendar).setVisibility(View.GONE);
            findViewById(R.id.buttonPets).setVisibility(View.GONE);
            buttonDeleteProfile.setVisibility(View.GONE);
            buttonEditProfile.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        buttonEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        buttonDeleteProfile.setOnClickListener(v -> confirmDeleteAccount());

        buttonLogout.setOnClickListener(v -> logoutUser());
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

        StringRequest request = new StringRequest(
                Request.Method.GET,
                BASE_URL + userId,
                response -> handleProfileResponse(response),
                error -> Toast.makeText(this, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show()
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

    private void handleProfileResponse(String response) {
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
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja prosseguir para a exclusão da conta?")
                .setPositiveButton("Continuar", (dialog, which) -> {
                    startActivity(new Intent(this, InactiveAccountActivity.class));
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

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
