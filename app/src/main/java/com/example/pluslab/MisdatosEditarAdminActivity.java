package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MisdatosEditarAdminActivity extends AppCompatActivity {
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misdatos_editar_admin_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        view = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Editar datos");
        agregarBtnAccion();
        agregarTextoSpinners();
        mostrarDatos();
    }
    void agregarBtnAccion(){
        Button btnGuardar = findViewById(R.id.btn_editar_guardar);
        Button btnCancelar = findViewById(R.id.btn_editar_cancelar);
        btnGuardar.setOnClickListener(guardar);
        btnCancelar.setOnClickListener(cancelar);
    }
    void agregarTextoSpinners(){
        Spinner dia1 = (Spinner)findViewById(R.id.spinner_dia_inicio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dia1.setAdapter(adapter);
        Spinner dia2 = findViewById(R.id.spinner_dia_fin);
        dia2.setAdapter(adapter);
    }
    void mostrarDatos(){
        final EditText razon_social = findViewById(R.id.input_razon_social);
        final EditText email = findViewById(R.id.input_correo_electronico);
        final EditText contraseña = findViewById(R.id.input_contraseña);
        final EditText celular = findViewById(R.id.input_celular);
        final Spinner diaInicio = findViewById(R.id.spinner_dia_inicio);
        final Spinner diaFin = findViewById(R.id.spinner_dia_fin);
        final EditText hora1 = findViewById(R.id.input_hora1);
        hora1.setInputType(InputType.TYPE_NULL);
        final EditText hora2 = findViewById(R.id.input_hora2);
        hora2.setInputType(InputType.TYPE_NULL);
        hora1.setOnClickListener(seleccionarHora1);
        hora2.setOnClickListener(seleccionarHora2);
        final EditText direccion = findViewById(R.id.input_direccion);
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    razon_social.setText(task.getResult().get("razon_social").toString());
                    email.setText(task.getResult().get("correo_electronico").toString());
                    contraseña.setText(task.getResult().get("contraseña").toString());
                    celular.setText(task.getResult().get("celular").toString());
                    String atencion = task.getResult().get("dias_atencion").toString();
                    String[] dias = atencion.split("-");
                    for(int i= 0; i < diaInicio.getAdapter().getCount(); i++){
                        if(diaInicio.getAdapter().getItem(i).toString().contains(dias[0])){
                            diaInicio.setSelection(i);
                        }
                    }
                    for(int i= 0; i < diaFin.getAdapter().getCount(); i++){
                        if(diaFin.getAdapter().getItem(i).toString().contains(dias[1])){
                            diaFin.setSelection(i);
                        }
                    }
                    String horario = task.getResult().get("horario").toString();
                    String[] horas = horario.split("-");
                    hora1.setText(horas[0]);
                    hora2.setText(horas[1]);
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
            final EditText razon_social = findViewById(R.id.input_razon_social);
            final EditText email = findViewById(R.id.input_correo_electronico);
            final EditText contraseña = findViewById(R.id.input_contraseña);
            final EditText celular = findViewById(R.id.input_celular);
            final Spinner diaInicio = findViewById(R.id.spinner_dia_inicio);
            final Spinner diaFin = findViewById(R.id.spinner_dia_fin);
            final EditText hora1 = findViewById(R.id.input_hora1);
            final EditText hora2 = findViewById(R.id.input_hora2);
            final EditText direccion = findViewById(R.id.input_direccion);
            if(Pattern.matches("^([A-ZÁ-Úa-zá-ú0-9]+\\s{0,1}[(A-ZÁ-Úa-zá-ú0-9)]+)+$", razon_social.getText().toString())){
                if(Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$", email.getText().toString())){
                    if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&\\*]).{6,}$", contraseña.getText().toString())){
                        if(diaInicio.getSelectedItemId() < diaFin.getSelectedItemId()){
                            String horaInicio = hora1.getText().toString().substring(0,2);
                            String horaFin = hora2.getText().toString().substring(0,2);
                            if(Integer.parseInt(horaInicio) < Integer.parseInt(horaFin)){
                                if(Pattern.matches("^(Calle)\\s[\\wÁÉÍÓÚáéíóú\\s\\.]{1,30}(\\s\\#\\d{0,3}\\s){0,1}(Colonia)\\s[\\wÁÉÍÓÚáéíóú\\s\\.]{1,30}(C)\\.(P)\\.\\s[\\d]{5}$", direccion.getText().toString())){
                                    //actualiza datos
                                    Map<String, Object> admin = new HashMap<>();
                                    admin.put("razon_social", razon_social.getText().toString());
                                    admin.put("correo_electronico", email.getText().toString());
                                    admin.put("contraseña", contraseña.getText().toString());
                                    admin.put("celular", celular.getText().toString());
                                    admin.put("dias_atencion", diaInicio.getSelectedItem().toString()+"-"+diaFin.getSelectedItem().toString());
                                    admin.put("horario", hora1.getText().toString()+"-"+hora2.getText().toString());
                                    admin.put("direccion", direccion.getText().toString());
                                    InicioSesionActivity.usuarioLogeado.update(admin);
                                    //Regresa
                                    Toast.makeText(innerView.getContext(), "Se han actualizado tus datos", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MisdatosEditarAdminActivity.this, MisdatosAdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MisdatosEditarAdminActivity.this.startActivity(intent);
                                }else{
                                    Snackbar.make(view, "Formato de dirección no válido. Ejemplo 'Calle Independencia #3 Colonia Centro de la colonia C.P. 12345'", Snackbar.LENGTH_LONG)
                                            .setAction("Mensaje de error", null).show();
                                }
                            }else{
                                Snackbar.make(view, "Horario de atención incorrecto. Hora de inicio debe ser menor a hora termino", Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje de error", null).show();
                            }
                        }else{
                            Snackbar.make(view, "Dias de atención incorrecto, el primer dia debe ser anterior al segundo", Snackbar.LENGTH_LONG)
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
                    Snackbar.make(view, "Razón social incorrecta, use sólo letras y digitos", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje de error", null).show();
            }

        }
    };
    View.OnClickListener cancelar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MisdatosEditarAdminActivity.this, MisdatosAdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MisdatosEditarAdminActivity.this.startActivity(intent);
        }
    };
    View.OnClickListener seleccionarHora1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar horaActual = Calendar.getInstance();
            int hour = horaActual.get(Calendar.HOUR_OF_DAY);
            int minute = horaActual.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MisdatosEditarAdminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    EditText hora = findViewById(R.id.input_hora1);
                    hora.setText( selectedHour + ":" + "00");
                }
            }, 0, 0, true);
            mTimePicker.setTitle("Seleccione la hora");
            mTimePicker.show();
        }
    };
    View.OnClickListener seleccionarHora2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar horaActual = Calendar.getInstance();
            int hour = horaActual.get(Calendar.HOUR_OF_DAY);
            int minute = horaActual.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MisdatosEditarAdminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    EditText hora = findViewById(R.id.input_hora2);
                    hora.setText( selectedHour + ":" + "00");
                }
            }, hour, minute, true);

            mTimePicker.setTitle("Seleccione la hora");
            mTimePicker.show();
        }
    };
}