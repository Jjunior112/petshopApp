package com.littlebirds.petshopapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeleteSchedulingActivity extends BaseActivity {

    private static final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings/";

    private TextView textPetName, textServiceType, textDate;
    private Button buttonDeleteScheduling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_scheduling);

        // Ativa a navegação inferior
        setupBottomNav();

        long schedulingId = getIntent().getLongExtra("schedulingId", -1);
        if (schedulingId == -1) {
            Toast.makeText(this, "ID inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textPetName = findViewById(R.id.text_pet_name);
        textServiceType = findViewById(R.id.text_service_type);
        textDate = findViewById(R.id.text_date);
        buttonDeleteScheduling = findViewById(R.id.buttonDeleteScheduling);

        fetchSchedulingById(schedulingId);

        buttonDeleteScheduling.setOnClickListener(v -> showConfirmDialog(schedulingId));
    }

    // ----------------------------------------------------
    // CONFIRMAR EXCLUSÃO
    // ----------------------------------------------------
    private void showConfirmDialog(long id) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente excluir este agendamento?")
                .setPositiveButton("Sim", (d, w) -> deleteScheduling(id))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ----------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------
    private void fetchSchedulingById(long id) {
        String url = SCHEDULING_URL + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String petName = response.getString("petName");
                        String serviceType = response.getString("serviceName").replace("_", " ");
                        String dateTime = response.getString("date");

                        String dateFormatted = formatDate(dateTime);

                        textPetName.setText(petName);
                        textServiceType.setText(serviceType);
                        textDate.setText(dateFormatted);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao interpretar dados.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao buscar agendamento.", Toast.LENGTH_SHORT).show()
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

    // ----------------------------------------------------
    // DELETE
    // ----------------------------------------------------
    private void deleteScheduling(long id) {
        String url = SCHEDULING_URL + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    Toast.makeText(this, "Agendamento excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Erro ao excluir.", Toast.LENGTH_SHORT).show()
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

    // ----------------------------------------------------
    // FORMATADOR
    // ----------------------------------------------------
    private String formatDate(String raw) {
        if (!raw.contains("T")) return raw;

        try {
            String[] parts = raw.split("T");
            String date = parts[0];
            String time = parts[1].replace("Z", "");

            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat br = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            return br.format(iso.parse(date)) + " " + time;

        } catch (ParseException e) {
            e.printStackTrace();
            return raw;
        }
    }
}
