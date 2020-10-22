package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InicioSesionActivity extends AppCompatActivity {
    FirebaseFirestore db;
    static DocumentReference usuarioLogeado;
    static String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion_activity);
        db = FirebaseFirestore.getInstance();
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
                                        usuarioLogeado = document.getReference();
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
}