package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    static String emailUsuario = "";
    static Context currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        currentActivity = this;
        //Si un usuario esta logeado ya no pedimos sus datos
        HelperSQLite helper = new HelperSQLite(MainActivity.this,"SQLite", null, 1);
        SQLiteDatabase bd = helper.getWritableDatabase();
        String[] email = {"correo_electronico"};
        Cursor consulta = bd.query("usuario",email,
                null,null,null,null,null);

        actualizarEdoCitas();
        if(!consulta.moveToFirst()) {
            agregarBtnAccion();
        }else{
            emailUsuario = consulta.getString(0);
            //Si es admin
            FirebaseFirestore.getInstance().collection("administrador").whereEqualTo("correo_electronico",consulta.getString(0)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().isEmpty()) {
                        //Si es paciente
                        FirebaseFirestore.getInstance().collection("pacientes").whereEqualTo("correo_electronico", emailUsuario).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    InicioSesionActivity.usuarioLogeado = document.getReference();
                                    InicioSesionActivity.tipoUsuario = "Paciente";
                                    Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    MainActivity.this.startActivity(intent);
                                }
                                InicioSesionActivity.registraToken(currentActivity, task.getResult().getDocuments().get(0).get("correo_electronico").toString());
                            }
                        });
                    }else{
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            InicioSesionActivity.usuarioLogeado = document.getReference();
                            InicioSesionActivity.tipoUsuario = "Administrador";
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            MainActivity.this.startActivity(intent);
                        }
                        InicioSesionActivity.registraToken(currentActivity, task.getResult().getDocuments().get(0).get("correo_electronico").toString());
                    }
                }
            });
        }
    }
    void agregarBtnAccion(){
        Button btnLogin = findViewById(R.id.btn_iniciarSesion);
        Button btnRegistro = findViewById(R.id.btn_registrarse);
        btnLogin.setOnClickListener(login);
        btnRegistro.setOnClickListener(registro);
    }

    void actualizarEdoCitas(){
        final SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        final Calendar calendario = Calendar.getInstance();
        calendario.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        Date today = null;
        try {
            today = formato.parse(formato.format(calendario.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("citas").whereEqualTo("fecha", formato.format(calendario.getTime())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().update("estado", "próxima");
                    document.getReference().update("prioridad", "1");
                }
                FirebaseFirestore.getInstance().collection("citas").whereLessThan("fecha", formato.format(calendario.getTime())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().update("estado", "no atendida");
                            document.getReference().update("prioridad", "6");
                        }
                    }
                });
            }
        });
    }

    //ActionListeners
    View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.startActivity(intent);
        }
    };
    View.OnClickListener registro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, RegistrarPacienteActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.startActivity(intent);
        }
    };
}
