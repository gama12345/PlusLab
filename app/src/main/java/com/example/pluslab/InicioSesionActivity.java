package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pluslab.RestAPI.Adaptador.AdapterRestAPI;
import com.example.pluslab.RestAPI.Endpoints;
import com.example.pluslab.RestAPI.Modelo.DatosUsuarioRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class InicioSesionActivity extends AppCompatActivity {
    FirebaseFirestore db;
    static DocumentReference usuarioLogeado;
    static String tipoUsuario;
    static Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion_activity);
        db = FirebaseFirestore.getInstance();
        myContext = this;
        agregarBtnAccion();
    }
    void agregarBtnAccion(){
        Button botonLogin = findViewById(R.id.boton_login);
        botonLogin.setOnClickListener(entrar);
    }

    //ActionListeners
    View.OnClickListener entrar = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            final View innerView = view;
            final EditText email = findViewById(R.id.email);
            final EditText password = findViewById(R.id.contraseña);

            if(!email.getText().toString().trim().equals("")) {
                if (!password.getText().toString().trim().equals("")) {
                    db.collection("pacientes").whereEqualTo("correo_electronico",email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().isEmpty()){
                                FirebaseFirestore.getInstance().collection("administrador").whereEqualTo("correo_electronico",email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().isEmpty()) {
                                            Snackbar.make(innerView, "Email o contraseña incorrectos", Snackbar.LENGTH_LONG)
                                                    .setAction("Mensaje de error", null).show();
                                        }else{
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.get("contraseña").equals(password.getText().toString())) {
                                                    registraToken(myContext, document.get("correo_electronico").toString());
                                                    usuarioLogeado = document.getReference();
                                                    tipoUsuario = "Administrador";
                                                    HelperSQLite helper = new HelperSQLite(InicioSesionActivity.this,"SQLite", null, 1);
                                                    SQLiteDatabase bd = helper.getWritableDatabase();
                                                    ContentValues registro = new ContentValues();
                                                    registro.put("correo_electronico", document.get("correo_electronico").toString().toString());
                                                    bd.insert("usuario", null, registro);
                                                    bd.close();
                                                    Intent intent = new Intent(InicioSesionActivity.this, MenuPrincipal.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    InicioSesionActivity.this.startActivity(intent);
                                                } else {
                                                    Snackbar.make(innerView, "Email o contraseña incorrectos", Snackbar.LENGTH_LONG)
                                                            .setAction("Mensaje de error", null).show();
                                                }
                                            }
                                        }
                                    }
                                });
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("contraseña").equals(password.getText().toString())) {
                                        registraToken(myContext, document.get("correo_electronico").toString());
                                        usuarioLogeado = document.getReference();
                                        MainActivity.emailUsuario = document.get("correo_electronico").toString();
                                        tipoUsuario = "Paciente";
                                        HelperSQLite helper = new HelperSQLite(InicioSesionActivity.this,"SQLite", null, 1);
                                        SQLiteDatabase bd = helper.getWritableDatabase();
                                        ContentValues registro = new ContentValues();
                                        registro.put("correo_electronico", document.get("correo_electronico").toString().toString());
                                        bd.insert("usuario", null, registro);
                                        bd.close();
                                        Intent intent = new Intent(InicioSesionActivity.this, MenuPrincipal.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        InicioSesionActivity.this.startActivity(intent);
                                    } else {
                                        Snackbar.make(innerView, "Email o contraseña incorrectos", Snackbar.LENGTH_LONG)
                                                .setAction("Mensaje de error", null).show();
                                    }
                                }
                            }
                        }
                    });
                }else{
                    Snackbar.make(view, "Se requiere contraseña", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje de error", null).show();
                }
            }else{
                Snackbar.make(view, "Se requiere email", Snackbar.LENGTH_LONG)
                        .setAction("Mensaje de error", null).show();
            }
        }
    };

    public static void registraToken(Context currentActivity, String email){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                final String miToken = task.getResult().toString();
                Toast.makeText(currentActivity, "Toke: "+miToken, Toast.LENGTH_LONG).show();
                FirebaseFirestore.getInstance().collection("tokens").whereEqualTo("token", task.getResult()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()){
                            task.getResult().getDocuments().get(0).getReference().delete();
                        }
                        AdapterRestAPI adapterRestAPI = new AdapterRestAPI();
                        Endpoints endpoints = adapterRestAPI.establecerConexionRestAPI();
                        Call<DatosUsuarioRequest> respuestaCall = endpoints.registrarTokenID(miToken, email);
                        respuestaCall.enqueue(new Callback<DatosUsuarioRequest>() {
                            @Override
                            public void onResponse(Call<DatosUsuarioRequest> call, Response<DatosUsuarioRequest> response) {
                                DatosUsuarioRequest respuesta = response.body();
                            }

                            @Override
                            public void onFailure(Call<DatosUsuarioRequest> call, Throwable t) {

                            }
                        });
                    }
                });
            }
        });
    }
}