package com.littlebirds.petshopapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeleteSchedulingActivity extends AppCompatActivity {

    private final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings/";

    private TextView textPetName, textServiceType, textDate;
    private Button buttonDeleteScheduling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_scheduling);

        long schedulingId = getIntent().getLongExtra("schedulingId", -1);
        if (schedulingId == -1) {
            Toast.makeText(this, "ID do agendamento inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.deleteSchedulingLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textPetName = findViewById(R.id.text_pet_name);
        textServiceType = findViewById(R.id.text_service_type);
        textDate = findViewById(R.id.text_date);
        buttonDeleteScheduling = findViewById(R.id.buttonDeleteScheduling);

        fetchSchedulingById(schedulingId);

        buttonDeleteScheduling.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Deseja realmente excluir este agendamento?")
                    .setPositiveButton("Sim", (dialog, which) -> deleteScheduling(schedulingId))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void fetchSchedulingById(Long id) {
        String url = SCHEDULING_URL + id;
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String petName = response.getString("petName");
                        String serviceType = response.getString("serviceType").replace("_", " ");
                        String dateTime = response.getString("date");

                        String dateFormatted = "";
                        if (dateTime.contains("T")) {
                            String[] parts = dateTime.split("T");
                            dateFormatted = formatDateToBrazilian(parts[0]) + " " + parts[1].replace("Z", "");
                        }

                        textPetName.setText(   petName);
                        textServiceType.setText( serviceType);
                        textDate.setText(dateFormatted);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao interpretar dados.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao buscar agendamento.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    private void deleteScheduling(Long id) {
        String url = SCHEDULING_URL + id;
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(this, "Agendamento excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // volta à tela anterior
                },
                error -> Toast.makeText(this, "Erro ao excluir agendamento.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    private String formatDateToBrazilian(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return brFormat.format(isoFormat.parse(isoDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }
}
