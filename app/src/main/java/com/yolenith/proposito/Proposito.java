package com.yolenith.proposito;

import java.io.Serializable;

public class Proposito implements Serializable {
    private int id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String hora;
    private int estado;
    private int repetir;

    public Proposito() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getRepetir() {
        return repetir;
    }

    public void setRepetir(int repetir) {
        this.repetir = repetir;
    }
}
