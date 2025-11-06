package com.littlebirds.petshopapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewSchedulingActivity extends AppCompatActivity {

    private Spinner spinnerWorker, spinnerPet, spinnerServiceType;
    private EditText editDate, editTime;
    private Button buttonConfirmScheduling;

    private ArrayList<String> workersList = new ArrayList<>();
    private ArrayList<String> workersIdList = new ArrayList<>();

    private ArrayList<String> petsList = new ArrayList<>();
    private ArrayList<Long> petsIdList = new ArrayList<>();

    private static final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings";
    private static final String WORKERS_URL = "http://10.0.2.2:8080/user/workers";
    private static final String PETS_URL = "http://10.0.2.2:8080/pets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_scheduling);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.newScheduling), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerWorker = findViewById(R.id.spinner_worker);
        spinnerPet = findViewById(R.id.spinner_pet);
        spinnerServiceType = findViewById(R.id.spinner_service_type);
        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        buttonConfirmScheduling = findViewById(R.id.buttonConfirmScheduling);

        setupServiceTypeSpinner();
        setupDateTimePickers();
        fetchWorkers();
        fetchPets();

        buttonConfirmScheduling.setOnClickListener(v -> handleCreateScheduling());
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
                        String date = String.format("%04d-%02d-%02d", y, m, d);
                        editDate.setText(date);
                    }, year, month, day);
            datePicker.show();
        });

        editTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, h, m) -> {
                        String time = String.format("%02d:%02d:00", h, m);
                        editTime.setText(time);
                    }, hour, minute, true);
            timePicker.show();
        });
    }

    private void handleCreateScheduling() {
        int workerIndex = spinnerWorker.getSelectedItemPosition();
        int petIndex = spinnerPet.getSelectedItemPosition();
        String serviceType = spinnerServiceType.getSelectedItem().toString();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();

        if (workerIndex < 0 || petIndex < 0 || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String workerId = workersIdList.get(workerIndex);
        Long petId = petsIdList.get(petIndex);
        String dateTime = date + "T" + time + "Z";

        createScheduling(workerId, petId, serviceType, dateTime);
    }

    private void fetchWorkers() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, WORKERS_URL, null,
                response -> {
                    workersList.clear();
                    workersIdList.clear();
                    try {
                        JSONArray content = response.getJSONArray("content");
                        for (int i = 0; i < content.length(); i++) {
                            JSONObject obj = content.getJSONObject(i);
                            workersIdList.add(obj.getString("id"));
                            workersList.add(obj.getString("fullName"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workersList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerWorker.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar funcionários.", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    private void fetchPets() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PETS_URL, null,
                response -> {
                    petsList.clear();
                    petsIdList.clear();
                    try {
                        JSONArray content = response.getJSONArray("content");
                        for (int i = 0; i < content.length(); i++) {
                            JSONObject obj = content.getJSONObject(i);
                            petsIdList.add(obj.getLong("id"));
                            petsList.add(obj.getString("name"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, petsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPet.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar pets.", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }

    private void createScheduling(String workerId, Long petId, String serviceType, String date) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("workerId", workerId);
            jsonBody.put("petId", petId);
            jsonBody.put("serviceType", serviceType.replace(" ", "_"));
            jsonBody.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SCHEDULING_URL, jsonBody,
                response -> Toast.makeText(this, "Agendamento criado com sucesso!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Erro ao criar agendamento.", Toast.LENGTH_SHORT).show()) {
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
