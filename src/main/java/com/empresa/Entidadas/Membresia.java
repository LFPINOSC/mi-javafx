/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.Entidadas;

/**
 *
 * @author lfpin
 */
public class Membresia {
    private String codigo;
    private String nombre;
    private double valor;
    private int estado;

    public Membresia(String codigo, String nombre, double valor, int estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.valor = valor;
        this.estado = estado;
    }

    public Membresia() {
    }

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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
}
