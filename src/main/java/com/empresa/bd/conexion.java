/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.empresa.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author lfpin
 */
public class conexion {
    private static final String urlBase="jdbc:postgresql://localhost:5432/gym2";
    private static final String usuario = "postgres";
    private static final String clave = "admin";
    
    public static Connection conectar() throws SQLException{
        return DriverManager.getConnection(urlBase,usuario,clave);
    }
}
