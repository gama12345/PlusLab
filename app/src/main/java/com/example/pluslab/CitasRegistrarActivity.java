package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CitasRegistrarActivity extends AppCompatActivity {
    String emailPaciente;
    static String fechaFormatoDb;
    String horarioInicio, horarioFin;
    Activity currentActivity;
    static EditText fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citas_registrar_activity);

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Mis citas");

        //Configurando fecha
        fecha = findViewById(R.id.input_fechaCita);
        fecha.setOnClickListener(mostrarFecha);
        configurarBtnAccion();
        prepararDatos();
    }

    void configurarBtnAccion(){
        Button btnRegistrar = findViewById(R.id.btnRegistrarCita);
        btnRegistrar.setOnClickListener(guardar);
        Button btnCancelar = findViewById(R.id.btnCancelarRegistroCita);
        btnCancelar.setOnClickListener(cancelar);
    }

    void prepararDatos(){
        //Colocando nombre paciente
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                EditText nombre = findViewById(R.id.input_nombreCita);
                nombre.setText(task.getResult().get("nombre").toString()+" "+task.getResult().get("apellidos").toString());
                emailPaciente = task.getResult().get("correo_electronico").toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("administrador").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String[] atencion = task.getResult().getDocuments().get(0).get("horario").toString().split("-");
                        horarioInicio = atencion[0]; horarioFin = atencion[1];

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        //Rellenando spinner de servicios
                        db.collection("analisis").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<String> spinnerArray =  new ArrayList<String>();
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    spinnerArray.add(document.get("nombre").toString());
                                }
                                Spinner servicios = findViewById(R.id.input_servicioCita);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, spinnerArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                servicios.setAdapter(adapter);

                                //Configurando hora
                                Spinner hora = findViewById(R.id.input_horaCita);
                                ArrayList<String> spinnerArrayHoras =  new ArrayList<String>();
                                int horaAux = Integer.parseInt(horarioInicio.substring(0,2));
                                int horafinNum = Integer.parseInt(horarioFin.substring(0,2));
                                while(horaAux <= horafinNum){
                                    if(horaAux < 10){
                                        spinnerArrayHoras.add("0"+horaAux+":00");
                                    }else {
                                        spinnerArrayHoras.add(horaAux + ":00");
                                    }
                                    horaAux++;
                                }
                                ArrayAdapter<String> adapterHoras = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, spinnerArrayHoras);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                hora.setAdapter(adapterHoras);
                            }
                        });
                    }
                });
            }
        });
    }

    View.OnClickListener guardar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            //Validando datos...
            try{
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendario = Calendar.getInstance();
                calendario.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                Date today = formato.parse(formato.format(calendario.getTime()));
                Log.d("FECHA ",today.toString());
                if(fechaFormatoDb == null){
                    fechaFormatoDb = formato.format(calendario.getTime());
                }
                if (today.compareTo(formato.parse(fechaFormatoDb)) < 0) {
                    //Validar hora
                    final Spinner horaCita = findViewById(R.id.input_horaCita);
                    FirebaseFirestore.getInstance().collection("citas").whereEqualTo("fecha", fechaFormatoDb).whereEqualTo("hora",horaCita.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().isEmpty()){
                                Spinner spinnerServicio = findViewById(R.id.input_servicioCita);
                                EditText acompañante = findViewById(R.id.input_acompañanteCita);
                                Map<String, Object> nvaCita = new HashMap<>();
                                nvaCita.put("paciente", emailPaciente);
                                nvaCita.put("servicio", spinnerServicio.getSelectedItem().toString());
                                nvaCita.put("fecha", fechaFormatoDb);
                                nvaCita.put("hora", horaCita.getSelectedItem().toString());
                                nvaCita.put("acompañante", acompañante.getText().toString());
                                nvaCita.put("estado", "en agenda");
                                nvaCita.put("prioridad", "2");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("citas").add(nvaCita).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(innerView.getContext(), "Cita registrada", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CitasRegistrarActivity.this, CitasPacienteActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        CitasRegistrarActivity.this.startActivity(intent);
                                    }
                                });
                            }else{
                                Snackbar.make(innerView, "Este horario no se encuentra disponible en "+fechaFormatoDb, Snackbar.LENGTH_LONG)
                                        .setAction("Mensaje de error", null).show();
                            }
                        }
                    });
                }else{
                    Snackbar.make(view, "Fecha incorrecta, agende una cita en una fecha posterior", Snackbar.LENGTH_LONG)
                            .setAction("Mensaje de error", null).show();
                }
            }catch(ParseException ex){
                ex.printStackTrace();
            }
        }
    };
    View.OnClickListener cancelar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CitasRegistrarActivity.this, CitasPacienteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            CitasRegistrarActivity.this.startActivity(intent);
        }
    };

    View.OnClickListener mostrarFecha = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newDate = new DatePickerFragment();
            newDate.show(getSupportFragmentManager(), "datePicker");
        }
    };
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            c.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String formatoFecha = "%1$02d";
            fechaFormatoDb = String.format(formatoFecha, day)+"-"+String.format(formatoFecha, month+1)+"-"+year;
            SimpleDateFormat formato = new SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", new Locale("es","MX"));
            SimpleDateFormat formato2 = new SimpleDateFormat("dd-MM-yyyy", new Locale("es","MX"));
            try {
                fecha.setText(formato.format(formato2.parse(fechaFormatoDb)));
            }catch(ParseException ex){
                ex.printStackTrace();
            }
        }
    }
}