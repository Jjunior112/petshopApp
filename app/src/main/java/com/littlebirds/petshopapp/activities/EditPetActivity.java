package com.littlebirds.petshopapp.activities;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPetActivity extends AppCompatActivity {

    private EditText textName, textRace, textColor, textBorn;
    private Button buttonConfirmEdit;
    private String PETS_URL = "http://10.0.2.2:8080/pets/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pet);

        long petId = getIntent().getLongExtra("petId", -1);

        if (petId != -1) {
            fetchPetById(petId);
        } else {
            Toast.makeText(this, "ID do pet inválido.", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPet), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textName = findViewById(R.id.edit_text_name);
        textRace = findViewById(R.id.edit_text_race);
        textColor = findViewById(R.id.edit_text_color);
        textBorn = findViewById(R.id.edit_text_born);
        buttonConfirmEdit = findViewById(R.id.buttonConfirmEdit);

        buttonConfirmEdit.setOnClickListener(v->{
            String name = textName.getText().toString().trim();
            String race = textRace.getText().toString().trim();
            String color = textColor.getText().toString().trim();
            String born = textBorn.getText().toString().trim();

            // Chama a função editPet com o ID do pet e os valores atualizados
            editPet(petId, name, race, color, born);
        });

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
    }

    private void editPet(Long id, String name,  String race, String color, String born) {
        String url = PETS_URL + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        // Cria o JSON com os dados atualizados
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("race", race);
            jsonBody.put("color", color);
            jsonBody.put("born", born);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    try {
                        // Atualiza os TextViews com os dados retornados do backend
                        textName.setText(response.getString("name"));
                        textRace.setText(response.getString("race"));
                        textColor.setText(response.optString("color", "Não informado"));
                        textBorn.setText(response.getString("born"));

                        Toast.makeText(this, "Pet atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar dados do pet.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao atualizar pet: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }


}
