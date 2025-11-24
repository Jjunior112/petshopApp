package com.littlebirds.petshopapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewPetActivity extends AppCompatActivity {

    private String PETS_URL = "http://10.0.2.2:8080/pets";

    private Button buttonConfirmNewPet;
    private Spinner spinnerPetType;
    private EditText textName, textRace, textColor, textBorn;

    private String selectedPetType = "DOG"; // valor padrão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_pet);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.newPet), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textName = findViewById(R.id.newPet_text_name);
        textRace = findViewById(R.id.newPet_text_race);
        textColor = findViewById(R.id.newPet_text_color);
        textBorn = findViewById(R.id.newPet_text_born);

        // DatePicker com dd/MM/yyyy para usuário e yyyy-MM-dd para backend
        textBorn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Para exibição
                        String displayDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        textBorn.setText(displayDate);

                        // Para envio ao backend
                        String serverDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        textBorn.setTag(serverDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        spinnerPetType = findViewById(R.id.spinnerPetType);
        buttonConfirmNewPet = findViewById(R.id.buttonConfirmNewPet);

        // Spinner com valores visuais e internos
        String[] displayValues = {"Cachorro", "Gato"};
        String[] realValues = {"DOG", "CAT"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, displayValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetType.setAdapter(adapter);

        spinnerPetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedPetType = realValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPetType = "DOG";
            }
        });

        // Clique do botão
        buttonConfirmNewPet.setOnClickListener(v -> {
            String name = textName.getText().toString().trim();
            String race = textRace.getText().toString().trim();
            String color = textColor.getText().toString().trim();
            String born = (String) textBorn.getTag();

            if (name.isEmpty() || race.isEmpty() || color.isEmpty() || born == null || born.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            postPet(name, selectedPetType, race, color, born);
        });
    }

    private void postPet(String name, String petType, String race, String color, String born) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("petType", petType);
            jsonBody.put("race", race);
            jsonBody.put("color", color);
            jsonBody.put("born", born); // formato LocalDate válido yyyy-MM-dd
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PETS_URL, jsonBody,
                response -> {
                    Toast.makeText(this, "Pet salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    // Redireciona para HomeActivity
                    Intent intent = new Intent(NewPetActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Erro ao criar pet: " + error.getMessage(), Toast.LENGTH_LONG).show()
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
