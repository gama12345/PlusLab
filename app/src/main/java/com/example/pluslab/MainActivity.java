package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    static String emailUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Si un usuario esta logeado ya no pedimos sus datos
        HelperSQLite helper = new HelperSQLite(MainActivity.this,"SQLite", null, 1);
        SQLiteDatabase bd = helper.getWritableDatabase();
        String[] email = {"correo_electronico"};
        Cursor consulta = bd.query("usuario",email,
                null,null,null,null,null);

        if(!consulta.moveToFirst()) {
            agregarBtnAccion();
        }else{
            emailUsuario = consulta.getString(0);
            FirebaseFirestore.getInstance().collection("pacientes").whereEqualTo("correo_electronico",consulta.getString(0)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                            InicioSesionActivity.pacienteLogeado = document.getReference();
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            MainActivity.this.startActivity(intent);
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
