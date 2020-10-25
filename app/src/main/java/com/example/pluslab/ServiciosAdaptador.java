package com.example.pluslab;

import android.app.Activity;
import android.content.Context;
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
        Servicio servicioActual = servicios.get(position);

        Log.d("Holaaaaaaaaa", ""+servicios.size());
        holder.nombreServicio.setText(servicioActual.getNombre());
        holder.categoriaServicio.setText("Categoria: "+servicioActual.getCategoria());
        holder.costoServicio.setText("Costo: "+servicioActual.getCosto());
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
