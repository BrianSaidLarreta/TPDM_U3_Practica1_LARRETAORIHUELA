package com.example.tpdm_u3_practica1_larretaorihuela;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button celulares, hospitales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celulares  = findViewById(R.id.btnC);
        hospitales = findViewById(R.id.btnH);

        celulares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pacientes = new Intent(v.getContext(), MPacientes.class);
                startActivity(pacientes);
            }
        });
        hospitales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hospitales = new Intent(v.getContext(), MHospitales.class);
                startActivity(hospitales);
            }
        });

    }
}
