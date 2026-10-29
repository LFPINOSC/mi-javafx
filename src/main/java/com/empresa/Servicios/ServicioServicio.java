package com.empresa.Servicios;

import com.empresa.Entidadas.Servicio;
import com.empresa.bd.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD para la tabla public.servicio. */
public class ServicioServicio {

    public boolean insertar(Servicio servicio) {
        String sql = """
            INSERT INTO public.servicio (codigo, nombre, estado)
            VALUES (?, ?, ?)
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setInt(3, servicio.getEstado());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al insertar servicio: " + e.getMessage());
            return false;
        }
    }

    public List<Servicio> listar() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = """
            SELECT codigo, nombre, estado
            FROM public.servicio
            ORDER BY nombre
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios: " + e.getMessage());
        }
        return servicios;
    }

    public Servicio buscarPorCodigo(String codigo) {
        String sql = """
            SELECT codigo, nombre, estado
            FROM public.servicio
            WHERE codigo = ?
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearServicio(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar servicio: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Servicio servicio) {
        String sql = """
            UPDATE public.servicio
            SET nombre = ?, estado = ?
            WHERE codigo = ?
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setInt(2, servicio.getEstado());
            ps.setString(3, servicio.getCodigo());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al actualizar servicio: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM public.servicio WHERE codigo = ?";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }

    private Servicio mapearServicio(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getInt("estado")
        );
    }
}
