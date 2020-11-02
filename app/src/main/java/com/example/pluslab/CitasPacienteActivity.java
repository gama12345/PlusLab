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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CitasPacienteActivity extends AppCompatActivity {
        static Activity currentActivity;
        static ArrayList<Cita> citas;
        static RecyclerView recyclerView;
        SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citas_paciente_activity);

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Mis citas");
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
        configurarBtnFlotante();
    }

    void leerDatosCitas(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("citas").orderBy("prioridad").whereEqualTo("paciente", MainActivity.emailUsuario).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                Intent intent = new Intent(CitasPacienteActivity.this, CitasRegistrarActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                CitasPacienteActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CitasPacienteActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}