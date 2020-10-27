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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ServiciosPacienteActivity extends AppCompatActivity {
    static Activity currentActivity;
    static ArrayList<Servicio> servicios;
    static RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_paciente_activity);

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Análisis clínicos");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.servicios_recyclerView_paciente);
        recyclerView.setLayoutManager(lim);
        refreshLayout = findViewById(R.id.swipeRefreshLayoutPaciente);
        refreshLayout.setOnRefreshListener(refreshAction);
        leerDatosServicios();
    }

    void leerDatosServicios(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("analisis").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                servicios = new ArrayList<Servicio>();
                for (QueryDocumentSnapshot servicio : task.getResult()) {
                    servicios.add(new Servicio(servicio.get("nombre").toString(),
                    servicio.get("categoria").toString(),
                    servicio.get("descripcion").toString(),
                    servicio.get("indicaciones").toString(),
                    servicio.get("costo").toString()));
                }
                ServiciosAdaptador adaptador = new ServiciosAdaptador(servicios, currentActivity);
                recyclerView.setAdapter(adaptador);
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener refreshAction = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            leerDatosServicios();
            refreshLayout.setRefreshing(false);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ServiciosPacienteActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}