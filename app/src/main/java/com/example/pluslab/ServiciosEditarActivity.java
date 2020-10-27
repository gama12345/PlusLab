package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServiciosEditarActivity extends AppCompatActivity {
    String servicio, oldName;

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
        servicio = (intent.getStringExtra("nombreServicio"));
        agregarTextoSpinner();
        agregarBtnAccion();
        mostrarDatos(servicio);
    }
    void agregarTextoSpinner(){
        Spinner categoria = (Spinner)findViewById(R.id.input_editar_categoriaServicio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias_analisis, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);
    }
    void agregarBtnAccion(){
        Button guardar = findViewById(R.id.btnGuardarEditarServicio);
        guardar.setOnClickListener(guardarCambios);
        Button eliminar = findViewById(R.id.btnEliminarServicio);
        eliminar.setOnClickListener(eliminarServicio);
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
                    oldName = servicio.get("nombre").toString();
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

    //ActionListeners
    View.OnClickListener guardarCambios = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            final EditText nombre = findViewById(R.id.input_editar_nombreServicio);
            final Spinner categoria = findViewById(R.id.input_editar_categoriaServicio);
            final EditText costo = findViewById(R.id.input_editar_costoServicio);
            final EditText descripcion = findViewById(R.id.input_editar_descripcionServicio);
            final EditText indicaciones = findViewById(R.id.input_editar_indicacionesServicio);
            if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", nombre.getText().toString())) {
                if (Pattern.matches("^([A-ZÁ-Úa-zá-ú0-9;,\\.:]+\\s{0,1}[A-ZÁ-Úa-zá-ú0-9,\\.\\s]+)+$", descripcion.getText().toString())) {
                    if (Pattern.matches("^([A-ZÁ-Úa-zá-ú0-9;,\\.:]+\\s{0,1}[A-ZÁ-Úa-zá-ú0-9,\\.\\s]+)+$", indicaciones.getText().toString())) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("analisis").whereEqualTo("nombre",nombre.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.getResult().isEmpty() || (oldName.equals(nombre.getText().toString()))){
                                    Map<String, Object> servicio = new HashMap<>();
                                    servicio.put("nombre",nombre.getText().toString());
                                    servicio.put("categoria",categoria.getSelectedItem().toString());
                                    servicio.put("costo",costo.getText().toString());
                                    servicio.put("descripcion",descripcion.getText().toString());
                                    servicio.put("indicaciones",indicaciones.getText().toString());
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().update(servicio);
                                    }

                                    Toast.makeText(innerView.getContext(), "Se ha actualizado el registro", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ServiciosEditarActivity.this, ServiciosAdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ServiciosEditarActivity.this.startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Ya se ha registrado un servicio con este nombre", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                            }else{
                        Snackbar.make(view, "Indicaciones incorrectas, use sólo letras, números, puntos y comas", Snackbar.LENGTH_LONG)
                                .setAction("Mensaje de error", null).show();
                    }
                }else{
                    Snackbar.make(view, "Descripción incorrecta, use sólo letras, números, puntos y comas", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje de error", null).show();
                }
            }else{
                Snackbar.make(view, "Nombre incorrecto, use sólo letras", Snackbar.LENGTH_LONG)
                        .setAction("Mensaje de error", null).show();
            }

        }
    };
    View.OnClickListener eliminarServicio = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Está seguro de eliminar este registro?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("analisis").whereEqualTo("nombre", servicio).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                    Toast.makeText(innerView.getContext(), "Se ha eliminado el registro", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ServiciosEditarActivity.this, ServiciosAdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ServiciosEditarActivity.this.startActivity(intent);
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object
            builder.create().show();
        }
    };
}