package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

public class MisdatosPacienteActivity extends AppCompatActivity {
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misdatos_paciente_activity);
        view = findViewById(R.id.toolbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Mis datos");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mostrarDatos();
        agregarBtnAccion();
    }
    void mostrarDatos(){
        final TextView nombre = findViewById(R.id.dato_nombre_paciente);
        final TextView apellidos = findViewById(R.id.dato_apellidos_paciente);
        final TextView email = findViewById(R.id.dato_correo_electronico_paciente);
        final TextView contraseña = findViewById(R.id.dato_contraseña_paciente);
        final TextView celular = findViewById(R.id.dato_celular_paciente);
        final TextView direccion = findViewById(R.id.dato_direccion_paciente);
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    nombre.setText("Nombre: "+task.getResult().get("nombre").toString());
                    apellidos.setText("Apellidos: "+task.getResult().get("apellidos").toString());
                    email.setText("Correo: "+task.getResult().get("correo_electronico").toString());
                    contraseña.setText("Contraseña: ********");
                    celular.setText("Celular: "+task.getResult().get("celular").toString());
                    direccion.setText("Dirección: "+task.getResult().get("direccion").toString());
                }else{
                    Snackbar.make(view, "Ha ocurrido un error al comunicarse con el servidor", Snackbar.LENGTH_LONG).setAction("Mensaje de error", null).show();
                }
            }
        });
    }
    void agregarBtnAccion(){
        Button btn = findViewById(R.id.btn_modificar_datos_paciente);
        btn.setOnClickListener(irEditar);
    }

    //ActionListeners
    View.OnClickListener irEditar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MisdatosPacienteActivity.this, MisdatosEditarPacienteActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MisdatosPacienteActivity.this.startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MisdatosPacienteActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}