package com.example.pluslab;

public class Servicio {
    private String nombre, categoria, descripcion, indicaciones, costo;

    public Servicio() {
    }

    public Servicio(String nombre, String categoria, String descripcion, String indicaciones, String costo) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.indicaciones = indicaciones;
        this.costo = costo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }
}
