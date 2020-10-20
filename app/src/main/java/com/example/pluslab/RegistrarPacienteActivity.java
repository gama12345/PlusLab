package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistrarPacienteActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_paciente_activity);
        db = FirebaseFirestore.getInstance();
        agregarBtnAccion();
    }
    void agregarBtnAccion(){
        Button btnGuardar = findViewById(R.id.botonRegistrar);
        Button btnRegresar = findViewById(R.id.botonCancelarREgistro);
        btnGuardar.setOnClickListener(guardar);
        btnRegresar.setOnClickListener(regresar);
    }

    //ActionListeners
    View.OnClickListener guardar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            final EditText nombre = findViewById(R.id.nombreRegistro);
            final EditText apellidos = findViewById(R.id.apellidosRegistro);
            final EditText celular = findViewById(R.id.celularRegistro);
            final EditText email = findViewById(R.id.emailRegistro);
            final EditText contraseña = findViewById(R.id.contraseñaRegistro);
            final EditText direccion = findViewById(R.id.direccionRegistro);
            if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", nombre.getText().toString())){
                if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", apellidos.getText().toString())){
                    if(Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$", email.getText().toString())){
                        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$", contraseña.getText().toString())){
                            //Registro
                            db.collection("pacientes").whereEqualTo("correo_electronico", email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult().isEmpty()){
                                        Map<String, Object> nvoPaciente = new HashMap<>();
                                        nvoPaciente.put("nombre",nombre.getText().toString());
                                        nvoPaciente.put("apellidos", apellidos.getText().toString());
                                        nvoPaciente.put("celular", celular.getText().toString());
                                        nvoPaciente.put("correo_electronico", email.getText().toString());
                                        nvoPaciente.put("contraseña", contraseña.getText().toString());
                                        nvoPaciente.put("direccion", direccion.getText().toString());
                                        db.collection("pacientes").add(nvoPaciente).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(innerView.getContext(), "Se han guardado tus datos. Ahora inicia sesión", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(RegistrarPacienteActivity.this, MainActivity.class);
                                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                RegistrarPacienteActivity.this.startActivity(intent);
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Correo electrónico no válido, use uno distinto", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Snackbar.make(view, "Use una mayúscula, un caracter especial y un número en su contraseña", Snackbar.LENGTH_LONG)
                                    .setAction("Mensaje de error", null).show();
                        }
                    }else{
                        Snackbar.make(view, "Email incorrecto (ejemplo@dominio.com)", Snackbar.LENGTH_LONG)
                                .setAction("Mensaje de error", null).show();
                    }
                }else{
                    Snackbar.make(view, "Apellidos incorrectos, use sólo letras", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje de error", null).show();
                }
            }else{
                Snackbar.make(view, "Nombre incorrecto, use sólo letras", Snackbar.LENGTH_LONG)
                        .setAction("Mensaje de error", null).show();
            }
        }
    };
    View.OnClickListener regresar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(RegistrarPacienteActivity.this, MainActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            RegistrarPacienteActivity.this.startActivity(intent);
        }
    };
}