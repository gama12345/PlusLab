package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ServiciosDetallesActivity extends AppCompatActivity {
    String servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_detalles_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Detalles del registro");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        servicio = (intent.getStringExtra("nombreServicio"));
        agregarBtnAccion();
        mostrarDatos(servicio);
    }


    void agregarBtnAccion(){
        Button regresarBtn = findViewById(R.id.btnRegresarDetalles);
        regresarBtn.setOnClickListener(regresar);
    }

    void mostrarDatos(String nombreServicio){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("analisis").whereEqualTo("nombre",nombreServicio).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                TextView nombre = findViewById(R.id.textView_nombreServicio);
                TextView categoria = findViewById(R.id.textView_categoriaServicio);
                TextView costo = findViewById(R.id.textView_costoServicio);
                TextView descripcion = findViewById(R.id.textView_descripcionServicio);
                TextView indicaciones = findViewById(R.id.textView_indicacionesServicio);
                for (QueryDocumentSnapshot servicio : task.getResult()) {
                    nombre.setText(servicio.get("nombre").toString());
                    categoria.setText(servicio.get("categoria").toString());
                    costo.setText(servicio.get("costo").toString()+" MXN");
                    descripcion.setText(servicio.get("descripcion").toString());
                    indicaciones.setText(servicio.get("indicaciones").toString());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ServiciosDetallesActivity.this, ServiciosPacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener regresar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ServiciosDetallesActivity.this, ServiciosPacienteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ServiciosDetallesActivity.this.startActivity(intent);
        }
    };
}