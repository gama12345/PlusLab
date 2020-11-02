package com.example.pluslab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CitasEditarActivity extends AppCompatActivity {
    String horaCita, fechaCita;
    static String fechaFormatoDb;
    String horarioInicio, horarioFin;
    Activity currentActivity;
    static EditText fechaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citas_editar_activity);

        currentActivity = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Detalles de cita");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Configurando fecha
        fechaEditar = findViewById(R.id.input_fechaCitaEditar);
        fechaEditar.setOnClickListener(mostrarFecha);

        Intent intent = getIntent();
        fechaCita = (intent.getStringExtra("fechaCita"));
        horaCita = (intent.getStringExtra("horaCita"));

        agregarBtnAccion();
        prepararDatos();
    }
    void agregarBtnAccion(){
        Button guardar = findViewById(R.id.btn_guardarCitaEditar);
        guardar.setOnClickListener(guardarCambios);
        Button cancelar = findViewById(R.id.btn_cancelarCita);
        cancelar.setOnClickListener(cancelarCita);
    }
    void prepararDatos(){
        //Colocando nombre paciente
        InicioSesionActivity.usuarioLogeado.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                EditText nombre = findViewById(R.id.input_pacienteCitaEditar);
                nombre.setText(task.getResult().get("nombre").toString()+" "+task.getResult().get("apellidos").toString());
                //emailPaciente = task.getResult().get("correo_electronico").toString();

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
                                final Spinner servicios = findViewById(R.id.spinner_servicioCitaEditar);
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, spinnerArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                servicios.setAdapter(adapter);

                                //Configurando hora
                                final Spinner hora = findViewById(R.id.spinner_horaCitaEditar);
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
                                final ArrayAdapter<String> adapterHoras = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, spinnerArrayHoras);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                hora.setAdapter(adapterHoras);

                                FirebaseFirestore.getInstance().collection("citas").whereEqualTo("fecha", fechaCita).whereEqualTo("hora", horaCita).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(!task.getResult().isEmpty()){

                                            for(QueryDocumentSnapshot document : task.getResult()) {
                                                servicios.setSelection(adapter.getPosition(document.get("servicio").toString()));

                                                SimpleDateFormat formato = new SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", new Locale("es", "MX"));
                                                SimpleDateFormat formato2 = new SimpleDateFormat("dd-MM-yyyy", new Locale("es", "MX"));
                                                try {
                                                    fechaEditar.setText(formato.format(formato2.parse(document.get("fecha").toString())));
                                                    fechaFormatoDb = fechaCita;
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                hora.setSelection(adapterHoras.getPosition(document.get("hora").toString()));
                                                EditText acompañante = findViewById(R.id.input_acompañanteCitaEditar);
                                                acompañante.setText(document.get("acompañante").toString());
                                                EditText estado = findViewById(R.id.input_estadoCitaEditar);
                                                estado.setText(document.get("estado").toString());
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    View.OnClickListener mostrarFecha = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newDate = new CitasEditarActivity.DatePickerFragment();
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
                fechaEditar.setText(formato.format(formato2.parse(fechaFormatoDb)));
            }catch(ParseException ex){
                ex.printStackTrace();
            }
        }
    }

    View.OnClickListener guardarCambios = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            //Validando datos...
            try{
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendario = Calendar.getInstance();
                calendario.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                Date today = formato.parse(formato.format(calendario.getTime()));
                if (today.compareTo(formato.parse(fechaFormatoDb)) < 0) {
                    //Validar hora
                    final Spinner horaCitaSpi = findViewById(R.id.spinner_horaCitaEditar);
                    FirebaseFirestore.getInstance().collection("citas").whereEqualTo("fecha", fechaFormatoDb).whereEqualTo("hora",horaCitaSpi.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().isEmpty() || (task.getResult().getDocuments().get(0).get("hora").toString().equals(horaCita) && task.getResult().getDocuments().get(0).get("fecha").toString().equals(fechaCita))){

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("citas").whereEqualTo("fecha", fechaCita).whereEqualTo("hora",horaCita).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        Spinner spinnerServicio = findViewById(R.id.spinner_servicioCitaEditar);
                                        EditText acompañante = findViewById(R.id.input_acompañanteCitaEditar);
                                        Map<String, Object> nvaCita = new HashMap<>();
                                        nvaCita.put("paciente", MainActivity.emailUsuario);
                                        nvaCita.put("servicio", spinnerServicio.getSelectedItem().toString());
                                        nvaCita.put("fecha", fechaFormatoDb );
                                        nvaCita.put("hora", horaCitaSpi.getSelectedItem().toString());
                                        nvaCita.put("acompañante", acompañante.getText().toString());
                                        nvaCita.put("estado", "en agenda");
                                        nvaCita.put("prioridad", "2");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().update(nvaCita);
                                        }
                                        Toast.makeText(innerView.getContext(), "Se ha actualizado su cita programda el "+fechaCita+" a las "+horaCita, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CitasEditarActivity.this, CitasPacienteActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        CitasEditarActivity.this.startActivity(intent);
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

    View.OnClickListener cancelarCita = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View innerView = view;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Está seguro de que quiere cancelar su cita?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("citas").whereEqualTo("fecha", fechaCita).whereEqualTo("hora", horaCita).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().update("estado", "cancelada");
                                        document.getReference().update("prioridad", "4");
                                    }
                                    Toast.makeText(innerView.getContext(), "Se ha cancelado su cita programda el "+fechaCita+" a las "+horaCita, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(CitasEditarActivity.this, CitasPacienteActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    CitasEditarActivity.this.startActivity(intent);
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object
            builder.create().show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CitasEditarActivity.this, CitasPacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}