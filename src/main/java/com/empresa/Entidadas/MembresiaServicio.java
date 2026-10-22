/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.Entidadas;

/**
 *
 * @author lfpin
 */
public class MembresiaServicio {
    private Membresia membresia;
    private Servicio servicio;

    public Membresia getMembresia() {
        return membresia;
    }

    public void setMembresia(Membresia membresia) {
        this.membresia = membresia;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public MembresiaServicio() {
    }

    public MembresiaServicio(Membresia membresia, Servicio servicio) {
        this.membresia = membresia;
        this.servicio = servicio;
    }
    
}
