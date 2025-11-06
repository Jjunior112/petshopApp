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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditSchedulingActivity extends AppCompatActivity {

    private TextView textPetName; // Somente leitura
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
            String serviceType = spinnerServiceType.getSelectedItem().toString();
            String date = editDate.getText().toString().trim();
            String time = editTime.getText().toString().trim();

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Preencha data e hora.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Converter de dd/MM/yyyy para formato ISO-8601
            String isoDate = convertToIsoFormat(date, time);
            if (isoDate == null) {
                Toast.makeText(this, "Data inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            editScheduling(schedulingId, serviceType, isoDate);
        });
    }

    private void setupServiceTypeSpinner() {
        String[] services = {"BANHO E TOSA", "BANHO", "TOSA", "TOSA HIGIENICA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        Calendar calendar = Calendar.getInstance();

        editDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, y, m, d) -> {
                        m += 1;
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m, y);
                        editDate.setText(date);
                    }, year, month, day);
            datePicker.show();
        });

        editTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, h, m) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m);
                        editTime.setText(time);
                    }, hour, minute, true);
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
                        String serviceType = response.getString("serviceType").replace("_", " ");
                        String dateTime = response.getString("date");

                        textPetName.setText(petName);
                        spinnerServiceType.setSelection(getIndex(spinnerServiceType, serviceType));

                        if (dateTime.contains("T")) {
                            String[] parts = dateTime.split("T");
                            String datePart = parts[0]; // yyyy-MM-dd
                            String timePart = parts[1].replace("Z", "");

                            // ✅ Converter data para formato dd/MM/yyyy
                            String formattedDate = formatDateToBrazilian(datePart);

                            editDate.setText(formattedDate);
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

    private void editScheduling(Long id, String serviceType, String date) {
        String url = SCHEDULING_URL + id;
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("serviceType", serviceType.replace(" ", "_"));
            jsonBody.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> Toast.makeText(this, "Agendamento atualizado com sucesso!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Erro ao atualizar agendamento.", Toast.LENGTH_SHORT).show()) {

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

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value))
                return i;
        }
        return 0;
    }

    // 🗓️ Converter data ISO (yyyy-MM-dd) → dd/MM/yyyy
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

    // 🔁 Converter dd/MM/yyyy + hora → formato ISO (yyyy-MM-dd'T'HH:mm:ss'Z')
    private String convertToIsoFormat(String brazilianDate, String time) {
        try {
            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            return isoFormat.format(brFormat.parse(brazilianDate)) + ""; // já inclui Z
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
