/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.Entidadas;

/**
 *
 * @author lfpin
 */
public class Cliente extends Persona{
    private String codigo;
    private double talla;
    private String sexo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getTalla() {
        return talla;
    }

    public void setTalla(double talla) {
        this.talla = talla;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Cliente(String codigo, double talla, String sexo) {
        this.codigo = codigo;
        this.talla = talla;
        this.sexo = sexo;
    }

    public Cliente(String codigo, double talla, String sexo, String cedula, String nombre, String apellido, String telefono, String correo) {
        super(cedula, nombre, apellido, telefono, correo);
        this.codigo = codigo;
        this.talla = talla;
        this.sexo = sexo;
    }
    public Cliente(){
        super();
    }
    
}
