package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    static DocumentReference pacienteLogeado;

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
        public void onClick(View view) {
            final View innerView = view;
            EditText email = findViewById(R.id.email);
            final EditText password = findViewById(R.id.contraseña);

            if(!email.getText().toString().trim().equals("")) {
                if (!password.getText().toString().trim().equals("")) {
                    db.collection("pacientes").whereEqualTo("correo_electronico",email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().isEmpty()){
                                Snackbar.make(innerView, "Email o contraseña incorrectos", Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje de error", null).show();
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("contraseña").equals(password.getText().toString())) {
                                        pacienteLogeado = document.getReference();
                                        Snackbar.make(innerView, "Hola " + document.get("nombre"), Snackbar.LENGTH_SHORT)
                                                .setAction("Autenticado", null).show();
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