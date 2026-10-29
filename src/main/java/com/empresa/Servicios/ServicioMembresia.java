package com.empresa.Servicios;

import com.empresa.Entidadas.Membresia;
import com.empresa.bd.conexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Operaciones CRUD para membresia y sus servicios asociados. */
public class ServicioMembresia {

    /** Inserta la membresia y sus relaciones de servicios en una transaccion. */
    public boolean insertar(Membresia membresia, List<String> codigosServicios) {
        if (!datosValidos(membresia)) {
            return false;
        }

        String sql = """
            INSERT INTO public.membresia (codigo, nombre, valor, estado)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, membresia.getCodigo().trim());
                    ps.setString(2, membresia.getNombre().trim());
                    ps.setBigDecimal(3, BigDecimal.valueOf(membresia.getValor()));
                    ps.setInt(4, membresia.getEstado());
                    ps.executeUpdate();
                }
                insertarRelaciones(con, membresia.getCodigo(), codigosServicios);
                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException | RuntimeException e) {
            System.err.println("Error al insertar membresia: " + e.getMessage());
            return false;
        }
    }

    public List<Membresia> listar() {
        List<Membresia> membresias = new ArrayList<>();
        String sql = """
            SELECT codigo, nombre, valor, estado
            FROM public.membresia
            ORDER BY nombre
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                membresias.add(mapearMembresia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar membresias: " + e.getMessage());
        }
        return membresias;
    }

    public Membresia buscarPorCodigo(String codigo) {
        String sql = """
            SELECT codigo, nombre, valor, estado
            FROM public.membresia
            WHERE codigo = ?
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMembresia(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar membresia: " + e.getMessage());
        }
        return null;
    }

    /** Devuelve los códigos de servicio asociados a una membresia. */
    public List<String> listarCodigosServicios(String codigoMembresia) {
        List<String> codigos = new ArrayList<>();
        String sql = """
            SELECT servicio_codigo
            FROM public.membresia_servicio
            WHERE membresia_codigo = ?
            ORDER BY servicio_codigo
            """;

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoMembresia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codigos.add(rs.getString("servicio_codigo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios de la membresia: " + e.getMessage());
        }
        return codigos;
    }

    /** Actualiza la membresia y reemplaza su lista de servicios en una transaccion. */
    public boolean actualizar(Membresia membresia, List<String> codigosServicios) {
        if (!datosValidos(membresia)) {
            return false;
        }

        String sql = """
            UPDATE public.membresia
            SET nombre = ?, valor = ?, estado = ?
            WHERE codigo = ?
            """;

        try (Connection con = conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                int filas;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, membresia.getNombre().trim());
                    ps.setBigDecimal(2, BigDecimal.valueOf(membresia.getValor()));
                    ps.setInt(3, membresia.getEstado());
                    ps.setString(4, membresia.getCodigo().trim());
                    filas = ps.executeUpdate();
                }
                if (filas == 0) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM public.membresia_servicio WHERE membresia_codigo = ?")) {
                    ps.setString(1, membresia.getCodigo().trim());
                    ps.executeUpdate();
                }
                insertarRelaciones(con, membresia.getCodigo(), codigosServicios);
                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException | RuntimeException e) {
            System.err.println("Error al actualizar membresia: " + e.getMessage());
            return false;
        }
    }

    /** Elimina la membresia; PostgreSQL elimina sus relaciones por ON DELETE CASCADE. */
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM public.membresia WHERE codigo = ?";
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al eliminar membresia: " + e.getMessage());
            return false;
        }
    }

    private void insertarRelaciones(Connection con, String codigoMembresia,
                                      List<String> codigosServicios) throws SQLException {
        Set<String> codigosUnicos = codigosServicios == null
                ? Set.of()
                : new LinkedHashSet<>(codigosServicios);
        if (codigosUnicos.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO public.membresia_servicio (membresia_codigo, servicio_codigo)
            VALUES (?, ?)
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String codigoServicio : codigosUnicos) {
                if (codigoServicio == null || codigoServicio.isBlank()) {
                    throw new SQLException("El código de servicio no puede estar vacío.");
                }
                ps.setString(1, codigoMembresia.trim());
                ps.setString(2, codigoServicio.trim());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private boolean datosValidos(Membresia membresia) {
        return membresia != null
                && membresia.getCodigo() != null
                && !membresia.getCodigo().isBlank()
                && membresia.getCodigo().trim().length() <= 20
                && membresia.getNombre() != null
                && !membresia.getNombre().isBlank()
                && membresia.getNombre().trim().length() <= 100
                && Double.isFinite(membresia.getValor())
                && membresia.getValor() >= 0
                && (membresia.getEstado() == 0 || membresia.getEstado() == 1);
    }

    private Membresia mapearMembresia(ResultSet rs) throws SQLException {
        return new Membresia(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("valor").doubleValue(),
                rs.getInt("estado")
        );
    }
}
