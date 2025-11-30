package Dao;

import Modelo.Solicitud;
import Patrones.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    public List<Solicitud> listarPendientes() {
        List<Solicitud> lista = new ArrayList<>();
        
        // SQL MEJORADO: Trae todo + el nombre del cliente
        String sql = "SELECT s.id_solicitud, s.id_cliente, s.monto, s.tipo_credito, " +
                     "s.estado, s.fecha_solicitud, c.nombre AS nombre_cliente " +
                     "FROM solicitud_credito s " +
                     "INNER JOIN cliente c ON s.id_cliente = c.id_cliente " + // OJO: singular 'cliente'
                     "WHERE s.estado = 'PENDIENTE'";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Solicitud s = new Solicitud();
                // Mapeo exacto de la tabla
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdCliente(rs.getInt("id_cliente"));
                s.setMonto(rs.getDouble("monto"));
                s.setTipoCredito(rs.getString("tipo_credito"));
                s.setEstado(rs.getString("estado"));
                s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                
                // El dato extra del JOIN
                s.setNombreCliente(rs.getString("nombre_cliente"));
                
                lista.add(s);
            }
        } catch (Exception e) {
            System.out.println("Error listar solicitudes: " + e.getMessage());
        }
        return lista;
    }
    
    // --- NUEVO: Método para que el Cliente registre la solicitud ---
    public boolean registrarSolicitud(Solicitud s) {
        String sql = "INSERT INTO solicitudes_credito (id_cliente, monto, tipo_credito, estado, fecha_solicitud) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, s.getIdCliente());
            ps.setDouble(2, s.getMonto());
            ps.setString(3, s.getTipoCredito());
            // Aquí usamos el getter del Patrón State (obtiene "PENDIENTE")
            ps.setString(4, s.getEstado()); 
            ps.setDate(5, s.getFechaSolicitud());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error registrando solicitud: " + e.getMessage());
            return false;
        }
    }

    // --- NUEVO: Método auxiliar para buscar una solicitud por ID ---
    public Solicitud obtenerPorId(int id) {
        Solicitud s = null;
        String sql = "SELECT * FROM solicitudes_credito WHERE id_solicitud = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s = new Solicitud();
                    s.setIdSolicitud(rs.getInt("id_solicitud"));
                    s.setIdCliente(rs.getInt("id_cliente"));
                    s.setMonto(rs.getDouble("monto"));
                    s.setTipoCredito(rs.getString("tipo_credito"));
                    s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                    
                    // IMPORTANTE: Al cargar desde BD, seteamos el estado
                    // Esto activará el switch en tu Modelo para crear el Objeto State correcto
                    s.setEstado(rs.getString("estado"));
                    
                    // Si ya tiene analista asignado
                    s.setIdAnalista(rs.getInt("id_analista"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo solicitud: " + e.getMessage());
        }
        return s;
    }

    // --- NUEVO: Método para guardar la Aprobación/Rechazo del Analista ---
    public boolean actualizarEstado(Solicitud s) {
        String sql = "UPDATE solicitudes_credito SET estado = ?, id_analista = ? WHERE id_solicitud = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Obtenemos el String del estado actual (APROBADO/RECHAZADO)
            ps.setString(1, s.getEstado()); 
            ps.setInt(2, s.getIdAnalista());
            ps.setInt(3, s.getIdSolicitud());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }
}