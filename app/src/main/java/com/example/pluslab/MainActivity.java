package com.example.pluslab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        agregarBtnAccion();
    }
    void agregarBtnAccion(){
        Button btnLogin = findViewById(R.id.btn_iniciarSesion);
        Button btnRegistro = findViewById(R.id.btn_registrarse);
        btnLogin.setOnClickListener(login);
        btnRegistro.setOnClickListener(registro);
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
