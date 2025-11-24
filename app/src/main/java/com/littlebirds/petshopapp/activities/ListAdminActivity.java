package com.littlebirds.petshopapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;

import com.littlebirds.petshopapp.R;

public class ListAdminActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_list);

        setupBottomNav(); // habilita barra de navegação herdada

        Button btnClients = findViewById(R.id.btnClients);
        Button btnEmployees = findViewById(R.id.btnEmployees);
        Button btnSchedulings = findViewById(R.id.btnSchedulings);
        Button btnServices = findViewById(R.id.btnServices);
        Button btnPromotions = findViewById(R.id.btnPromotions);


        btnClients.setOnClickListener(v ->
                startActivity(new Intent(this, ClientsActivity.class))
        );

        //btnEmployees.setOnClickListener(v ->
        //        startActivity(new Intent(this, EmployeesActivity.class))
        //);

        //btnSchedulings.setOnClickListener(v ->
        //        startActivity(new Intent(this, SchedulingsActivity.class))
        //);

        //btnServices.setOnClickListener(v ->
        //        startActivity(new Intent(this, ServicesActivity.class))
        //);

        //btnPromotions.setOnClickListener(v ->
        //        startActivity(new Intent(this, PromotionsActivity.class))
        //);
    }
}
