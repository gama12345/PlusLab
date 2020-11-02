package com.example.pluslab;

public class Cita {
    String paciente, servicio, fecha, hora, acompañante, estado, prioridad;

    public Cita() {
    }

    public Cita(String paciente, String servicio, String fecha, String hora, String acompañante, String estado, String prioridad) {
        this.paciente = paciente;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.acompañante = acompañante;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getAcompañante() {
        return acompañante;
    }

    public void setAcompañante(String acompañante) {
        this.acompañante = acompañante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
