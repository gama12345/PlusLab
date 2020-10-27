package com.example.pluslab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        agregarBtnAccion();
        configurarBtnFlotante();
    }

    void agregarBtnAccion(){
        Button btnMisdatos = findViewById(R.id.btnMisdatos);
        Button btnServicios = findViewById(R.id.btnServicios);
        Button btnCitas = findViewById(R.id.btnCitas);
        if(InicioSesionActivity.tipoUsuario.equals("Administrador")){
            btnMisdatos.setOnClickListener(irDatosAdmin);
            btnServicios.setOnClickListener(irServiciosAdmin);
        }else{
            btnMisdatos.setOnClickListener(irDatosPaciente);
            btnServicios.setOnClickListener(irServiciosPaciente);
        }
    }

    void configurarBtnFlotante(){
        FloatingActionButton salir = findViewById(R.id.floatingActionButton);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperSQLite helper = new HelperSQLite(MenuPrincipal.this,"SQLite", null, 1);
                SQLiteDatabase bd = helper.getWritableDatabase();
                String[] args = {MainActivity.emailUsuario};
                bd.delete("usuario","correo_electronico = ?",args);
                Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MenuPrincipal.this.startActivity(intent);
            }
        });
    }

    //ActionListeners
    View.OnClickListener irDatosAdmin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenuPrincipal.this, MisdatosAdminActivity.class);
            MenuPrincipal.this.startActivity(intent);
        }
    };
    View.OnClickListener irServiciosAdmin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenuPrincipal.this, ServiciosAdminActivity.class);
            MenuPrincipal.this.startActivity(intent);
        }
    };
    View.OnClickListener irDatosPaciente = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenuPrincipal.this, MisdatosPacienteActivity.class);
            MenuPrincipal.this.startActivity(intent);
        }
    };
    View.OnClickListener irServiciosPaciente = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenuPrincipal.this, ServiciosPacienteActivity.class);
            MenuPrincipal.this.startActivity(intent);
        }
    };
}