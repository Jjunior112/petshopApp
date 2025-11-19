package com.littlebirds.petshopapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewSchedulingActivity extends AppCompatActivity {

    private Spinner spinnerWorker, spinnerPet, spinnerServiceType;
    private EditText editDate, editTime;

    private ImageButton buttonInicio, buttonAgendar, buttonPets, buttonAgendamentos, buttonPerfil;
    private Button buttonConfirmScheduling;

    private ArrayList<String> workersList = new ArrayList<>();
    private ArrayList<String> workersIdList = new ArrayList<>();

    private ArrayList<String> petsList = new ArrayList<>();
    private ArrayList<Long> petsIdList = new ArrayList<>();

    private ArrayList<String> servicesList = new ArrayList<>();
    private ArrayList<Long> servicesIdList = new ArrayList<>();

    private static final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings";
    private static final String WORKERS_URL = "http://10.0.2.2:8080/user/workers";
    private static final String PETS_URL = "http://10.0.2.2:8080/pets";
    private static final String SERVICES_URL = "http://10.0.2.2:8080/services";

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
        buttonInicio = findViewById(R.id.buttonInicio);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonPets = findViewById(R.id.buttonPets);
        buttonAgendamentos = findViewById(R.id.buttonAgendamentos);
        buttonPerfil = findViewById(R.id.buttonPerfil);

        buttonAgendar.setOnClickListener(v -> {
            // opcional: apenas fechar menu ou atualizar UI
            Toast.makeText(this, "Você já está em Agendar", Toast.LENGTH_SHORT).show();
        });

        buttonInicio.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));

        buttonPets.setOnClickListener(v -> startActivity(new Intent(this, PetsActivity.class)));

        buttonAgendamentos.setOnClickListener(v -> startActivity(new Intent(this, SchedulingActivity.class)));

        buttonPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        setupDateTimePickers();
        fetchWorkers();
        fetchPets();
        fetchServices();

        buttonConfirmScheduling.setOnClickListener(v -> handleCreateScheduling());
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

    private void fetchServices() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                SERVICES_URL,
                null,
                response -> {
                    servicesList.clear();
                    servicesIdList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            servicesIdList.add(obj.getLong("id"));
                            servicesList.add(obj.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                servicesList
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerServiceType.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Erro ao processar serviços.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()
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

    private void handleCreateScheduling() {
        int workerIndex = spinnerWorker.getSelectedItemPosition();
        int petIndex = spinnerPet.getSelectedItemPosition();
        int serviceIndex = spinnerServiceType.getSelectedItemPosition();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();

        if (workerIndex < 0 || petIndex < 0 || serviceIndex < 0 || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String workerId = workersIdList.get(workerIndex);
        Long petId = petsIdList.get(petIndex);
        Long serviceId = servicesIdList.get(serviceIndex);
        String dateTime = date + "T" + time + "-03:00";

        createScheduling(workerId, petId, serviceId, dateTime);
    }

    private void createScheduling(String workerId, Long petId, Long serviceId, String date) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("workerId", workerId);
            jsonBody.put("petId", petId);
            jsonBody.put("serviceId", serviceId);
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
