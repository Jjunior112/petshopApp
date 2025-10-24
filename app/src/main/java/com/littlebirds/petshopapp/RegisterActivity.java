package com.littlebirds.petshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private Button buttonLoginRedirect;

    private Button buttonRegister;

    private EditText fullName,editTextTextEmailAddressRegister,editTextTextPasswordRegister , editTextPhoneRegister;

    private String REGISTER_URL = "http://10.0.2.2:8080/user/register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonRegister = findViewById(R.id.buttonRegister);

        fullName = findViewById(R.id.fullNameRegister);
        editTextTextEmailAddressRegister = findViewById(R.id.editTextTextEmailAddressRegister);
        editTextTextPasswordRegister = findViewById(R.id.editTextTextPasswordRegister);
        editTextPhoneRegister = findViewById(R.id.editTextPhoneRegister);

        buttonRegister.setOnClickListener(v -> registerUser());


        buttonLoginRedirect = findViewById(R.id.buttonLoginRedirect);

        buttonLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String name = fullName.getText().toString().trim();
        String email = editTextTextEmailAddressRegister.getText().toString().trim();
        String password = editTextTextPasswordRegister.getText().toString().trim();
        String phone = editTextPhoneRegister.getText().toString().trim();

        if (name.isEmpty() ||email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11 ) {
            Toast.makeText(this, "Telefone inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("fullName",name);
            requestBody.put("email",email);
            requestBody.put("password",password);
            requestBody.put("phone",phone);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    REGISTER_URL,
                    requestBody,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Exemplo: resposta com token JWT
                                String emailResponse = response.getString("email");
                                Toast.makeText(RegisterActivity.this, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                                // e redirecionar o usuário:

                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.putExtra("EMAIL", emailResponse);
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RegisterActivity.this, "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, "Erro ao fazer registro: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }
}
