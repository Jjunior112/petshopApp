package com.littlebirds.petshopapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.littlebirds.petshopapp.R;


public class ListAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

       // btnServices.setOnClickListener(v ->
        //        startActivity(new Intent(this, ServicesActivity.class))
       // );

        //btnPromotions.setOnClickListener(v ->
        //        startActivity(new Intent(this, PromotionsActivity.class))
        //);
    }
}
