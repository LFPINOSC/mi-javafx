/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.Entidadas;

/**
 *
 * @author lfpin
 */
public class Servicio {
    private String codigo;
    private String nombre;
    private int estado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Servicio() {
    }

    public Servicio(String codigo, String nombre, int estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
    }
    
}
