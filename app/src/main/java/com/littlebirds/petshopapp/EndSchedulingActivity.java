package com.littlebirds.petshopapp;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EndSchedulingActivity extends AppCompatActivity {

    private TextView textServiceTypeEnd, textDateEnd, textTimeEnd, textPetNameEnd;
    private Button buttonConfirmEndScheduling;

    private final String URL_COMPLETE = "http://10.0.2.2:8080/schedulings/complete/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_scheduling);

        long schedulingId = getIntent().getLongExtra("schedulingId", -1);
        if (schedulingId == -1) {
            Toast.makeText(this, "ID inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editScheduling), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        textServiceTypeEnd = findViewById(R.id.text_service_type_end);
        textDateEnd = findViewById(R.id.text_date_end);
        textTimeEnd = findViewById(R.id.text_time_end);
        textPetNameEnd = findViewById(R.id.text_pet_name_end);
        buttonConfirmEndScheduling = findViewById(R.id.buttonConfirmEndScheduling);

        fetchScheduling(schedulingId);

        buttonConfirmEndScheduling.setOnClickListener(v -> {
            completeScheduling(schedulingId);
        });
    }

    private void fetchScheduling(long id) {
        String url = "http://10.0.2.2:8080/schedulings/" + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String service = response.getString("serviceName");
                        String pet = response.getString("petName");
                        String dateTime = response.getString("date");

                        textServiceTypeEnd.setText(service);
                        textPetNameEnd.setText(pet);

                        if (dateTime.contains("T")) {
                            String[] parts = dateTime.split("T");
                            String date = parts[0]; // yyyy-MM-dd
                            String time = parts[1];

                            if (time.contains("-"))
                                time = time.substring(0, 8);
                            if (time.contains("+"))
                                time = time.substring(0, 8);

                            textDateEnd.setText(date);
                            textTimeEnd.setText(time);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        queue.add(req);
    }

    private void completeScheduling(long id) {
        String url = URL_COMPLETE + id;

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        // corpo vazio
        JSONObject body = new JSONObject();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, body,
                response -> {
                    Toast.makeText(this, "Serviço finalizado!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Erro ao finalizar.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        queue.add(req);
    }
}
