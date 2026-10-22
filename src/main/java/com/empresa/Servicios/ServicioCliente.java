
package com.empresa.Servicios;


import com.empresa.Entidadas.Cliente;
import com.empresa.bd.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioCliente {

    // 1. INSERTAR CLIENTE Y PERSONA
    public boolean insert(Cliente cliente) {
        String sqlPersona = """
            INSERT INTO public.persona
                (cedula, nombre, apellido, telefono, correo)
            VALUES (?, ?, ?, ?, ?)
            """;

        String sqlCliente = """
            INSERT INTO public.cliente
                (codigo, cedula, talla, sexo)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
                    ps.setString(1, cliente.getCedula());
                    ps.setString(2, cliente.getNombre());
                    ps.setString(3, cliente.getApellido());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getCorreo());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                    ps.setString(1, cliente.getCodigo());
                    ps.setString(2, cliente.getCedula());
                    ps.setDouble(3, cliente.getTalla());
                    ps.setString(4, cliente.getSexo());
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // 2. LISTAR TODOS LOS CLIENTES
    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT c.codigo, c.cedula, c.talla, c.sexo,
                   p.nombre, p.apellido, p.telefono, p.correo
            FROM public.cliente c
            INNER JOIN public.persona p ON p.cedula = c.cedula
            ORDER BY p.apellido, p.nombre
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();

                cliente.setCodigo(rs.getString("codigo"));
                cliente.setCedula(rs.getString("cedula"));
                cliente.setTalla(rs.getDouble("talla"));
                cliente.setSexo(rs.getString("sexo"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setCorreo(rs.getString("correo"));

                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    // 3. BUSCAR CLIENTE POR CÓDIGO
    public Cliente buscarPorCodigo(String codigo) {
        String sql = """
            SELECT c.codigo, c.cedula, c.talla, c.sexo,
                   p.nombre, p.apellido, p.telefono, p.correo
            FROM public.cliente c
            INNER JOIN public.persona p ON p.cedula = c.cedula
            WHERE c.codigo = ?
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();

                    cliente.setCodigo(rs.getString("codigo"));
                    cliente.setCedula(rs.getString("cedula"));
                    cliente.setTalla(rs.getDouble("talla"));
                    cliente.setSexo(rs.getString("sexo"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setCorreo(rs.getString("correo"));

                    return cliente;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }

        return null;
    }

    // 4. ACTUALIZAR CLIENTE Y PERSONA
    public boolean actualizar(Cliente cliente) {
        String sqlPersona = """
            UPDATE public.persona
            SET nombre = ?, apellido = ?, telefono = ?, correo = ?
            WHERE cedula = ?
            """;

        String sqlCliente = """
            UPDATE public.cliente
            SET talla = ?, sexo = ?
            WHERE codigo = ?
            """;

        try (Connection con = conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                int personaActualizada;

                try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getApellido());
                    ps.setString(3, cliente.getTelefono());
                    ps.setString(4, cliente.getCorreo());
                    ps.setString(5, cliente.getCedula());

                    personaActualizada = ps.executeUpdate();
                }

                int clienteActualizado;

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                    ps.setDouble(1, cliente.getTalla());
                    ps.setString(2, cliente.getSexo());
                    ps.setString(3, cliente.getCodigo());

                    clienteActualizado = ps.executeUpdate();
                }

                if (personaActualizada == 0 || clienteActualizado == 0) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // 5. ELIMINAR CLIENTE Y SU PERSONA ASOCIADA
    public boolean eliminar(String codigo) {
        String sqlBuscar = """
            SELECT cedula
            FROM public.cliente
            WHERE codigo = ?
            """;

        String sqlCliente = """
            DELETE FROM public.cliente
            WHERE codigo = ?
            """;

        String sqlPersona = """
            DELETE FROM public.persona
            WHERE cedula = ?
            """;

        try (Connection con = conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                String cedula;

                try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                    ps.setString(1, codigo);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        cedula = rs.getString("cedula");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
                    ps.setString(1, cedula);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}
