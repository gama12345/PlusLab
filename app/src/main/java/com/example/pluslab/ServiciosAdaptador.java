package com.example.pluslab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServiciosAdaptador extends RecyclerView.Adapter<ServiciosAdaptador.ServiciosViewHolder> {

    ArrayList<Servicio> servicios;
    static Activity actividad;
    static Context contexto;

    public ServiciosAdaptador(ArrayList<Servicio> servicios, Activity parentActivity) {
        this.servicios = servicios;
        actividad = parentActivity;
        contexto = parentActivity;
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenedor_datos_servicio, parent, false);
        return new ServiciosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiciosViewHolder holder, int position) {
        final Servicio servicioActual = servicios.get(position);
        holder.nombreServicio.setText(servicioActual.getNombre());
        holder.categoriaServicio.setText("Categoria: "+servicioActual.getCategoria());
        holder.costoServicio.setText("Costo: "+servicioActual.getCosto());

        View.OnClickListener click = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                if(InicioSesionActivity.tipoUsuario.equals("Administrador")) {
                    intent = new Intent(actividad, ServiciosEditarActivity.class);
                }else{
                    intent = new Intent(actividad, ServiciosDetallesActivity.class);
                }
                intent.putExtra("nombreServicio", servicioActual.getNombre());
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                actividad.startActivity(intent);
            }
        };
        holder.nombreServicio.setOnClickListener(click);
        holder.categoriaServicio.setOnClickListener(click);
        holder.costoServicio.setOnClickListener(click);

    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    //-viewHolder
    public class ServiciosViewHolder extends RecyclerView.ViewHolder{
        TextView nombreServicio, categoriaServicio, costoServicio;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreServicio = itemView.findViewById(R.id.tituloNombreServicio);
            categoriaServicio = itemView.findViewById(R.id.datoCategoria);
            costoServicio = itemView.findViewById(R.id.datoCosto);
        }
    }
}
