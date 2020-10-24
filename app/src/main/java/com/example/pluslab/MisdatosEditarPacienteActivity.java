package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MisdatosEditarPacienteActivity extends AppCompatActivity {
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misdatos_editar_paciente_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        view = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Editar datos");
        agregarBtnAccion();
        mostrarDatos();
    }
    void agregarBtnAccion(){
        Button btnGuardar = findViewById(R.id.btn_editar_guardar_paciente);
        Button btnCancelar = findViewById(R.id.btn_editar_cancelar_paciente);
        btnGuardar.setOnClickListener(guardar);
        btnCancelar.setOnClickListener(cancelar);
    }

    void mostrarDatos(){
        final EditText nombre = findViewById(R.id.input_nombre_paciente);
        final EditText apellidos = findViewById(R.id.input_apellidos_paciente);
        final EditText email = findViewById(R.id.input_correo_electronico_paciente);
        final EditText contraseña = findViewById(R.id.input_contraseña_paciente);
        final EditText celular = findViewById(R.id.input_celular_paciente);
        final EditText direccion = findViewById(R.id.input_direccion_paciente);
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    nombre.setText(task.getResult().get("nombre").toString());
                    apellidos.setText(task.getResult().get("apellidos").toString());
                    email.setText(task.getResult().get("correo_electronico").toString());
                    contraseña.setText(task.getResult().get("contraseña").toString());
                    celular.setText(task.getResult().get("celular").toString());
                    direccion.setText(task.getResult().get("direccion").toString());
                }else{
                    Snackbar.make(view, "Ha ocurrido un error al comunicarse con el servidor", Snackbar.LENGTH_LONG).setAction("Mensaje de error", null).show();
                }
            }
        });
    }

    //ActionListeners
    View.OnClickListener guardar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View innerView = view;
            //Validando datos
            final EditText nombre = findViewById(R.id.input_nombre_paciente);
            final EditText apellidos = findViewById(R.id.input_apellidos_paciente);
            final EditText email = findViewById(R.id.input_correo_electronico_paciente);
            final EditText contraseña = findViewById(R.id.input_contraseña_paciente);
            final EditText celular = findViewById(R.id.input_celular_paciente);
            final EditText direccion = findViewById(R.id.input_direccion_paciente);
            if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", nombre.getText().toString())){
                if(Pattern.matches("^([A-ZÁ-Úa-zá-ú]+\\s{0,1}[(A-ZÁ-Úa-zá-ú)]+)+$", apellidos.getText().toString())){
                    if(Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$", email.getText().toString())){
                        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$", contraseña.getText().toString())){
                            if(Pattern.matches("^(Calle)\\s[\\wÁÉÍÓÚáéíóú\\s\\.]{1,30}(\\s\\#\\d{0,3}\\s){0,1}(Colonia)\\s[\\wÁÉÍÓÚáéíóú\\s\\.]{1,30}(C)\\.(P)\\.\\s[\\d]{5}$", direccion.getText().toString())){
                                //actualiza datos
                                Map<String, Object> admin = new HashMap<>();
                                admin.put("nombre", nombre.getText().toString());
                                admin.put("apellidos", apellidos.getText().toString());
                                admin.put("correo_electronico", email.getText().toString());
                                admin.put("contraseña", contraseña.getText().toString());
                                admin.put("celular", celular.getText().toString());
                                admin.put("direccion", direccion.getText().toString());
                                InicioSesionActivity.usuarioLogeado.update(admin);
                                //Regresa
                                Toast.makeText(innerView.getContext(), "Se han actualizado tus datos", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MisdatosEditarPacienteActivity.this, MisdatosPacienteActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                MisdatosEditarPacienteActivity.this.startActivity(intent);
                            }else{
                                Snackbar.make(view, "Formato de dirección no válido. Ejemplo 'Calle Independencia #3 Colonia Centro de la colonia C.P. 12345'", Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje de error", null).show();
                            }
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
    View.OnClickListener cancelar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MisdatosEditarPacienteActivity.this, MisdatosPacienteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MisdatosEditarPacienteActivity.this.startActivity(intent);
        }
    };
}