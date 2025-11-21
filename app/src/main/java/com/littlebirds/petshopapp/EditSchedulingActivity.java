package com.littlebirds.petshopapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class EditSchedulingActivity extends AppCompatActivity {

    private TextView textPetName;
    private EditText editDate, editTime;
    private Spinner spinnerServiceType;
    private Button buttonConfirmEditScheduling;

    private final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_scheduling);

        long schedulingId = getIntent().getLongExtra("schedulingId", -1);
        if (schedulingId == -1) {
            Toast.makeText(this, "ID do agendamento inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editScheduling), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textPetName = findViewById(R.id.text_pet_name);
        spinnerServiceType = findViewById(R.id.spinner_service_type);
        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        buttonConfirmEditScheduling = findViewById(R.id.buttonConfirmEditScheduling);

        setupServiceTypeSpinner();
        setupDateTimePickers();
        fetchSchedulingById(schedulingId);

        buttonConfirmEditScheduling.setOnClickListener(v -> {
            String date = editDate.getText().toString().trim();
            String time = editTime.getText().toString().trim();

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Preencha data e hora.", Toast.LENGTH_SHORT).show();
                return;
            }

            String isoDate = convertToIsoFormat(date, time);
            if (isoDate == null) {
                Toast.makeText(this, "Data inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            editScheduling(schedulingId, isoDate);
        });
    }

    private void setupServiceTypeSpinner() {
        fetchServices();
    }

    private void fetchServices() {
        String url = "http://10.0.2.2:8080/services";
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ServiceItem> serviceItems = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Long id = obj.getLong("id");
                            String name = obj.getString("name");
                            serviceItems.add(new ServiceItem(id, name));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<ServiceItem> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, serviceItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerServiceType.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    private void setupDateTimePickers() {
        Calendar calendar = Calendar.getInstance();

        editDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, y, m, d) -> {
                        m += 1;
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m, y);
                        editDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        editTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, h, m) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m);
                        editTime.setText(time);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);

            timePicker.show();
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
                        String dateTime = response.getString("date");

                        textPetName.setText(petName);

                        if (dateTime.contains("T")) {
                            String[] parts = dateTime.split("T");
                            String datePart = parts[0];
                            String timePart = parts[1];

                            // Remove fuso
                            if (timePart.contains("-"))
                                timePart = timePart.substring(0, 8);
                            if (timePart.contains("+"))
                                timePart = timePart.substring(0, 8);

                            editDate.setText(formatDateToBrazilian(datePart));
                            editTime.setText(timePart);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void editScheduling(Long id, String isoDate) {
        String url = SCHEDULING_URL + id;
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        ServiceItem service = (ServiceItem) spinnerServiceType.getSelectedItem();
        if (service == null) {
            Toast.makeText(this, "Selecione um serviço.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject json = new JSONObject();
        try {
            json.put("serviceId", service.id);
            json.put("date", isoDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> Toast.makeText(this, "Agendamento atualizado!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Erro ao atualizar.", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(req);
    }

    // CONVERSÃO CORRETA — DATA + HORA + FUSO REAL (-03:00)
    private String convertToIsoFormat(String brazilianDate, String time) {
        try {
            // Entrada: 25/11/2025 e 14:30:00
            String[] dateParts = brazilianDate.split("/");
            String day = dateParts[0];
            String month = dateParts[1];
            String year = dateParts[2];

            // Monta no formato ISO sem conversão de fuso
            return year + "-" + month + "-" + day + "T" + time + "-03:00";

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDateToBrazilian(String isoDate) {
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat br = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return br.format(iso.parse(isoDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    // Classe interna do Spinner
    private static class ServiceItem {
        Long id;
        String name;

        ServiceItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
