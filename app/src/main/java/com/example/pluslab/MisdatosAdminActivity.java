package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import static android.graphics.Color.WHITE;

public class MisdatosAdminActivity extends AppCompatActivity {
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misdatos_admin_activity);
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
        final TextView razon_social = findViewById(R.id.dato_razon_social);
        final TextView email = findViewById(R.id.dato_correo_electronico);
        final TextView contraseña = findViewById(R.id.dato_contraseña);
        final TextView celular = findViewById(R.id.dato_celular);
        final TextView dias = findViewById(R.id.dato_dias);
        final TextView horario = findViewById(R.id.dato_horario);
        final TextView direccion = findViewById(R.id.dato_direccion);
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    razon_social.setText("Razón social: "+task.getResult().get("razon_social"));
                    email.setText("Correo: "+task.getResult().get("correo_electronico"));
                    contraseña.setText("Contraseña: ********");
                    celular.setText("Celular: "+task.getResult().get("celular"));
                    dias.setText("Abierto: "+task.getResult().get("dias_atencion"));
                    horario.setText("Horario: "+task.getResult().get("horario"));
                    direccion.setText("Dirección: "+task.getResult().get("direccion"));
                }else{
                    Snackbar.make(view, "Ha ocurrido un error al comunicarse con el servidor", Snackbar.LENGTH_LONG).setAction("Mensaje de error", null).show();
                }
            }
        });
    }
    void agregarBtnAccion(){
        Button btn = findViewById(R.id.btn_modificar_datos);
        btn.setOnClickListener(irEditar);
    }

    //ActionListeners
    View.OnClickListener irEditar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MisdatosAdminActivity.this, MisdatosEditarAdminActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MisdatosAdminActivity.this.startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MisdatosAdminActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
