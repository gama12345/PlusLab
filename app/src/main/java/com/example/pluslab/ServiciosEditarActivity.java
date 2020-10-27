package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ServiciosEditarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_editar_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Detalles del registro");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        String servicio = (intent.getStringExtra("nombreServicio"));
        agregarTextoSpinner();
        mostrarDatos(servicio);
    }
    void agregarTextoSpinner(){
        Spinner categoria = (Spinner)findViewById(R.id.input_editar_categoriaServicio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias_analisis, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);
    }

    void mostrarDatos(String nombreServicio){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("analisis").whereEqualTo("nombre",nombreServicio).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                EditText nombre = findViewById(R.id.input_editar_nombreServicio);
                Spinner categoria = findViewById(R.id.input_editar_categoriaServicio);
                EditText costo = findViewById(R.id.input_editar_costoServicio);
                EditText descripcion = findViewById(R.id.input_editar_descripcionServicio);
                EditText indicaciones = findViewById(R.id.input_editar_indicacionesServicio);
                for (QueryDocumentSnapshot servicio : task.getResult()) {
                    nombre.setText(servicio.get("nombre").toString());
                    int posicion = 0;
                    if(servicio.get("categoria").toString().equals("Bioquímica")){
                        posicion = 1;
                    }else if(servicio.get("categoria").toString().equals("Coagulación")){
                        posicion = 2;
                    }else if(servicio.get("categoria").toString().equals("Bacteriología")){
                        posicion = 3;
                    }else if(servicio.get("categoria").toString().equals("Inmunología")){
                        posicion = 4;
                    }else if(servicio.get("categoria").toString().equals("Endocrinología")){
                        posicion = 5;
                    }else if(servicio.get("categoria").toString().equals("Alergías")){
                        posicion = 6;
                    }else if(servicio.get("categoria").toString().equals("Toxicología")){
                        posicion = 7;
                    }else if(servicio.get("categoria").toString().equals("Heces")){
                        posicion = 8;
                    }
                    categoria.setSelection(posicion);
                    costo.setText(servicio.get("costo").toString());
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
                Intent intent = new Intent(ServiciosEditarActivity.this, ServiciosAdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}