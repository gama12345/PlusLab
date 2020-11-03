package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CitasActivity extends AppCompatActivity {
        static Activity currentActivity;
        static ArrayList<Cita> citas;
        static RecyclerView recyclerView;
        SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(InicioSesionActivity.tipoUsuario.equals("Paciente")) {
            setContentView(R.layout.citas_paciente_activity);
        }else{
            setContentView(R.layout.citas_admin_activity);
        }

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        if(InicioSesionActivity.tipoUsuario.equals("Paciente")) {
            getSupportActionBar().setTitle("Mis citas");
            configurarBtnFlotante();
        }else{
            getSupportActionBar().setTitle("Citas registradas");
        }
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.citas_recyclerViewPaciente);
        recyclerView.setLayoutManager(lim);
        refreshLayout = findViewById(R.id.swipeRefreshLayoutCitas);
        refreshLayout.setOnRefreshListener(refreshAction);
        leerDatosCitas();
    }

    void leerDatosCitas(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(InicioSesionActivity.tipoUsuario.equals("Paciente")) {
            db.collection("citas").orderBy("prioridad").orderBy("fecha").whereEqualTo("paciente", MainActivity.emailUsuario).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    citas = new ArrayList<Cita>();
                    for (QueryDocumentSnapshot cita : task.getResult()) {
                        citas.add(new Cita(cita.get("paciente").toString(),
                                cita.get("servicio").toString(),
                                cita.get("fecha").toString(),
                                cita.get("hora").toString(),
                                cita.get("acompañante").toString(),
                                cita.get("estado").toString(),
                                cita.get("prioridad").toString()));
                    }
                    CitasAdaptador adaptador = new CitasAdaptador(citas, currentActivity);
                    recyclerView.setAdapter(adaptador);
                }
            });
        }else{
            db.collection("citas").orderBy("prioridad").orderBy("fecha").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    citas = new ArrayList<Cita>();
                    for (QueryDocumentSnapshot cita : task.getResult()) {
                        citas.add(new Cita(cita.get("paciente").toString(),
                                cita.get("servicio").toString(),
                                cita.get("fecha").toString(),
                                cita.get("hora").toString(),
                                cita.get("acompañante").toString(),
                                cita.get("estado").toString(),
                                cita.get("prioridad").toString()));
                    }
                    CitasAdaptador adaptador = new CitasAdaptador(citas, currentActivity);
                    recyclerView.setAdapter(adaptador);
                }
            });
        }
    }

    SwipeRefreshLayout.OnRefreshListener refreshAction = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            leerDatosCitas();
            refreshLayout.setRefreshing(false);
        }
    };

    void configurarBtnFlotante(){
        FloatingActionButton nuevo = findViewById(R.id.btnAgregarCita);
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CitasActivity.this, CitasRegistrarActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                CitasActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CitasActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}