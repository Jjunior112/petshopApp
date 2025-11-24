package com.littlebirds.petshopapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.littlebirds.petshopapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewSchedulingActivity extends BaseActivity {

    private Spinner spinnerWorker, spinnerPet, spinnerServiceType;
    private EditText editDate, editTime;
    private TextView textErrorScheduling;
    private Button buttonConfirmScheduling;

    private final ArrayList<String> workersList = new ArrayList<>();
    private final ArrayList<String> workersIdList = new ArrayList<>();

    private final ArrayList<String> petsList = new ArrayList<>();
    private final ArrayList<Long> petsIdList = new ArrayList<>();

    private final ArrayList<String> servicesList = new ArrayList<>();
    private final ArrayList<Long> servicesIdList = new ArrayList<>();

    private static final String SCHEDULING_URL = "http://10.0.2.2:8080/schedulings";
    private static final String WORKERS_URL = "http://10.0.2.2:8080/user/workers";
    private static final String PETS_URL = "http://10.0.2.2:8080/pets";
    private static final String SERVICES_URL = "http://10.0.2.2:8080/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_scheduling);

        // Barra inferior
        setupBottomNav();

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
        textErrorScheduling = findViewById(R.id.textErrorScheduling);
        buttonConfirmScheduling = findViewById(R.id.buttonConfirmScheduling);

        setupDateTimePickers();
        fetchWorkers();
        fetchPets();
        fetchServices();

        buttonConfirmScheduling.setOnClickListener(v -> handleCreateScheduling());
    }

    private void setupDateTimePickers() {
        Calendar calendar = Calendar.getInstance();

        editDate.setOnClickListener(v -> {
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        month += 1;
                        editDate.setText(String.format("%04d-%02d-%02d", year, month, day));
                    },
                    y, m, d);

            dialog.show();
        });

        editTime.setOnClickListener(v -> {
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hour, minute) ->
                            editTime.setText(String.format("%02d:%02d:00", hour, minute)),
                    h, min, true);

            dialog.show();
        });
    }

    private void fetchWorkers() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                WORKERS_URL,
                null,
                response -> {
                    try {
                        workersList.clear();
                        workersIdList.clear();

                        JSONArray content = response.getJSONArray("content");
                        for (int i = 0; i < content.length(); i++) {
                            JSONObject obj = content.getJSONObject(i);
                            workersIdList.add(obj.getString("id"));
                            workersList.add(obj.getString("fullName"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, workersList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerWorker.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error ->
                        Toast.makeText(this, "Erro ao carregar funcionários.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        queue.add(request);
    }

    private void fetchPets() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                PETS_URL,
                null,
                response -> {
                    try {
                        petsList.clear();
                        petsIdList.clear();

                        JSONArray content = response.getJSONArray("content");
                        for (int i = 0; i < content.length(); i++) {
                            JSONObject obj = content.getJSONObject(i);
                            petsIdList.add(obj.getLong("id"));
                            petsList.add(obj.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, petsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPet.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error ->
                        Toast.makeText(this, "Erro ao carregar pets.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
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
                    try {
                        servicesList.clear();
                        servicesIdList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            servicesIdList.add(obj.getLong("id"));
                            servicesList.add(obj.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, servicesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerServiceType.setAdapter(adapter);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Erro ao processar serviços.", Toast.LENGTH_SHORT).show();
                    }
                },
                error ->
                        Toast.makeText(this, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        queue.add(request);
    }

    private void handleCreateScheduling() {
        textErrorScheduling.setVisibility(View.GONE);

        int wi = spinnerWorker.getSelectedItemPosition();
        int pi = spinnerPet.getSelectedItemPosition();
        int si = spinnerServiceType.getSelectedItemPosition();

        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();

        if (wi < 0 || pi < 0 || si < 0 || date.isEmpty() || time.isEmpty()) {
            textErrorScheduling.setText("Preencha todos os campos.");
            textErrorScheduling.setVisibility(View.VISIBLE);
            return;
        }

        String workerId = workersIdList.get(wi);
        long petId = petsIdList.get(pi);
        long serviceId = servicesIdList.get(si);
        String dateTime = date + "T" + time + "-03:00";

        createScheduling(workerId, petId, serviceId, dateTime);
    }

    private void createScheduling(String workerId, Long petId, Long serviceId, String date) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject body = new JSONObject();
        try {
            body.put("workerId", workerId);
            body.put("petId", petId);
            body.put("serviceId", serviceId);
            body.put("date", date);
        } catch (Exception ignored) {}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SCHEDULING_URL,
                body,
                response -> {
                    Toast.makeText(this, "Agendamento criado!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String message = "Erro ao criar agendamento.";
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String res = new String(error.networkResponse.data, "UTF-8");
                            JSONObject obj = new JSONObject(res);
                            message = obj.optString("message", message);
                        }
                    } catch (Exception ignored) {}

                    textErrorScheduling.setText(message);
                    textErrorScheduling.setVisibility(View.VISIBLE);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        queue.add(request);
    }
}
