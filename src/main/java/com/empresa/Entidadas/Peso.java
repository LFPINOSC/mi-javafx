/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.Entidadas;

import java.util.Date;

/**
 *
 * @author lfpin
 */
public class Peso {
    private Date fecha;
    private double valor;
    private Cliente cliente;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Peso() {
    }

    public Peso(Date fecha, double valor, Cliente cliente) {
        this.fecha = fecha;
        this.valor = valor;
        this.cliente = cliente;
    }
    
}
