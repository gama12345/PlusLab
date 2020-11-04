package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pluslab.RestAPI.Adaptador.AdapterRestAPI;
import com.example.pluslab.RestAPI.Endpoints;
import com.example.pluslab.RestAPI.Modelo.DatosUsuarioRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CitasDetallesActivity extends AppCompatActivity {
    String pacienteCita, servicioCita, horaCita, fechaCita, acompañanteCita, estadoCita;
    Activity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(InicioSesionActivity.tipoUsuario.equals("Paciente")) {
            setContentView(R.layout.citas_detalles_paciente_activity);
        }else{
            setContentView(R.layout.citas_detalles_admin_activity);
            agregarBtnAccion();
        }

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Detalles de cita");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        pacienteCita = (intent.getStringExtra("pacienteCita"));
        servicioCita = (intent.getStringExtra("servicioCita"));
        fechaCita = (intent.getStringExtra("fechaCita"));
        horaCita = (intent.getStringExtra("horaCita"));
        acompañanteCita = (intent.getStringExtra("acompañanteCita"));
        estadoCita = (intent.getStringExtra("estadoCita"));

        prepararDatos();
    }

    void agregarBtnAccion(){
        Button btnGuardarEstado = findViewById(R.id.btnGuardarEstadoDetallesCita);
        btnGuardarEstado.setOnClickListener(guardar);
        Button btnRegresarEstado = findViewById(R.id.btnRegresarDetallesCita);
        btnRegresarEstado.setOnClickListener(regresar);
    }

    void prepararDatos(){
        FirebaseFirestore.getInstance().collection("pacientes").whereEqualTo("correo_electronico", pacienteCita).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                TextView pacienteCitaET = findViewById(R.id.datos_detallesPacienteCita);
                pacienteCitaET.setText(task.getResult().getDocuments().get(0).get("nombre")+" "+task.getResult().getDocuments().get(0).get("apellidos"));
                TextView servicioCitaET = findViewById(R.id.datos_detallesServicioCita);
                servicioCitaET.setText(servicioCita);
                TextView fechaCitaET = findViewById(R.id.datos_detallesFechaCita);
                fechaCitaET.setText(fechaCita);
                TextView horaCitaET = findViewById(R.id.datos_detallesHoraCita);
                horaCitaET.setText(horaCita);
                TextView acompañanteCitaET = findViewById(R.id.datos_detallesAcompañanteCita);
                acompañanteCitaET.setText(acompañanteCita);
                TextView estadoCitaET = findViewById(R.id.datos_detallesEstadoCita);
                estadoCitaET.setText(estadoCita);


                if(InicioSesionActivity.tipoUsuario.equals("Administrador")) {
                    ArrayList<String> spinnerArray = new ArrayList<String>();
                    if (!estadoCita.equals("atendida") && !estadoCita.equals("concluida")) {
                        spinnerArray.add(estadoCita);
                    }
                    spinnerArray.add("atendida");
                    spinnerArray.add("concluida");
                    Spinner estadosCita = findViewById(R.id.datos_detallesCambiarEstadoCita);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    estadosCita.setAdapter(adapter);
                }
            }
        });
    }
    
    View.OnClickListener guardar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            final Spinner estadosCita = findViewById(R.id.datos_detallesCambiarEstadoCita);
            FirebaseFirestore.getInstance().collection("citas").whereEqualTo("fecha", fechaCita).whereEqualTo("hora",horaCita).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (!task.getResult().isEmpty()){
                        task.getResult().getDocuments().get(0).getReference().update("estado", estadosCita.getSelectedItem().toString());
                        String prior = "1";
                        if(estadosCita.getSelectedItem().toString().equals("en agenda")){
                            prior = "2";
                        }else if(estadosCita.getSelectedItem().toString().equals("atendida")){
                            prior = "3";
                        }else if(estadosCita.getSelectedItem().toString().equals("concluida")){
                            prior = "5";
                            enviarNotificacion();
                        }
                        task.getResult().getDocuments().get(0).getReference().update("prioridad", prior);

                        Toast.makeText(innerView.getContext(), "Se ha actualizado el estado de la cita", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CitasDetallesActivity.this, CitasActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        CitasDetallesActivity.this.startActivity(intent);
                    }
                }
            });
        }
    };
    View.OnClickListener regresar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CitasDetallesActivity.this, CitasActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            CitasDetallesActivity.this.startActivity(intent);
        }
    };

    public void enviarNotificacion(){
        AdapterRestAPI adapterRestAPI = new AdapterRestAPI();
        Endpoints endpoints = adapterRestAPI.establecerConexionRestAPI();
        Call<DatosUsuarioRequest> usuarioResponseCall = endpoints.enviarNotificacion(InicioSesionActivity.tokenUsuario);
        usuarioResponseCall.enqueue(new Callback<DatosUsuarioRequest>() {
            @Override
            public void onResponse(Call<DatosUsuarioRequest> call, Response<DatosUsuarioRequest> response) {
                DatosUsuarioRequest respuesta = response.body();
            }

            @Override
            public void onFailure(Call<DatosUsuarioRequest> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CitasDetallesActivity.this, CitasActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}