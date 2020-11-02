package com.example.pluslab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CitasAdaptador extends RecyclerView.Adapter<CitasAdaptador.CitasViewHolder> {

    ArrayList<Cita> citas;
    static Activity actividad;
    static Context contexto;

    public CitasAdaptador(ArrayList<Cita> citas, Activity parentActivity) {
        this.citas = citas;
        actividad = parentActivity;
        contexto = parentActivity;
    }

    @NonNull
    @Override
    public CitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenedor_datos_cita, parent, false);
        return new CitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CitasViewHolder holder, int position) {
        final Cita citaActual = citas.get(position);
        FirebaseFirestore.getInstance().collection("pacientes").whereEqualTo("correo_electronico", citaActual.getPaciente()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       holder.pacienteCita.setText(task.getResult().getDocuments().get(0).get("nombre")+" "+task.getResult().getDocuments().get(0).get("apellidos"));
                       holder.servicioCita.setText("Servicio: "+citaActual.getServicio());
                       holder.estadoCita.setText("Estado: "+citaActual.getEstado());
                       holder.fechaCita.setText("Fecha: "+citaActual.getFecha());

                       View.OnClickListener click = new View.OnClickListener() {
                           public void onClick(View v) {
                               Intent intent;
                               if(InicioSesionActivity.tipoUsuario.equals("Administrador") || !citaActual.getEstado().equals("en agenda")) {
                                   intent = new Intent(actividad, CitasEditarActivity.class);
                               }else{
                                   intent = new Intent(actividad, CitasEditarActivity.class);
                               }
                               intent.putExtra("fechaCita", citaActual.getFecha());
                               intent.putExtra("horaCita", citaActual.getHora());
                               intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                               actividad.startActivity(intent);
                           }
                       };
                       if (!citaActual.getEstado().equals("cancelada")) {
                           holder.cardView.setOnClickListener(click);
                       }
                   }
               });


    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    //-viewHolder
    public class CitasViewHolder extends RecyclerView.ViewHolder{
        TextView pacienteCita, servicioCita, estadoCita, fechaCita;
        CardView cardView;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);
            pacienteCita = itemView.findViewById(R.id.datoPacienteCita);
            servicioCita = itemView.findViewById(R.id.datoServicioCita);
            estadoCita = itemView.findViewById(R.id.datoEstadoCita);
            fechaCita = itemView.findViewById(R.id.datoFechaCita);
            cardView = itemView.findViewById(R.id.cardView_cita);
        }
    }
}
