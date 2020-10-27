package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServiciosRegistrarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_registrar_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Nuevo registro");

        agregarTextoSpinner();
        agregarBtnAccion();
    }
    void agregarTextoSpinner(){
        Spinner categoria = (Spinner)findViewById(R.id.input_categoriaServicio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias_analisis, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);
    }
    void agregarBtnAccion(){
        Button btnRegistrar = findViewById(R.id.btnRegistrarServicio);
        btnRegistrar.setOnClickListener(guardar);
        Button btnCancelar = findViewById(R.id.btnCancelarServicio);
        btnCancelar.setOnClickListener(cancelar);
    }

    //ActionListener
    View.OnClickListener guardar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            final EditText nombre = findViewById(R.id.input_nombreServicio);
            final Spinner categoria = findViewById(R.id.input_categoriaServicio);
            final EditText costo = findViewById(R.id.input_costoServicio);
            final EditText descripcion = findViewById(R.id.input_descripcionServicio);
            final EditText indicaciones = findViewById(R.id.input_indicacionesServicio);
            if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", nombre.getText().toString())){
                if(Pattern.matches("^([A-ZÁ-Úa-zá-ú0-9;,\\.:]+\\s{0,1}[A-ZÁ-Úa-zá-ú0-9,\\.\\s]+)+$", descripcion.getText().toString())){
                    if(Pattern.matches("^([A-ZÁ-Úa-zá-ú0-9;,\\.:]+\\s{0,1}[A-ZÁ-Úa-zá-ú0-9,\\.\\s]+)+$", indicaciones.getText().toString())){
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("analisis").whereEqualTo("nombre",nombre.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.getResult().isEmpty()){
                                    Map<String, Object> nvoServicio = new HashMap<>();
                                    nvoServicio.put("nombre", nombre.getText().toString());
                                    nvoServicio.put("categoria", categoria.getSelectedItem().toString());
                                    nvoServicio.put("costo", costo.getText().toString());
                                    nvoServicio.put("descripcion", descripcion.getText().toString());
                                    nvoServicio.put("indicaciones", indicaciones.getText().toString());
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("analisis").add(nvoServicio).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(innerView.getContext(), "Registro correcto", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(ServiciosRegistrarActivity.this, ServiciosAdminActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            ServiciosRegistrarActivity.this.startActivity(intent);
                                            }
                                    });
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
    View.OnClickListener cancelar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ServiciosRegistrarActivity.this, ServiciosAdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ServiciosRegistrarActivity.this.startActivity(intent);
        }
    };
}